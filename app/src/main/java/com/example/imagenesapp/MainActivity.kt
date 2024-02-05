package com.example.imagenesapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.Manifest
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    // Declaraciones de variables
    private lateinit var recyclerView: RecyclerView
    private val imageList = mutableListOf<Uri>()
    private lateinit var adapter: ImageAdapter
    private val REQUEST_CODE_READ_EXTERNAL_STORAGE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)

        // Verificar si el permiso de lectura externa está otorgado
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si el permiso no está otorgado, solicitarlo al usuario
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_READ_EXTERNAL_STORAGE
            )
        } else {
            // Si el permiso está otorgado, cargar la lista de imágenes y configurar el adaptador
            cargarListaImagenes()

            // Obtener nombres y fechas de las fotos
            val fotosInfo = obtenerNombresFechasFotos(this)
            for ((nombre, fecha) in fotosInfo) {
                Log.d("MainActivity", "Nombre: $nombre, Fecha: $fecha")
            }
        }
    }

    // Método para cargar la lista de imágenes y configurar el adaptador
    private fun cargarListaImagenes() {
        // Cargar la lista de URIs de imágenes al iniciar la aplicación
        loadImageListFromSharedPreferences()

        // Inicializar el adaptador del RecyclerView
        adapter = ImageAdapter(
            imageList,
            { imageUri ->
                val position = imageList.indexOf(imageUri)
                onImageClick(imageUri, position)
            },
            { imageUri ->
                val position = imageList.indexOf(imageUri)
                onImageClick(imageUri, position)
            },
            { position ->
                onImageLongClick(position)
            }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Registrar el ActivityResultLauncher en onCreate
        registerLaunchImagenDetalles()
    }

    // Se declara un ActivityResultLauncher que manejará los resultados de las actividades lanzadas para obtener un resultado.
    private val launchImagenDetalles =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val position = data?.getIntExtra("position", RecyclerView.NO_POSITION)
                position?.let {
                    if (it != RecyclerView.NO_POSITION) {
                        Log.d("MainActivity", "Received position from ImagenDetallesActivity: $position")
                        imageList.removeAt(it)
                        // Se notifica al adaptador que se eliminó un elemento en la posición especificada.
                        adapter.notifyItemRemoved(it)
                        // Se guarda la lista actualizada de imágenes en SharedPreferences.
                        saveImageListToSharedPreferences()
                    }
                }
            }
        }

    // Método para obtener nombres y fechas de las fotos
    private fun obtenerNombresFechasFotos(context: Context): ArrayList<Pair<String, String>> {
        val fotosInfo = arrayListOf<Pair<String, String>>()

        // Verificar permisos de lectura del almacenamiento externo
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Definir las columnas que deseas recuperar
            val projection = arrayOf(
                MediaStore.Images.Media.DISPLAY_NAME,  // Nombre del archivo de la imagen
                MediaStore.Images.Media.DATE_TAKEN     // Fecha en que se tomó la imagen
            )

            // Ordenar por fecha, de más reciente a más antigua
            val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

            // Realizar la consulta al proveedor de contenido de MediaStore
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val nombreColumnIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val fechaColumnIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

                while (cursor.moveToNext()) {
                    val nombreFoto = cursor.getString(nombreColumnIndex)
                    val fechaFoto = cursor.getString(fechaColumnIndex)

                    fotosInfo.add(Pair(nombreFoto, fechaFoto))
                }
            }
        } else {
            // Solicitar permisos de lectura del almacenamiento externo si no están otorgados
            ActivityCompat.requestPermissions(
                context as AppCompatActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_READ_EXTERNAL_STORAGE
            )
        }

        return fotosInfo
    }

    // Método para registrar el ActivityResultLauncher
    private fun registerLaunchImagenDetalles() {
        // Este método se encarga de registrar el ActivityResultLauncher
        // No es necesario volver a declarar launchImagenDetalles aquí
        // porque ya lo hemos definido arriba.
    }

    // Manejador del clic en la imagen
    private fun onImageClick(imageUri: Uri, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            val intent = Intent(this, ImagenDetallesActivity::class.java)
            intent.putExtra("imageUri", imageUri.toString())
            Log.d("MainActivity", "Launching ImagenDetallesActivity with imageUri: $imageUri")
            launchImagenDetalles.launch(intent)
        } else {
            Log.e("MainActivity", "Invalid position or empty image list")
            // Aquí puedes mostrar un mensaje de error o realizar alguna otra acción adecuada.
        }
    }



    // Manejador del clic largo en la imagen
    private fun onImageLongClick(imageUri: Uri) {
        val position = imageList.indexOf(imageUri)
        if (position != RecyclerView.NO_POSITION) {
            adapter.removeItem(position)
            Log.d("MainActivity", "Long clicked on item at position: $position")

            // No necesitas llamar notifyItemRemoved o notifyDataSetChanged aquí
            // porque removeItem ya se encarga de notificar al adaptador.

            // Guardar la lista actualizada en SharedPreferences
            saveImageListToSharedPreferences()
        }
    }



    // Método para guardar la lista de imágenes en SharedPreferences
    private fun saveImageListToSharedPreferences() {
        val sharedPreferences = getSharedPreferences("ImageList", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val set = HashSet<String>()
        for (uri in imageList) {
            set.add(uri.toString())
        }
        editor.putStringSet("imageSet", set)
        editor.apply()
    }

    // Método para cargar la lista de imágenes desde SharedPreferences
    private fun loadImageListFromSharedPreferences() {
        val sharedPreferences = getSharedPreferences("ImageList", Context.MODE_PRIVATE)
        val set = sharedPreferences.getStringSet("imageSet", HashSet<String>())
        set?.let {
            imageList.clear()
            for (uriString in it) {
                imageList.add(Uri.parse(uriString))
            }
        } ?: run {
            imageList.clear() // Limpiar la lista si el conjunto es nulo
        }
    }


    // Método para abrir la galería y seleccionar imágenes
    fun openGallery(view: android.view.View) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    // Método para manejar el resultado de la selección de imágenes desde la galería
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_CODE) {
                val imageUri = data?.data
                imageUri?.let {
                    imageList.add(it)
                    adapter.notifyDataSetChanged()
                    saveImageListToSharedPreferences()
                }
            }
        }
    }


    // Código de solicitud para abrir la galería
    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}





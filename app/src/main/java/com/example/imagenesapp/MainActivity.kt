package com.example.imagenesapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val imageList = mutableListOf<Uri>()
    private lateinit var adapter: ImageAdapter

    private val IMAGE_PICK_CODE = 1000
    private val DELETE_IMAGE_REQUEST_CODE = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)

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
    }


    fun openGallery(view: android.view.View) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    fun onImageClick(imageUri: Uri, position: Int) {
        if (position != RecyclerView.NO_POSITION && position < imageList.size) {
            val position = getIntent().getIntExtra("position", RecyclerView.NO_POSITION)
            Log.d("MainActivity", "Position received: $position")
            val intent = Intent(this, ImagenDetallesActivity::class.java)
            intent.putExtra("imageUri", imageUri.toString())
            startActivityForResult(intent, DELETE_IMAGE_REQUEST_CODE)
        } else {
            Log.e("MainActivity", "Invalid position or empty image list")
            // Aquí puedes mostrar un mensaje de error o realizar alguna otra acción adecuada.
        }
    }



    fun onImageLongClick(position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            adapter.removeItem(position)

            // No necesitas llamar notifyItemRemoved o notifyDataSetChanged aquí
            // porque removeItem ya se encarga de notificar al adaptador.

            // Guardar la lista actualizada en SharedPreferences
            saveImageListToSharedPreferences()
        }
    }


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
            } else if (requestCode == DELETE_IMAGE_REQUEST_CODE) {
                val position = data?.getIntExtra("position", RecyclerView.NO_POSITION)
                position?.let {
                    Log.d("onActivityResult", "Received position: $position")
                    if (it != RecyclerView.NO_POSITION) {
                        Log.d("onActivityResult", "Removing image at position: $position")
                        // Eliminar la imagen correspondiente a la posición
                        imageList.removeAt(it)
                        adapter.notifyItemRemoved(it)
                        // Guardar la lista actualizada en SharedPreferences
                        saveImageListToSharedPreferences()
                    }
                }
            }
        }
    }



    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}



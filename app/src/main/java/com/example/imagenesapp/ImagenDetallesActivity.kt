package com.example.imagenesapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

// Clase que muestra los detalles de una imagen seleccionada
class ImagenDetallesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imagen_detalles)

        // Se obtiene la URI de la imagen desde el intento
        val imageUriString = intent.getStringExtra("imageUri")

        try {
            if (imageUriString != null) {
                // Se convierte la URI de String a Uri
                val imageUri = Uri.parse(imageUriString)
                val imageView = findViewById<ImageView>(R.id.imageViewDetail)

                // Se carga la imagen en el ImageView utilizando Glide
                Glide.with(this)
                    .load(imageUri)
                    .into(imageView)

                // Se muestra el nombre y la fecha de la imagen
                mostrarInformacionImagen(imageUri)
            } else {
                // Manejo de caso en que no se recibe ninguna URI de imagen
                Log.e("ImagenDetallesActivity", "No se recibió ninguna URI de imagen.")
                // Finalizar la actividad si no hay URI de imagen para mostrar
                finish()
            }
        } catch (e: Exception) {
            // Manejo de excepciones durante la carga de la imagen
            Log.e("ImagenDetallesActivity", "Error al cargar la imagen: ${e.message}")
            e.printStackTrace()
            // Puedes mostrar un mensaje de error al usuario aquí
        }
    }

    // Método para mostrar el nombre y la fecha de la imagen en TextViews
    private fun mostrarInformacionImagen(imageUri: Uri) {
        // Obtener el nombre de la imagen
        val nombreImagen = obtenerNombreImagen(imageUri)

        // Obtener la fecha de la imagen
        val fechaImagen = obtenerFechaImagen(imageUri)

        // Mostrar el nombre y la fecha en TextViews
        findViewById<TextView>(R.id.textViewNombre)?.text = "Nombre: $nombreImagen"
        findViewById<TextView>(R.id.textViewFecha)?.text = "Fecha: $fechaImagen"
    }

    // Método para obtener el nombre de la imagen a partir de su URI
    private fun obtenerNombreImagen(imageUri: Uri): String {
        var nombreImagen = ""
        contentResolver.query(imageUri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nombreColumnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                nombreImagen = cursor.getString(nombreColumnIndex)
            }
        }
        return nombreImagen
    }

    // Método para obtener la fecha de la imagen a partir de sus metadatos Exif
    @SuppressLint("SimpleDateFormat")
    private fun obtenerFechaImagen(imageUri: Uri): String {
        var fechaImagen = ""
        try {
            contentResolver.openInputStream(imageUri)?.use { inputStream ->
                val exifInterface = ExifInterface(inputStream)
                val fechaMillis = exifInterface.getAttribute(ExifInterface.TAG_DATETIME)
                fechaMillis?.let {
                    val dateFormat = SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
                    val date = dateFormat.parse(it)
                    date?.let {
                        val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm").format(date)
                        fechaImagen = formattedDate
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return fechaImagen
    }
}




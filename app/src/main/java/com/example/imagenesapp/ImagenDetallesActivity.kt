package com.example.imagenesapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.IOException

class ImagenDetallesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imagen_detalles)


        val imageUriString = intent.getStringExtra("imageUri")
        val imageUri = Uri.parse(imageUriString)

        // Obtener el nombre del archivo y la fecha de la foto
        val fileName = obtenerNombreArchivo(imageUri)
        val dateTime = obtenerFechaFoto(imageUri)

        // Referencias a los TextView en el layout
        val fileNameTextView: TextView = findViewById(R.id.fileNameTextView)
        val dateTimeTextView: TextView = findViewById(R.id.dateTimeTextView)

        // Establecer el nombre del archivo y la fecha de la foto en los TextView
        fileNameTextView.text = fileName
        dateTimeTextView.text = dateTime

        val imageView = findViewById<ImageView>(R.id.imageViewDetail)

        // Cargar la imagen con Glide
        Glide.with(this)
            .load(imageUri)
            .into(imageView)

    }

    @SuppressLint("Range")
    private fun obtenerNombreArchivo(uri: Uri): String {
        // Obtener el nombre del archivo desde la URI
        val cursor = contentResolver.query(uri, null, null, null, null)
        val nombreArchivo = if (cursor != null) {
            cursor.moveToFirst()
            val nombre = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            cursor.close()
            nombre
        } else {
            uri.lastPathSegment
        }
        return nombreArchivo ?: "Nombre desconocido"
    }

    private fun obtenerFechaFoto(uri: Uri): String {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val exifInterface = ExifInterface(inputStream)
                val fechaFoto = exifInterface.getAttribute(ExifInterface.TAG_DATETIME)
                inputStream.close()
                return fechaFoto ?: "Fecha desconocida"
            } else {
                return "No se pudo abrir el InputStream"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return "Error al abrir el InputStream"
        }
    }
}



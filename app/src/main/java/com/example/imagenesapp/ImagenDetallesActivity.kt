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

class ImagenDetallesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imagen_detalles)

        val imageUriString = intent.getStringExtra("imageUri")

        try {
            if (imageUriString != null) {
                val imageUri = Uri.parse(imageUriString)
                val imageView = findViewById<ImageView>(R.id.imageViewDetail)

                // Cargar la imagen con Glide
                Glide.with(this)
                    .load(imageUri)
                    .into(imageView)
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
}


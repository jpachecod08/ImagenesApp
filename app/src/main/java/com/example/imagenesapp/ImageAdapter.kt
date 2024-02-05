package com.example.imagenesapp

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// Adaptador para mostrar una lista de imágenes en un RecyclerView
class ImageAdapter(
    private val imageList: MutableList<Uri>, // Lista de URIs de las imágenes
    private val onItemClick: (Uri) -> Unit, // Función para manejar el clic en un elemento
    private val onImageClick: (Uri) -> Unit, // Función para manejar el clic en la imagen
    private val onDeleteClick: (Uri) -> Unit // Función para manejar el clic en el botón de eliminar
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    // Método para crear una nueva vista de elemento de lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        // Inflar el diseño del elemento de imagen
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    // Método para enlazar los datos de una imagen a una vista de elemento de lista
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = imageList[position] // URI de la imagen en la posición actual
        holder.bind(imageUri) // Enlazar la imagen con la vista de ImageView
        // Manejar el clic en el elemento de lista
        holder.itemView.setOnClickListener {
            onItemClick(imageUri)
        }
        // Manejar el clic en la imagen
        holder.itemView.findViewById<ImageView>(R.id.imageViewItem).setOnClickListener {
            onImageClick(imageUri)
        }
        // Manejar el clic en el botón de eliminar
        holder.itemView.findViewById<ImageView>(R.id.btnDelete).setOnClickListener {
            onDeleteClick(imageUri)
        }
    }

    // Método para obtener el número total de elementos en la lista
    override fun getItemCount(): Int {
        return imageList.size
    }

    // Método para eliminar un elemento de la lista y notificar al adaptador
    fun removeItem(position: Int) {
        imageList.removeAt(position)
        notifyItemRemoved(position)
    }

    // Clase interna que representa la vista de un elemento de imagen
    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageViewItem)

        // Método para enlazar una imagen con la vista de ImageView
        fun bind(imageUri: Uri) {
            Glide.with(itemView.context)
                .load(imageUri)
                .into(imageView)
        }
    }
}

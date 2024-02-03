package com.example.imagenesapp

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageAdapter(
    private val imageList: MutableList<Uri>,
    private val onItemClick: (Uri) -> Unit,
    private val onImageClick: (Uri) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = imageList[position]
        holder.bind(imageUri)
        holder.itemView.setOnClickListener {
            onItemClick(imageUri)
        }
        holder.itemView.findViewById<ImageView>(R.id.imageViewItem).setOnClickListener {
            onImageClick(imageUri)
        }
        holder.itemView.findViewById<ImageView>(R.id.btnDelete).setOnClickListener {
            onDeleteClick(position)
        }


    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    fun removeItem(position: Int) {
        imageList.removeAt(position)
        notifyItemRemoved(position)
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageViewItem)

        fun bind(imageUri: Uri) {
            Glide.with(itemView.context)
                .load(imageUri)
                .into(imageView)
        }
    }
}

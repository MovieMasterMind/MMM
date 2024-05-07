package com.example.mmm

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mmm.R

class TrailerAdapter(private val trailers: List<TrailerMember>) : RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trailer, parent, false)
        return TrailerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrailerViewHolder, position: Int) {
        val trailer = trailers[position]
        holder.bind(trailer)
    }

    override fun getItemCount(): Int {
        return trailers.size
    }

    inner class TrailerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnailImageView: ImageView = itemView.findViewById(R.id.thumbnailImageView)

        fun bind(trailer: TrailerMember) {
            // Load trailer thumbnail using Glide or any other image loading library
            Glide.with(itemView)
                .load("https://img.youtube.com/vi/${trailer.key}/maxresdefault.jpg")
                .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                .into(thumbnailImageView)

            // Set click listener or any other interactions for the item
            itemView.setOnClickListener {
                // Handle click on trailer item
                val intent = Intent(itemView.context, TrailerActivity::class.java)
                intent.putExtra("trailerUrl", "${trailer.YouTubeURL}")
                itemView.context.startActivity(intent)
            }
        }
    }
}


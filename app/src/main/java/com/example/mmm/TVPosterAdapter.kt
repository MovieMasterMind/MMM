package com.example.mmm

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TVPosterAdapter(private val posterUrls: List<String>, private val tvShowIds: List<Int>) :
    RecyclerView.Adapter<TVPosterAdapter.PosterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosterViewHolder {
        // Ensure you are inflating the correct layout
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_poster, parent, false)
        return PosterViewHolder(view)
    }

    override fun onBindViewHolder(holder: PosterViewHolder, position: Int) {
        // Use Glide to load the poster image
        Glide.with(holder.itemView.context)
            .load(posterUrls[position])
            .into(holder.imageView)

        holder.imageView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TvDetailsActivity::class.java)
            intent.putExtra("TV_ID", tvShowIds[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = posterUrls.size

    inner class PosterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewPoster)
    }
}

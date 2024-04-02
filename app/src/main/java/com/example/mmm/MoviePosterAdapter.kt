package com.example.mmm
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mmm.R
import com.bumptech.glide.request.target.Target

class MoviePosterAdapter(private val posterUrls: List<String>) :

class MoviePosterAdapter(private val posterUrls: List<String>, private val movieIds: List<Int>) :
    //This is a subclass of RecyclerView.Adapter, called PosterViewHolder
    RecyclerView.Adapter<MoviePosterAdapter.PosterViewHolder>() {

        //now we override these functions because we need to,
        //get use these with the item_movie_poster.xml file that is used instead of Recycler view
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosterViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_poster, parent, false)
            return PosterViewHolder(view)
        }

    override fun onBindViewHolder(holder: PosterViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(posterUrls[position])
            .into(holder.imageView)

        // Set a click listener to open MovieDetailsActivity with the correct movie ID
        holder.imageView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, MovieDetailsActivity::class.java)
            intent.putExtra("MOVIE_ID", movieIds[position]) // Pass the movie ID here
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = posterUrls.size

    inner class PosterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewPoster)

        init {
            itemView.setOnClickListener {
                // Get the adapter position
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION) {
                    val context = itemView.context
                    val intent = Intent(context, MovieDetailsActivity::class.java).apply {
                        // Make sure movieIds[position] is valid
                        putExtra("MOVIE_ID", movieIds[position])
                    }
                    context.startActivity(intent)
                }
            }
        }
    }
}

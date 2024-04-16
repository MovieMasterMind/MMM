
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mmm.MovieDetailsActivity
import com.example.mmm.R

class SearchResultAdapter(private val items: List<MoviePoster>) : RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return SearchResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.search_result_item_title)
        private val posterImageView: ImageView = itemView.findViewById(R.id.search_result_item_poster)

        fun bind(moviePoster: MoviePoster) {
            titleTextView.text = moviePoster.title
            Glide.with(itemView.context)
                .load(moviePoster.imageUrl)
                .into(posterImageView)

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, MovieDetailsActivity::class.java).apply {
                    putExtra("MOVIE_ID", moviePoster.movieId.toInt()) // Assuming movieId is a property in MoviePoster
                }
                context.startActivity(intent)
            }
        }
    }
}

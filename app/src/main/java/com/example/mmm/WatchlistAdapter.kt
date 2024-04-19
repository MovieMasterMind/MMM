import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mmm.MovieDetailsActivity
import com.example.mmm.R
import com.example.mmm.WatchlistItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WatchlistAdapter(private val items: MutableList<WatchlistItem>) : RecyclerView.Adapter<WatchlistAdapter.WatchlistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchlistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_watchlist, parent, false)
        return WatchlistViewHolder(view)
    }

    override fun onBindViewHolder(holder: WatchlistViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class WatchlistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.watchlist_item_title)
        private val posterImageView: ImageView = itemView.findViewById(R.id.watchlist_item_poster)
        private val removeButton: Button = itemView.findViewById(R.id.remove_from_watchlist_button)

        fun bind(watchlistItem: WatchlistItem) {
            titleTextView.text = watchlistItem.title
            Glide.with(itemView.context)
                .load(watchlistItem.posterUrl)
                .into(posterImageView)

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, MovieDetailsActivity::class.java).apply {
                    putExtra("MOVIE_ID", watchlistItem.movieId)
                }
                context.startActivity(intent)
            }

            removeButton.setOnClickListener {
                removeFromWatchlist(adapterPosition, itemView.context)
            }
        }
    }

    private fun removeFromWatchlist(position: Int, context: Context) {
        val movieId = items[position].movieId
        items.removeAt(position)
        notifyItemRemoved(position)

        // Update SharedPreferences
        val sharedPrefs = context.getSharedPreferences("watchlist", Context.MODE_PRIVATE)
        val gson = Gson()
        val type = object : TypeToken<List<WatchlistItem>>() {}.type
        val watchlist = gson.fromJson<List<WatchlistItem>>(sharedPrefs.getString("watchlistJson", "[]"), type).toMutableList()

        watchlist.removeAll { it.movieId == movieId }

        sharedPrefs.edit().putString("watchlistJson", gson.toJson(watchlist, type)).apply()
    }
}

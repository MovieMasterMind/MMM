import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mmm.MovieDetailsActivity
import com.example.mmm.R
import com.example.mmm.TvDetailsActivity
import com.example.mmm.WatchlistActivity
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
            if (watchlistItem.isMovie == true) {
                itemView.setOnClickListener {
                    val context = itemView.context
                    val intent = Intent(context, MovieDetailsActivity::class.java).apply {
                        putExtra("MOVIE_ID", watchlistItem.itemId)
                    }
                    context.startActivity(intent)
                }
            } else {
                itemView.setOnClickListener {
                    val context = itemView.context
                    val intent = Intent(context, TvDetailsActivity::class.java).apply {
                        putExtra("TV_ID", watchlistItem.itemId)
                    }
                    context.startActivity(intent)
                }
            }

            removeButton.apply {
                setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.remove_from_watchlist_background
                    )
                )  // Set the background color
                setOnClickListener {
                    removeFromWatchlist(adapterPosition, itemView.context)
                }
            }
            }
        }

    private fun removeFromWatchlist(position: Int, context: Context) {
        val itemToRemove = items[position]
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount) // Ensure that item positions update correctly

        // Update SharedPreferences
        val sharedPrefs = context.getSharedPreferences("watchlist", Context.MODE_PRIVATE)
        val gson = Gson()
        val type = object : TypeToken<List<WatchlistItem>>() {}.type
        var watchlist = gson.fromJson<List<WatchlistItem>>(sharedPrefs.getString("watchlistJson", "[]"), type).toMutableList()

        // Remove all items with the same itemId and isMovie flag
        watchlist.removeAll { it.itemId == itemToRemove.itemId && it.isMovie == itemToRemove.isMovie }
        sharedPrefs.edit().putString("watchlistJson", gson.toJson(watchlist, type)).apply()

        // Call to check if the list is empty and update UI accordingly
        if (context is WatchlistActivity) {
            context.checkEmpty()
        }
    }
}


import android.content.Context
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchResultAdapter(private var items: MutableList<MoviePoster>) : RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return SearchResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<MoviePoster>) {
        items.clear() // Clear the existing items
        items.addAll(newItems) // Add all the new items
        notifyDataSetChanged() // Notify the adapter to refresh the view
    }

    inner class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.search_result_item_title)
        private val posterImageView: ImageView = itemView.findViewById(R.id.search_result_item_poster)

        fun bind(moviePoster: MoviePoster) {
            titleTextView.text = moviePoster.title
            Glide.with(itemView.context)
                .load(moviePoster.imageUrl)
                .into(posterImageView)

            itemView.setOnClickListener {
                saveSearchToHistory(items[adapterPosition], itemView.context)
                val context = itemView.context
                val intent = Intent(context, MovieDetailsActivity::class.java).apply {
                    putExtra("MOVIE_ID", moviePoster.movieId.toInt())
                }
                context.startActivity(intent)
            }
        }
    }

    private fun saveSearchToHistory(moviePoster: MoviePoster, context: Context) {
        val sharedPreferences = context.getSharedPreferences("SearchHistoryPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val type = object : TypeToken<MutableList<MoviePoster>>() {}.type

        // Retrieve the current history or initialize it as an empty list.
        val historyJson = sharedPreferences.getString("historyJson", "[]")
        val history: MutableList<MoviePoster> = gson.fromJson(historyJson, type) ?: mutableListOf()

        // Check if the movie is already in the history to prevent duplicates.
        if (!history.any { it.movieId == moviePoster.movieId }) {
            history.add(0, moviePoster)  // Add the new item at the beginning of the list for recent access

            // Limit the history size to avoid excessive memory use.
            if (history.size > MAX_HISTORY_SIZE) {
                history.removeAt(history.size - 1)  // Remove the last item if over size limit
            }

            // Convert the updated history back to JSON and save it.
            sharedPreferences.edit().putString("historyJson", gson.toJson(history, type)).apply()
        }
    }

    companion object {
        private const val MAX_HISTORY_SIZE = 10  // Max number of items in the history.
    }
}
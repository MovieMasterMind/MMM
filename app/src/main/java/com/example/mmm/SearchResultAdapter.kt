
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mmm.APICallerForMovie
import com.example.mmm.MovieDetailsActivity
import com.example.mmm.R
import com.example.mmm.SearchableActivity
import com.example.mmm.TvDetailsActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SearchResultAdapter(private var items: MutableList<MoviePoster>) : RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {

    var isSearchingForMovies: Boolean = true // Default value
        set(value) {
            field = value
            notifyDataSetChanged() // Notify adapter that data set has changed
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return SearchResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, isSearchingForMovies)
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
        val searchableactivity = SearchableActivity()
        val apiCallerForMovie = APICallerForMovie()

        fun bind(moviePoster: MoviePoster,isSearchingForMovies: Boolean){
            // Access views and bind data here
            titleTextView.text = moviePoster.title
            Glide.with(itemView.context)
                .load(moviePoster.imageUrl)
                .into(posterImageView)

            // Access the Switch view
            itemView.setOnClickListener {
                Log.e("isSearchingForMovies",isSearchingForMovies.toString())
                if(isSearchingForMovies)
                {
                    saveSearchToMovieHistory(items[adapterPosition], itemView.context)
                    val context = itemView.context
                    val intent = Intent(context, MovieDetailsActivity::class.java).apply {
                        putExtra("MOVIE_ID", moviePoster.movieId.toInt())
                    }
                    context.startActivity(intent)
                }
                else
                {
                    saveSearchToTVHistory(items[adapterPosition], itemView.context)
                    val context = itemView.context
                    val intent = Intent(context, TvDetailsActivity::class.java).apply {
                        putExtra("TV_ID", moviePoster.movieId.toInt())
                    }
                    context.startActivity(intent)
                }

            }
        }
    }

    private fun saveSearchToMovieHistory(moviePoster: MoviePoster, context: Context) {
        val sharedPreferences = context.getSharedPreferences("SearchHistoryPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val type = object : TypeToken<MutableList<MoviePoster>>() {}.type

        // Retrieve the current history or initialize it as an empty list.
        val historyJson = sharedPreferences.getString("movieHistoryJson", "[]")
        val history: MutableList<MoviePoster> = gson.fromJson(historyJson, type) ?: mutableListOf()

        // Check if the movie is already in the history to prevent duplicates.
        if (!history.any { it.movieId == moviePoster.movieId }) {
            history.add(0, moviePoster)  // Add the new item at the beginning of the list for recent access

            // Limit the history size to avoid excessive memory use.
            if (history.size > MAX_HISTORY_SIZE) {
                history.removeAt(history.size - 1)  // Remove the last item if over size limit
            }

            // Convert the updated history back to JSON and save it.
            sharedPreferences.edit().putString("movieHistoryJson", gson.toJson(history, type)).apply()
        }
    }
    private fun saveSearchToTVHistory(moviePoster: MoviePoster, context: Context) {
        val sharedPreferences = context.getSharedPreferences("SearchHistoryPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val type = object : TypeToken<MutableList<MoviePoster>>() {}.type

        // Retrieve the current history or initialize it as an empty list.
        val historyJson = sharedPreferences.getString("tvHistoryJson", "[]")
        val history: MutableList<MoviePoster> = gson.fromJson(historyJson, type) ?: mutableListOf()

        // Check if the movie is already in the history to prevent duplicates.
        if (!history.any { it.movieId == moviePoster.movieId }) {
            history.add(0, moviePoster)  // Add the new item at the beginning of the list for recent access

            // Limit the history size to avoid excessive memory use.
            if (history.size > MAX_HISTORY_SIZE) {
                history.removeAt(history.size - 1)  // Remove the last item if over size limit
            }

            // Convert the updated history back to JSON and save it.
            sharedPreferences.edit().putString("tvHistoryJson", gson.toJson(history, type)).apply()
        }
    }

    companion object {
        private const val MAX_HISTORY_SIZE = 10  // Max number of items in the history.
    }
}
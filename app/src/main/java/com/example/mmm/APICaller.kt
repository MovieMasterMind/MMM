import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class APICaller {

    private val client = OkHttpClient()

    fun getData(apiUrl: String, textView: TextView, recyclerView: RecyclerView) {

        val request = Request.Builder().url(apiUrl).get().build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API Error", "Error fetching data: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    val responseData = it.string()
                    Log.d("API Response", responseData)
                    parseAndDisplayData(responseData, textView, recyclerView)
                }
            }
        })
    }

    private fun parseAndDisplayData(response: String, textView: TextView, recyclerView: RecyclerView) {
        try {
            val jsonObject = JSONObject(response)
            val resultsArray = jsonObject.getJSONArray("results")

            // Display details about the movie in the TextView
            val movieDetails = StringBuilder()
            for (i in 0 until resultsArray.length()) {
                val movieObject = resultsArray.getJSONObject(i)
                val title = movieObject.optString("title")
                val releaseDate = movieObject.optString("release_date")
                val overview = movieObject.optString("overview")
                val posterPath = movieObject.optString("poster_path")

                val details = "Title: $title\nRelease Date: $releaseDate\nOverview: $overview\nPoster: $posterPath\n\n"
                movieDetails.append(details)
            }
            textView.post { textView.text = movieDetails.toString() }

            // Load poster images into the RecyclerView
            val adapter = MoviePosterAdapter(getPosterUrls(resultsArray))
            recyclerView.post { recyclerView.adapter = adapter }


        } catch (e: JSONException) {
            Log.e("JSON Error", "Error parsing JSON: $e")
        }
    }


    private fun getPosterUrls(resultsArray: JSONArray): List<String> {
        val posterUrls = mutableListOf<String>()
        for (i in 0 until resultsArray.length()) {
            val movieObject = resultsArray.getJSONObject(i)
            val posterPath = movieObject.optString("poster_path")
            val posterUrl = "https://image.tmdb.org/t/p/w500$posterPath"
            posterUrls.add(posterUrl)
        }
        return posterUrls
    }
}

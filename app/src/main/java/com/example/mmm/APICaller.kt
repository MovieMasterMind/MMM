
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mmm.MainActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class APICaller {

    private val client = OkHttpClient()

    fun getData(apiUrl: String, textView: TextView, recyclerView: RecyclerView, callback: (List<String>, List<Int>) -> Unit) {
        val request = Request.Builder().url(apiUrl).get().build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API Error", "Error fetching data: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    val responseData = it.string()
                    Log.d("API Response", responseData)

                    // Parse the response data here and call the callback with the results
                    val (posterUrls, movieIds) = parseAndDisplayData(responseData, textView)
                    // Make sure to call the callback on the main thread
                    (recyclerView.context as MainActivity).runOnUiThread {
                        callback(posterUrls, movieIds)
                    }
                }
            }
        })
    }

    private fun parseAndDisplayData(response: String, textView: TextView): Pair<List<String>, List<Int>> {
        val posterUrls = mutableListOf<String>()
        val movieIds = mutableListOf<Int>()

        try {
            val jsonObject = JSONObject(response)
            val resultsArray = jsonObject.getJSONArray("results")

            for (i in 0 until resultsArray.length()) {
                val movieObject = resultsArray.getJSONObject(i)
                val id = movieObject.getInt("id")
                val posterPath = movieObject.getString("poster_path")
                val posterUrl = "https://image.tmdb.org/t/p/w500$posterPath"

                movieIds.add(id)
                posterUrls.add(posterUrl)
            }
        } catch (e: JSONException) {
            Log.e("JSON Error", "Error parsing JSON: $e")
        }

        return Pair(posterUrls, movieIds)
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

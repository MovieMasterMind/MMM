package com.example.mmm

import android.content.Context
import android.widget.ImageView
import org.json.JSONObject
import java.io.IOException

class PosterToScreen(private val context: Context) {

    fun displayPoster(imageView: ImageView, fileName: String) {
        // Read JSON data from the file
        val jsonData = readJsonFromAsset(fileName)

        // Parse JSON data
        val jsonObject = JSONObject(jsonData)
        val posterUrl = jsonObject.getString("Poster")

        // Load image directly to ImageView
        imageView.setImageResource(context.resources.getIdentifier(posterUrl, "drawable", context.packageName))
    }

    private fun readJsonFromAsset(fileName: String): String {
        val json: String?
        try {
            val inputStream = context.assets.open(fileName)
            json = inputStream.bufferedReader().use { it.readText() }
            inputStream.close()
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return ""
        }
        return json
    }
}

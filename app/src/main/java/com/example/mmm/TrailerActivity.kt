package com.example.mmm

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class TrailerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trailer)

        val webView: WebView = findViewById(R.id.webViewTrailer)
        val trailerUrl: String? = intent.getStringExtra("trailerUrl")

        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()
        webView.loadUrl(trailerUrl ?: "")

        val toolbar: Toolbar = findViewById(R.id.toolbar_trailer)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}

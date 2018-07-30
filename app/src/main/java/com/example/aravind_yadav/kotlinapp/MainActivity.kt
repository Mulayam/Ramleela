package com.example.aravind_yadav.kotlinapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.webkit.WebView
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var url :String = downloadUrl("https://drive.google.com/uc?export=download&id=170LvuCdF04gjLNlZRd3vY7hENhzmt62E");

      /*  val webview = findViewById(R.id.webView) as WebView
        webview.webViewClient = WebViewClient()
        webview.settings.javaScriptEnabled = true
        webview.settings.javaScriptCanOpenWindowsAutomatically = true
        webview.settings.pluginState = WebSettings.PluginState.ON
        webview.settings.mediaPlaybackRequiresUserGesture = false
        webview.webChromeClient = WebChromeClient()
        webview.loadUrl(url)*/


    }


    @Throws(IOException::class)
    fun downloadUrl(myurl: String): String {
        var `is`: InputStream? = null
        try {
            val url = URL(myurl)
            val conn = url.openConnection() as HttpURLConnection
            conn.setReadTimeout(10000)
            conn.setConnectTimeout(15000)
            conn.setRequestMethod("GET")
            conn.setDoInput(true)
            conn.connect()
            `is` = conn.getInputStream()
            return readIt(`is`)
        } finally {
            if (`is` != null) {
                `is`!!.close()
            }
        }
    }


    @Throws(IOException::class)
    fun readIt(stream: InputStream): String {
        val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
        val sb = StringBuilder()
        var line: String=""
        while ((line == reader.readLine()) != null) {
            if (line.contains("fmt_stream_map")) {
                sb.append(line + "\n")
                break
            }
        }
        reader.close()
        val result = decode(sb.toString())
        val url = result.split("\\|".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        return url[1]
    }

    fun decode(`in`: String): String {
        var working = `in`
        var index: Int
        index = working.indexOf("\\u")
        while (index > -1) {
            val length = working.length
            if (index > length - 6) break
            val numStart = index + 2
            val numFinish = numStart + 4
            val substring = working.substring(numStart, numFinish)
            val number = Integer.parseInt(substring, 16)
            val stringStart = working.substring(0, index)
            val stringEnd = working.substring(numFinish)
            working = stringStart + number.toChar() + stringEnd
            index = working.indexOf("\\u")
        }
        return working
    }

}

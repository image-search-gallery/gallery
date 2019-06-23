package com.project.gallery.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.net.HttpURLConnection
import java.net.URL

class HttpsBitmapUrlLoader : BitmapUrlLoader {

    override fun load(url: String): Bitmap? {
        val connection = URL(url).openConnection() as HttpURLConnection
        var bitmap: Bitmap? = null
        try {
            connection.inputStream.use {
                bitmap = BitmapFactory.decodeStream(it)
            }
        } finally {
            connection.disconnect()
        }

        return bitmap
    }

}
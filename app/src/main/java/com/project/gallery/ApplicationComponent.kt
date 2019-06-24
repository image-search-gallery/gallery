package com.project.gallery

import android.graphics.Bitmap
import android.util.LruCache
import com.project.gallery.search.data.repository.flickr.FlickrImageRepository
import com.project.gallery.search.data.repository.flickr.JsonResponseDeserializer
import java.util.concurrent.Executors

object ApplicationComponent {

    val imageRepository by lazy {
        FlickrImageRepository(JsonResponseDeserializer(), Executors.newCachedThreadPool())
    }

    val bitmapLruCache by lazy {
        val maxMemoryBytes = Runtime.getRuntime().maxMemory()
        val cacheSize = (maxMemoryBytes / 4).toInt()

        object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.allocationByteCount
            }
        }
    }
}
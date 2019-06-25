package com.project.gallery

import android.graphics.Bitmap
import android.util.LruCache
import com.project.gallery.search.data.repository.flickr.FlickrImageRepository
import com.project.gallery.search.data.repository.flickr.FlickrJsonResponseDeserializer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Provides singleton dependencies for global application scope.
 */
object ApplicationComponent {

    /**
     * Provides image repository.
     */
    val imageRepository by lazy {
        FlickrImageRepository(FlickrJsonResponseDeserializer(), Executors.newCachedThreadPool())
    }

    /**
     * Provides LRU cache for bitmap caching.
     */
    val bitmapLruCache by lazy {
        val maxMemoryBytes = Runtime.getRuntime().maxMemory()
        val cacheSize = (maxMemoryBytes / 4).toInt()

        object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.allocationByteCount
            }
        }
    }

    /**
     * Provides executor to use for image loading.
     */
    val imageLoaderExecutor by lazy<ExecutorService> {
        Executors.newFixedThreadPool(3)
    }
}
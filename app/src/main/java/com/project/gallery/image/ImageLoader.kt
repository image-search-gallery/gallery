package com.project.gallery.image

import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

class ImageLoader(
    private val executor: ExecutorService,
    private val bitmapUrlLoader: BitmapUrlLoader
) {

    private val imageViewToFuture: HashMap<ImageView, Future<*>> = HashMap()
    private var lruCache: LruCache<String, Bitmap>

    init {
        val maxMemoryBytes = Runtime.getRuntime().maxMemory()
        val cacheSize = (maxMemoryBytes / 4).toInt()

        lruCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String?, value: Bitmap?): Int {
                return value?.allocationByteCount ?: 0
            }
        }
    }

    fun load(imageUrl: String, view: ImageView) {

        val cachedImage = lruCache.get(imageUrl)

        cachedImage?.let {
            setBitmapToImageView(it, view)
            return
        }

        view.tag = imageUrl

        val future = imageViewToFuture[view]
        future?.let {
            it.cancel(true)
        }

        imageViewToFuture[view] = executor.submit {
            try {
                val bitmap = bitmapUrlLoader.load(imageUrl)

                bitmap?.let {
                    if (view.tag == imageUrl) {
                        setBitmapToImageView(it, view)
                    }

                    lruCache.put(imageUrl, it)
                }
            } catch (e: Exception) {
                Log.e("ImageLoader", e.message)
                Log.e("ImageLoader", "Failing URL: $imageUrl")
                e.printStackTrace()
            }
        }
    }

    private fun setBitmapToImageView(bitmap: Bitmap, view: ImageView) {
        view.post {
            view.setImageBitmap(bitmap)
        }
    }
}
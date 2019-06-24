package com.project.gallery.image

import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import com.project.gallery.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

class ImageLoader(
    private val executor: ExecutorService,
    private val bitmapUrlLoader: BitmapUrlLoader,
    private val lruCache: LruCache<String, Bitmap>
) {

    private val imageViewToFuture: HashMap<ImageView, Future<*>> = HashMap()

    fun load(imageUrl: String, view: ImageView) {

        val cachedImage = lruCache.get(imageUrl)

        cachedImage?.let {
            view.setImageBitmap(it)
            return
        }

        view.setImageResource(R.drawable.ic_image_place_holder)

        view.tag = imageUrl

        val future = imageViewToFuture[view]
        future?.cancel(true)

        imageViewToFuture[view] = executor.submit {
            try {
                val bitmap = bitmapUrlLoader.load(imageUrl)

                view.post {
                    bitmap?.let {
                        if (view.tag == imageUrl) {
                            view.setImageBitmap(bitmap)
                        }

                        lruCache.put(imageUrl, it)
                    }
                }
            } catch (e: Exception) {
                Log.e("ImageLoader", e.message)
                Log.e("ImageLoader", "Failing URL: $imageUrl")
                e.printStackTrace()
            }
        }
    }

}
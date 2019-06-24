package com.project.gallery.image

import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import com.project.gallery.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

/**
 * Provides image loading from URL using [BitmapUrlLoader] and sets it to [ImageView].
 * Uses [LruCache] to store recently created [Bitmap]s.
 * Modifies an [ImageView] tag in order to determine if a completed [Bitmap] loading request result is still valid and
 * should be set to the aforementioned [ImageView].
 */
class ImageLoader(
    private val executor: ExecutorService,
    private val bitmapUrlLoader: BitmapUrlLoader,
    private val lruCache: LruCache<String, Bitmap>
) {

    private val imageViewToFuture: HashMap<ImageView, Future<*>> = HashMap()

    /**
     * Loads [Bitmap] from given URL and sets it to given [ImageView]
     * @param imageUrl to load image from
     * @param view to set loaded image to
     */
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
            } catch (exception: Exception) {
                Log.e(ImageLoader::class.java.simpleName, exception.message)
                exception.printStackTrace()
            }
        }
    }

}
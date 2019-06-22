package com.project.gallery.image

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

class ImageLoader(
    private val executor: ExecutorService,
    private val bitmapUrlLoader: BitmapUrlLoader
) {

    private var context: Context? = null
    private val urlToFuture: HashMap<String, Future<*>> = HashMap()

    fun initialize(context: Context) {
        this.context = context
    }

    fun load(imageUrl: String, view: ImageView) {
        view.tag = imageUrl

        val future = urlToFuture[imageUrl]
        future?.let {
            it.cancel(true)
        }

        urlToFuture[imageUrl] = executor.submit {
            val bitmap = bitmapUrlLoader.load(imageUrl)

            bitmap?.let {
                if (view.tag == imageUrl) {
                    setBitmapToImageView(it, view)
                }
            }
        }
    }

    private fun setBitmapToImageView(bitmap: Bitmap, view: ImageView) {
        view.post {
            view.setImageBitmap(bitmap)
        }
    }
}
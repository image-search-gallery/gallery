package com.project.gallery.image

import android.graphics.Bitmap

/**
 * Provides [Bitmap] from URL.
 */
interface BitmapUrlLoader {
    /**
     * @return [Bitmap] loaded from given URL or `null` if loading isn't possible.
     * @param url to load image from.
     */
    fun load(url: String) : Bitmap?
}
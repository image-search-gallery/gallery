package com.project.gallery.image

import android.graphics.Bitmap

interface BitmapUrlLoader {
    fun load(url: String) : Bitmap?
}
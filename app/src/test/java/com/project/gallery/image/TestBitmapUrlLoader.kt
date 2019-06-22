package com.project.gallery.image

import android.graphics.Bitmap
import com.project.gallery.image.BitmapUrlLoader

class TestBitmapUrlLoader : BitmapUrlLoader {
    override fun load(url: String): Bitmap? {
        return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    }
}
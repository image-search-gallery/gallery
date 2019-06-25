@file:Suppress("IllegalIdentifier")

package com.project.gallery.image

import android.graphics.Bitmap
import android.util.LruCache
import android.widget.ImageView
import com.google.common.truth.Truth.assertThat
import com.project.gallery.ApplicationComponent
import com.project.gallery.utils.TestExecutorService
import com.project.gallery.utils.willReturn
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ImageLoaderTest {
    @get:Rule
    var rule = MockitoJUnit.rule()

    @Mock
    lateinit var bitmapUrlLoader : BitmapUrlLoader

    lateinit var imageLoader : ImageLoader

    @Before
    fun setUp(){
        imageLoader = ImageLoader(TestExecutorService(), bitmapUrlLoader, getBitmapLruCache())

        bitmapUrlLoader.load(anyString()) willReturn Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    }

    private fun getBitmapLruCache() : LruCache<String, Bitmap> {
        val maxMemoryBytes = Runtime.getRuntime().maxMemory()
        val cacheSize = (maxMemoryBytes / 4).toInt()

        return object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.allocationByteCount
            }
        }
    }

    @Test
    fun `Image from URL is loaded to image view`() {
        // Given
        val url = "url"
        val imageView = ImageView(RuntimeEnvironment.systemContext)

        // When
        imageLoader.load(url, imageView)

        // Then
        assertThat(imageView.tag).isEqualTo(url)
        assertThat(imageView.drawable).isNotNull()
    }

    @Test
    fun `Image from URL is loaded to already used image view`(){
        // Given
        val firstUrl = "firstUrl"
        val secondUrl = "secondUrl"
        val imageView = ImageView(RuntimeEnvironment.systemContext)

        // When
        imageLoader.load(firstUrl, imageView)
        imageLoader.load(secondUrl, imageView)

        // Then
        assertThat(imageView.tag).isEqualTo(secondUrl)
        assertThat(imageView.drawable).isNotNull()
    }

    @Test
    fun `Requesting same url uses bitmap loader once`(){
        // Given
        val url = "url"
        val imageView = ImageView(RuntimeEnvironment.systemContext)

        // When
        imageLoader.load(url, imageView)
        imageLoader.load(url, imageView)

        // Then
        verify(bitmapUrlLoader).load(url)
    }

    @Test
    fun `Requesting same url for different views uses bitmap loader once`(){
        // Given
        val url = "url"
        val firstImageView = ImageView(RuntimeEnvironment.systemContext)
        val secondImageView = ImageView(RuntimeEnvironment.systemContext)

        // When
        imageLoader.load(url, firstImageView)
        imageLoader.load(url, secondImageView)

        // Then
        verify(bitmapUrlLoader).load(url)
    }
}
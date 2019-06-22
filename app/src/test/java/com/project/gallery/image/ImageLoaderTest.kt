@file:Suppress("IllegalIdentifier")

package com.project.gallery.image

import android.widget.ImageView
import com.google.common.truth.Truth.assertThat
import com.project.gallery.utils.TestExecutorService
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ImageLoaderTest {


    val imageLoader = ImageLoader(TestExecutorService(), TestBitmapUrlLoader())

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

}
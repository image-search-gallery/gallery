package com.project.gallery.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.project.gallery.R
import com.project.gallery.search.data.repository.InMemoryImageRepository
import com.project.gallery.search.data.repository.flickr.FlickrImageRepository
import com.project.gallery.search.data.repository.flickr.JsonResponseDeserializer
import com.project.gallery.search.domain.GallerySearchInteractor
import com.project.gallery.utils.Throttler
import kotlinx.android.synthetic.main.gallery_search_activity.*
import java.util.concurrent.Executors

class GallerySearchActivity : AppCompatActivity() {

    lateinit var interactor: GallerySearchInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_search_activity)

        interactor = GallerySearchInteractor(
            FlickrImageRepository(JsonResponseDeserializer(), Executors.newCachedThreadPool()),
            gallerySearchView,
            Throttler(100)
        )
    }

    override fun onStart() {
        super.onStart()
        interactor.start()
    }

    override fun onStop() {
        super.onStop()
        interactor.stop()
    }
}

package com.project.gallery.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.project.gallery.ApplicationComponent
import com.project.gallery.R
import com.project.gallery.search.domain.GallerySearchInteractor
import kotlinx.android.synthetic.main.gallery_search_activity.*

/**
 * Displays search field which performs dynamic search and search result items in a grid.
 */
class GallerySearchActivity : AppCompatActivity() {

    lateinit var interactor: GallerySearchInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_search_activity)

        interactor = GallerySearchComponent(
            gallerySearchView,
            ApplicationComponent
        ).interactor
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
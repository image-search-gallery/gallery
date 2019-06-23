package com.project.gallery.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.project.gallery.R
import com.project.gallery.search.data.repository.InMemoryImageRepository
import com.project.gallery.search.domain.GallerySearchInteractor
import com.project.gallery.utils.Throttler
import kotlinx.android.synthetic.main.gallery_search_activity.*

class GallerySearchActivity : AppCompatActivity() {

    lateinit var interactor: GallerySearchInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_search_activity)

        interactor = GallerySearchInteractor(InMemoryImageRepository(), gallerySearchView, Throttler(100))
    }

    override fun onStart() {
        super.onStart()
        interactor.start()
//        interactor.search(InMemoryImageRepository.KITTENS_KEYWORD)
    }
}

package com.project.gallery.search

import com.project.gallery.ApplicationComponent
import com.project.gallery.search.domain.GallerySearchInteractor
import com.project.gallery.search.view.GallerySearchPresenter
import com.project.gallery.utils.Throttler

/**
 * Provides dependencies specific to gallery search screen.
 */
class GallerySearchComponent(
    private val presenter: GallerySearchPresenter,
    private val applicationComponent: ApplicationComponent
) {

    val interactor by lazy {
        GallerySearchInteractor(
            applicationComponent.imageRepository,
            presenter,
            Throttler(100)
        )
    }
}
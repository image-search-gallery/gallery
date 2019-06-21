package com.project.gallery.search.domain

import com.project.gallery.search.data.repository.ImagePaginator
import com.project.gallery.search.data.repository.ImageRepository
import com.project.gallery.search.view.GallerySearchPresenter
import com.project.gallery.search.view.GallerySearchPresenter.GalleryItem.*
import com.project.gallery.search.view.GallerySearchPresenter.State
import com.project.gallery.search.view.GallerySearchPresenter.State.*

class GallerySearchInteractor(
    private val repository: ImageRepository,
    private val presenter: GallerySearchPresenter
) {

    var currentState: State = Empty
    var currentPaginator: ImagePaginator? = null

    fun start() {
        presenter.updateState(Empty)
    }

    fun stop() {

    }

    fun search(keyword: String) {
        presenter.updateState(Loading)

        val imagePaginator = repository.search(keyword)

        imagePaginator.subscribeForImageUpdates(object : ImagePaginator.ImageUpdatesListener {
            override fun update(imageUrls: List<String>) {
                currentState = Ready(imageUrls.map { ImageItem(it) })
                presenter.updateState(currentState)
            }
        })

        imagePaginator.loadNext()

        currentPaginator = imagePaginator
    }

    fun loadNext() {
        val state = currentState
        if (state is Ready) {
            presenter.updateState(Ready(state.images + LoadingItem))
        }

        currentPaginator?.loadNext()
    }
}
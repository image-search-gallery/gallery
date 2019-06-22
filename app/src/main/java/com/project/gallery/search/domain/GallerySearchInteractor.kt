package com.project.gallery.search.domain

import com.project.gallery.search.data.repository.ImagePaginator
import com.project.gallery.search.data.repository.ImageRepository
import com.project.gallery.search.view.GallerySearchPresenter
import com.project.gallery.search.view.GallerySearchPresenter.GalleryItem.ImageItem
import com.project.gallery.search.view.GallerySearchPresenter.GalleryItem.LoadingItem
import com.project.gallery.search.view.GallerySearchPresenter.State
import com.project.gallery.search.view.GallerySearchPresenter.State.*

class GallerySearchInteractor(
    private val repository: ImageRepository,
    private val presenter: GallerySearchPresenter
) : GallerySearchPresenter.Listener {

    private var currentState: State = Empty
    private var currentPaginator: ImagePaginator? = null
    private var currentImageUpdatesListener: ImagePaginator.ImageUpdatesListener? = null

    fun start() {
        presenter.updateState(Empty)
    }

    fun stop() {

    }

    override fun search(keyword: String) {
        presenter.updateState(Loading)

        val imagePaginator = repository.search(keyword)
        val paginatorListener = ImageUpdatesListenerImpl()

        imagePaginator.subscribeForImageUpdates(paginatorListener)

        imagePaginator.loadNext()

        currentImageUpdatesListener?.let {
            currentPaginator?.unsubscribeFromImageUpdates(it)
        }

        currentPaginator = imagePaginator
        currentImageUpdatesListener = paginatorListener
    }

    override fun loadNext() {
        val state = currentState
        if (state is Ready) {
            presenter.updateState(Ready(state.images + LoadingItem))
        }

        currentPaginator?.loadNext()
    }

    inner class ImageUpdatesListenerImpl : ImagePaginator.ImageUpdatesListener {
        override fun update(imageUrls: List<String>) {
            currentState = Ready(imageUrls.map { ImageItem(it) })
            presenter.updateState(currentState)
        }
    }
}
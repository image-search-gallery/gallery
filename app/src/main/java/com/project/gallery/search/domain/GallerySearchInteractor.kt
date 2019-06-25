package com.project.gallery.search.domain

import com.project.gallery.search.data.repository.ImagePaginator
import com.project.gallery.search.data.repository.ImageRepository
import com.project.gallery.search.view.GallerySearchPresenter
import com.project.gallery.search.view.GallerySearchPresenter.GalleryItem.ImageItem
import com.project.gallery.search.view.GallerySearchPresenter.GalleryItem.LoadingItem
import com.project.gallery.search.view.GallerySearchPresenter.State
import com.project.gallery.search.view.GallerySearchPresenter.State.*
import com.project.gallery.utils.Throttler
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Contains business logic for [GallerySearchPresenter].
 */
class GallerySearchInteractor(
    private val repository: ImageRepository,
    private val presenter: GallerySearchPresenter,
    private val throttler: Throttler
) : GallerySearchPresenter.ViewEventsListener {

    private var currentState: State = Empty
    private var currentPaginator: ImagePaginator? = null
    private val currentImageUpdatesListener = ImageUpdatesListenerImpl()

    private var loading = AtomicBoolean(false)

    /**
     * Puts [GallerySearchInteractor] in a working state meaning it will start processing for data updates and handle
     * [GallerySearchPresenter.ViewEventsListener] calls.
     */
    fun start() {
        presenter.setListener(this)
        presenter.updateState(Empty)

        currentPaginator?.subscribeForImageUpdates(currentImageUpdatesListener)
    }

    /**
     * Puts [GallerySearchInteractor] in a stopped state meaning it will no longer process and receive data updates and
     * it will stop handling [GallerySearchPresenter.ViewEventsListener] calls.
     */
    fun stop() {
        unsubscribe()
    }

    override fun search(keyword: String) {
        loading.compareAndSet(false, true)

        if (keyword.length < 3) {
            presenter.updateState(Empty)
            unsubscribe()
            currentPaginator = null
            return
        }


        throttler.submit {

            presenter.updateState(Loading)
            val imagePaginator = repository.search(keyword)

            unsubscribe()
            imagePaginator.subscribeForImageUpdates(currentImageUpdatesListener)

            imagePaginator.loadNext()


            currentPaginator = imagePaginator
        }
    }

    private fun unsubscribe() {
        currentPaginator?.unsubscribeFromImageUpdates(currentImageUpdatesListener)
    }

    override fun loadNext() {

        if (!loading.get() && currentPaginator?.hasNext() == true) {
            val state = currentState
            if (state is Ready) {
                presenter.updateState(Ready(state.images + LoadingItem))
            }

            currentPaginator?.loadNext()

            loading.compareAndSet(false, true)
        }

    }

    private inner class ImageUpdatesListenerImpl : ImagePaginator.ImageUpdatesListener {
        override fun onUpdate(imageUrls: List<String>) {
            currentState = Ready(imageUrls.map { ImageItem(it) })
            presenter.updateState(currentState)

            loading.compareAndSet(true, false)
        }

        override fun onError(error: Exception) {
            presenter.updateState(Failed)
        }
    }
}
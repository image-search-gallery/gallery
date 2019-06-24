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

class GallerySearchInteractor(
    private val repository: ImageRepository,
    private val presenter: GallerySearchPresenter,
    private val throttler: Throttler
) : GallerySearchPresenter.ViewEventsListener {

    private var currentState: State = Empty
    private var currentPaginator: ImagePaginator? = null
    private val currentImageUpdatesListener = ImageUpdatesListenerImpl()

    private var loading = AtomicBoolean(false)

    fun start() {
        println("Start")
        presenter.setListener(this)
        presenter.updateState(Empty)

        currentPaginator?.subscribeForImageUpdates(currentImageUpdatesListener)
    }

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

    inner class ImageUpdatesListenerImpl : ImagePaginator.ImageUpdatesListener {
        override fun update(imageUrls: List<String>) {
            currentState = Ready(imageUrls.map { ImageItem(it) })
            presenter.updateState(currentState)

            loading.compareAndSet(true, false)
        }

        override fun onError(error: Exception) {
            println("On Error")
            presenter.updateState(Failed)
        }
    }
}
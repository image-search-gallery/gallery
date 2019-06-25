package com.project.gallery.search.view

interface GallerySearchPresenter {

    fun updateState(state: State)

    /**
     * Represents all possible states for gallery view.
     */
    sealed class State {
        data class Ready(val images: List<GalleryItem>) : State()

        object Loading : State()
        object Empty : State()
        object Failed : State()
    }

    /**
     * Represents all possible states for gallery image item.
     */
    sealed class GalleryItem {
        data class ImageItem(val image: String) : GalleryItem()
        object LoadingItem : GalleryItem()
    }

    fun setListener(listener: ViewEventsListener)

    interface ViewEventsListener{
        fun search(keyword: String)
        fun loadNext()
    }
}
package com.project.gallery.search.view

/**
 * Describes all possible presentation states for a view to handle in order to show gallery search screen.
 */
interface GallerySearchPresenter {

    /**
     * Process update for a view state.
     */
    fun updateState(state: State)

    /**
     * Represents all possible states for a gallery search screen.
     */
    sealed class State {
        /**
         * Ready to display images.
         */
        data class Ready(val images: List<GalleryItem>) : State()

        /**
         * Search result processing.
         */
        object Loading : State()

        /**
         * Nothing to show.
         */
        object Empty : State()

        /**
         * Failed to process search result.
         */
        object Failed : State()
    }

    /**
     * Represents all possible states for gallery image item.
     */
    sealed class GalleryItem {
        /**
         * Ready to show image as URL represented by string.
         */
        data class ImageItem(val image: String) : GalleryItem()
        object LoadingItem : GalleryItem()
    }

    /**
     * Sets listener to precess search result screen specific events.
     */
    fun setListener(listener: ViewEventsListener)

    /**
     * Listener for search result screen.
     */
    interface ViewEventsListener{
        /**
         * Should perform search for a given word.
         * @param keyword to perform search on
         */
        fun search(keyword: String)

        /**
         * Should load next page of images.
         */
        fun loadNext()
    }
}
package com.project.gallery.search.data.repository

/**
 * Serves as an iterator through search result. Must be retrieved using [ImageRepository.search].
 */
interface ImagePaginator {
    /**
     * Initiates loading of next page of images. Use [subscribeForImageUpdates] to register
     * [ImageUpdatesListener] in order to receive image updates and error reports in case of failure.
     */
    fun loadNext()

    /**
     * @return `true` if the currently loaded page is not the last available page and `false` otherwise.
     */
    fun hasNext(): Boolean

    /**
     * Registers [ImageUpdatesListener] in order to receive image updates and error reports in case of failure.
     */
    fun subscribeForImageUpdates(listener: ImageUpdatesListener)

    /**
     * Unregisters [ImageUpdatesListener] so it no longer receives updates or error reports.
     */
    fun unsubscribeFromImageUpdates(listener: ImageUpdatesListener)

    /**
     * Provides image updates and error reports.
     */
    interface ImageUpdatesListener {
        /**
         * Provides a successful image page update from the search result.
         */
        fun onUpdate(imageUrls: List<String>)

        /**
         * Provides error report in case if procuring page update has failed.
         */
        fun onError(error: Exception)
    }
}
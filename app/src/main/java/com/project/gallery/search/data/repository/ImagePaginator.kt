package com.project.gallery.search.data.repository

interface ImagePaginator {
    // TODO: add hasNext
    fun loadNext()
    fun subscribeForImageUpdates(listener: ImageUpdatesListener)
    fun unsubscribeFromImageUpdates(listener: ImageUpdatesListener)

    interface ImageUpdatesListener{
        fun update(imageUrls : List<String>)
    }
}
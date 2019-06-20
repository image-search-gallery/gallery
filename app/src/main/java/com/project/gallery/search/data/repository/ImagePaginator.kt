package com.project.gallery.search.data.repository

interface ImagePaginator {

    fun loadNext()
    fun subscribeForImageUpdates(listener: ImageUpdatesListener) : ImagePaginator
    fun unsubscribeFromImageUpdates(listener: ImageUpdatesListener)

    interface ImageUpdatesListener{
        fun update(imageUrls : List<String>)
    }
}
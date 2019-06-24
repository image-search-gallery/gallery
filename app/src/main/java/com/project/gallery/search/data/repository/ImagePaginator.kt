package com.project.gallery.search.data.repository

import java.lang.Exception

interface ImagePaginator {
    // TODO: add hasNext
    fun loadNext()
    fun hasNext() : Boolean
    fun subscribeForImageUpdates(listener: ImageUpdatesListener)
    fun unsubscribeFromImageUpdates(listener: ImageUpdatesListener)

    interface ImageUpdatesListener{
        fun update(imageUrls : List<String>)
        fun onError(error: Exception)
    }
}
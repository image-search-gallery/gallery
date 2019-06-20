package com.project.gallery.search.data.repository

interface ImageRepository{
    fun search()
    fun loadNext()
    fun subscribeForImageUpdates(listener: ImagePaginator.ImageUpdatesListener)
    fun unsubscribeFromImageUpdates(listener: ImagePaginator.ImageUpdatesListener)
}
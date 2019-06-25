package com.project.gallery.utils

import com.project.gallery.search.data.repository.ImagePaginator
import com.project.gallery.search.data.repository.ImageRepository

class PushImageRepository : ImageRepository {

    private val keywordToPaginator: HashMap<String, PushImagePaginator> = HashMap()
    private var lastPaginator: PushImagePaginator? = null


    fun push(keyword: String, newImages: List<String>) {
        keywordToPaginator[keyword]?.push(newImages)
    }

    fun pushError() {
        lastPaginator?.notifyListenersAboutFailure(Exception())
    }

    override fun search(keyword: String) = keywordToPaginator.getOrPut(keyword, { PushImagePaginator() }).also { lastPaginator = it }

    inner class PushImagePaginator : ImagePaginator {
        private val listeners = arrayListOf<ImagePaginator.ImageUpdatesListener>()

        private var loading = false

        fun push(newImages: List<String>) {
            if (loading) {
                synchronized(listeners) {
                    listeners.forEach {
                        it.onUpdate(newImages)
                    }
                }
            }
        }

        override fun loadNext() {
            loading = true
        }

        override fun hasNext() = true

        override fun subscribeForImageUpdates(listener: ImagePaginator.ImageUpdatesListener) {
            synchronized(listeners) {
                listeners.add(listener)
            }
        }

        override fun unsubscribeFromImageUpdates(listener: ImagePaginator.ImageUpdatesListener) {
            synchronized(listeners) {
                listeners.remove(listener)
            }
        }

        fun notifyListenersAboutFailure(exception: Exception) {
            synchronized(listeners) {
                listeners.forEach {
                    it.onError(exception)
                }
            }
        }
    }
}
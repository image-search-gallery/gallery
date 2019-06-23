package com.project.gallery.search.data.repository.flickr

import android.util.Log
import com.project.gallery.search.data.repository.ImagePaginator
import com.project.gallery.search.data.repository.ImageRepository
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.Executor

class FlickrImageRepository(
    private val jsonResponseDeserializer: JsonResponseDeserializer,
    private val executor: Executor
) : ImageRepository {

    companion object {
        private const val API_KEY = "3e7cc266ae2b0e0d78e279ce8e361736"
    }

    private fun buildUrl(keyword: String, pageNumber: Int): URL {
        return URL(
            "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=$API_KEY&format=json&nojsoncallback=1&safe_search=1&page=$pageNumber&text=${URLEncoder.encode(
                keyword,
                "UTF-8"
            )};"
        )
    }

    private fun performSearchRequest(url: URL): String {
        val urlConnection = url.openConnection() as HttpURLConnection
        try {
            urlConnection.inputStream.use {
                it.reader().use { reader ->
                    return reader.readText()
                }
            }
        } finally {
            urlConnection.disconnect()
        }
    }

    override fun search(keyword: String) = FlickrImagePaginator(keyword)

    inner class FlickrImagePaginator(private val keyword: String) : ImagePaginator {
        private val listeners = arrayListOf<ImagePaginator.ImageUpdatesListener>()

        private var currentImageUrls = listOf<String>()

        private var currentPage = 0
        private var totalPages = Int.MAX_VALUE

        override fun loadNext() {
            val page = currentPage
            val maxPages = totalPages

            if (page >= maxPages) {
                return
            }

            executor.execute {
                val url = buildUrl(keyword, page)

                try {
                    val searchResult = performSearchRequest(url)
                    val (resultPage, resultPages, resultImages) = jsonResponseDeserializer.parseResult(searchResult)

                    currentPage = resultPage
                    totalPages = resultPages
                    currentImageUrls = currentImageUrls + resultImages

                    notifyListeners()

                    currentPage++
                } catch (e: IOException) {
                    Log.e("FlickrImageRepository", e.message, e)
                }
            }
        }

        private fun notifyListeners() {
            synchronized(listeners) {
                listeners.forEach {
                    it.update(currentImageUrls)
                }
            }
        }

        override fun subscribeForImageUpdates(listener: ImagePaginator.ImageUpdatesListener) {
            synchronized(listeners) {
                listeners.add(listener)
            }

            notifyListeners()
        }

        override fun unsubscribeFromImageUpdates(listener: ImagePaginator.ImageUpdatesListener) {
            synchronized(listeners) {
                listeners.remove(listener)
            }
        }
    }
}
package com.project.gallery

import com.project.gallery.search.data.repository.flickr.FlickrImageRepository
import com.project.gallery.search.data.repository.flickr.JsonResponseDeserializer
import java.util.concurrent.Executors

object ApplicationComponent {

    val imageRepository by lazy {
        FlickrImageRepository(JsonResponseDeserializer(), Executors.newCachedThreadPool())
    }
}
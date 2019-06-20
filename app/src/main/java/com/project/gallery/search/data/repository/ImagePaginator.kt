package com.project.gallery.search.data.repository

interface ImagePaginator {
    interface ImageUpdatesListener{
        fun update(imageUrls : List<String>)
    }
}
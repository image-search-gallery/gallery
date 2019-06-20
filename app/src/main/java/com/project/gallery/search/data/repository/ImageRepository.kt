package com.project.gallery.search.data.repository

interface ImageRepository{
    fun search(keyword: String) : ImagePaginator
}
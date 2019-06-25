package com.project.gallery.search.data.repository

/**
 * Provides possibility to search through images using search keyword.
 */
interface ImageRepository {
    /**
     * @return [ImagePaginator] which acts as an iterator to a search result pages for given keyword.
     * @param keyword to perform search with
     */
    fun search(keyword: String): ImagePaginator
}
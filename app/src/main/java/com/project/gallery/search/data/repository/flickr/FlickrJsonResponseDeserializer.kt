package com.project.gallery.search.data.repository.flickr

import org.json.JSONException
import org.json.JSONObject

/**
 * Used to deserialize JSON response to Flickr.
 * Provides [Response] containing current page, total pages, and images from current page.
 */
class FlickrJsonResponseDeserializer {
    /**
     * Parses Flickr search result into a [Response].
     */
    @Throws(JSONException::class)
    fun parseResult(result: String): Response {

        val root = JSONObject(result)
        val photos = root.getJSONObject("photos")
        val photoList = photos.getJSONArray("photo")

        val images = arrayListOf<String>()

        for (i in 0 until photoList.length()) {
            val photoDetails = photoList.getJSONObject(i)

            val farm = photoDetails.getInt("farm")
            val server = photoDetails.getString("server")
            val imageId = photoDetails.getString("id")
            val secret = photoDetails.getString("secret")

            val imageUrl = "https://farm$farm.static.flickr.com/$server/${imageId}_$secret.jpg"

            images.add(imageUrl)
        }

        return Response(
            photos.getInt("page"),
            photos.getInt("pages"),
            images
        )
    }

    /**
     *  Represents search response from Flickr. Contains current page, total pages, and images from current page.
     */
    data class Response(
        /**
         * Current page.
         */
        val page: Int,
        /**
         * Total pages in search result.
         */
        val pages: Int,
        /**
         * Images from current page for searchh result.
         */
        val images: List<String>
    )
}
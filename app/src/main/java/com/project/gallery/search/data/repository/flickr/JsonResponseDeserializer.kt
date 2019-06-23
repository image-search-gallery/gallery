package com.project.gallery.search.data.repository.flickr

import org.json.JSONException
import org.json.JSONObject

class JsonResponseDeserializer {
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

    data class Response(
        val page: Int,
        val pages: Int,
        val images: List<String>
    )
}
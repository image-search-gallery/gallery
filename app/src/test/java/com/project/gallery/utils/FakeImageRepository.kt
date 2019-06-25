package com.project.gallery.utils

import com.project.gallery.search.data.repository.ImagePaginator
import com.project.gallery.search.data.repository.ImageRepository

/**
 * [ImageRepository] implementation which uses hard-coded URLs.
 * Search is possible for the following keywords: [PUPPIES_KEYWORD], [KITTENS_KEYWORD]. Any other search request will
 * result in empty list.
 * Used mainly in testing.
 * [FakeImagePaginator] provided by this implementation isn't thread safe.
 */
class FakeImageRepository : ImageRepository {

    companion object {
        const val PUPPIES_KEYWORD = "puppies"
        const val KITTENS_KEYWORD = "kittens"
        const val pageSize = 30

        val kittens = (0..100).flatMap {
            listOf(
                "https://pbs.twimg.com/profile_images/638743277865271297/3pSO-2xR_400x400.jpg",
                "https://i.pinimg.com/originals/3b/8b/b9/3b8bb9d93cc193af1e351fe77ab950da.jpg",
                "https://cdn6.aptoide.com/imgs/a/9/c/a9ca0834d77090715966ee161600e5e4_icon.png?w=256",
                "https://pbs.twimg.com/profile_images/378800000564268753/4034c9ab7914ad5729f2ace5e329fae7.jpeg",
                "https://pbs.twimg.com/profile_images/694718778911764480/3dH7jGTE_400x400.jpg"
            )
        }

        val puppies = (0..100).flatMap {
            listOf(
                "https://2puppies.com/api/media/cache/listing_small/1556478855_1501573615cc5fb87d627a8.15042060.jpeg",
                "https://www.parkerspreciouspuppies.com/_Media/paris6-parkerspreciouspuppi_hr.jpeg",
                "https://www.pets4homes.co.uk/images/classifieds/2016/02/24/1212031/large/golden-retriever-puppies-for-sale-56cda0857c9fe.jpg",
                "https://is2-ssl.mzstatic.com/image/thumb/Purple/v4/d1/b0/15/d1b01506-c05f-021f-c7b5-35cb48e8c2d4/source/256x256bb.jpg",
                "https://mishkanasevere.com/wp-content/uploads/2018/07/QuinoaCookies-thegem-post-thumb-large.jpg",
                "https://www.wildcoyotefarm.com/uploads/7/1/0/8/71089429/img-2429.jpg",
                "https://www.parkerspreciouspuppies.com/_Media/brianna5-parkerspreciouspup_hr.jpeg",
                "https://petergreenberg.com/wp-content/uploads/2008/07/puppy-dog.jpg",
                "https://www.cheatlakevets.com/wp-content/uploads/2019/01/great-outdoorsman.jpg"
            )
        }
    }

    override fun search(keyword: String): ImagePaginator {
        return FakeImagePaginator(keyword)
    }

    /**
     * [ImagePaginator] implementation which uses hard-coded strings as a search result.
     */
    inner class FakeImagePaginator(private val keyword: String) :
        ImagePaginator {

        private val maxImageCollectionIndex = when (keyword) {
            KITTENS_KEYWORD -> kittens.size - 1
            PUPPIES_KEYWORD -> puppies.size - 1
            else -> 0
        }

        private var nextImageIndex = 0

        private val listeners = arrayListOf<ImagePaginator.ImageUpdatesListener>()

        override fun hasNext(): Boolean {
            return nextImageIndex + pageSize <= maxImageCollectionIndex
        }

        override fun loadNext() {
            listeners.forEach {
                val newNextImageIndex = nextImageIndex + pageSize
                nextImageIndex = if (hasNext()) newNextImageIndex else maxImageCollectionIndex
                it.onUpdate(
                    when (keyword) {
                        KITTENS_KEYWORD -> kittens.subList(0, nextImageIndex)
                        PUPPIES_KEYWORD -> puppies.subList(0, nextImageIndex)
                        else -> emptyList()
                    }
                )
            }
        }

        override fun subscribeForImageUpdates(listener: ImagePaginator.ImageUpdatesListener) {
            listeners.add(listener)
        }

        override fun unsubscribeFromImageUpdates(listener: ImagePaginator.ImageUpdatesListener) {
            listeners.remove(listener)
        }
    }
}
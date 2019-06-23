package com.project.gallery.search.data.repository

class InMemoryImageRepository : ImageRepository {

    companion object {
        const val PUPPIES_KEYWORD = "puppies"
        const val KITTENS_KEYWORD = "kittens"
        const val pageSize = 4


        val kittens = listOf(
            "https://pbs.twimg.com/profile_images/638743277865271297/3pSO-2xR_400x400.jpg",
            "http://www.faydark-coon.com/images/photos/2/allywood-loretta-1.jpg",
            "http://faydark-coon.com/images/photos/11/faydark-blackberry-1.jpg",
            "http://www.faydark-coon.com/images/photos/351/faydark-glorfindel-32.jpg",
            "http://www.tiltingtowardwindmills.com/wp-content/uploads/2018/11/Sure-Seems-Like-Monday-256x256.jpg",
            "http://search.lostpetusa.net/petimages/256/69479d5b-df04-49eb-ae13-f2f1d89defdc.jpg",
            "https://i.pinimg.com/originals/3b/8b/b9/3b8bb9d93cc193af1e351fe77ab950da.jpg",
            "http://www.tiltingtowardwindmills.com/wp-content/uploads/2017/09/Sid-too-big-to-fit-2-256x256.jpg",
            "https://img.olx.com.br/thumbs256x256/45/457929002900798.jpg",
            "https://cdn6.aptoide.com/imgs/a/9/c/a9ca0834d77090715966ee161600e5e4_icon.png?w=256"
        )

        val puppies = listOf(
            "https://2puppies.com/api/media/cache/listing_small/1556478855_1501573615cc5fb87d627a8.15042060.jpeg",
            "http://www.parkerspreciouspuppies.com/_Media/paris6-parkerspreciouspuppi_hr.jpeg",
            "https://www.pets4homes.co.uk/images/classifieds/2016/02/24/1212031/large/golden-retriever-puppies-for-sale-56cda0857c9fe.jpg",
            "https://is2-ssl.mzstatic.com/image/thumb/Purple/v4/d1/b0/15/d1b01506-c05f-021f-c7b5-35cb48e8c2d4/source/256x256bb.jpg",
            "https://mishkanasevere.com/wp-content/uploads/2018/07/QuinoaCookies-thegem-post-thumb-large.jpg",
            "https://www.wildcoyotefarm.com/uploads/7/1/0/8/71089429/img-2429.jpg",
            "http://www.parkerspreciouspuppies.com/_Media/brianna5-parkerspreciouspup_hr.jpeg",
            "https://petergreenberg.com/wp-content/uploads/2008/07/puppy-dog.jpg",
            "http://heatherwoodgoldens.com/sitebuilder/images/7_weeks0-256x256.jpg",
            "https://www.cheatlakevets.com/wp-content/uploads/2019/01/great-outdoorsman.jpg"
        )
    }

    override fun search(keyword: String): ImagePaginator {
        return InMemoryImagePaginator(keyword)
    }

    inner class InMemoryImagePaginator(val keyword: String) : ImagePaginator {

        private val maxImageCollectionIndex = when (keyword) {
            KITTENS_KEYWORD -> kittens.size - 1
            PUPPIES_KEYWORD -> puppies.size - 1
            else -> 0
        }

        var nextImageIndex = 0

        private val listeners = arrayListOf<ImagePaginator.ImageUpdatesListener>()

        override fun loadNext() {
            synchronized(listeners) {
                listeners.forEach {
                    val newNextImageIndex = nextImageIndex + pageSize
                    nextImageIndex = if (newNextImageIndex <= maxImageCollectionIndex) newNextImageIndex else maxImageCollectionIndex
                    it.update(
                        when (keyword) {
                            KITTENS_KEYWORD -> kittens.subList(0, nextImageIndex)
                            PUPPIES_KEYWORD -> puppies.subList(0, nextImageIndex)
                            else -> emptyList()
                        }
                    )
                }
            }
        }

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
    }
}
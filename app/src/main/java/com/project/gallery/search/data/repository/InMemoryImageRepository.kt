package com.project.gallery.search.data.repository

class InMemoryImageRepository : ImageRepository {

    companion object {
        const val PUPPIES_KEYWORD = "puppies"
        const val KITTENS_KEYWORD = "kittens"
    }

    private val kittens = listOf(
        "https://shop-cdn-m.shpp.ext.zooplus.io/bilder/royal/canin/maine/coon/kitten/9/400/rc_maincoonk_9.jpg",
        "http://powerfulmind.co/wp-content/uploads/2019/02/maine-coon-kittens-37-5c34c902b5aa5__700.jpg",
        "https://www.thehappycatsite.com/wp-content/uploads/2017/12/maine-coon-kittens.jpg",
        "http://www.cascademountain.net/kittens/dec-15/5-2-16.jpg",
        "https://image.jimcdn.com/app/cms/image/transf/dimension=910x10000:format=jpg/path/sa855c229b4088494/image/i1782b70780124ebe/version/1499385450/extra-large-maine-coon-kittens-for-sale.jpg",
        "https://saltycoons.com/wp-content/uploads/2018/04/5AFCF968-3084-45B2-A914-1D42B752D58C.jpeg",
        "https://imgc.allpostersimages.com/img/print/poster/mark-taylor-two-maine-coon-kittens-8-weeks-one-with-its-paw-raised_a-G-10574993-14258389.jpg",
        "https://9b16f79ca967fd0708d1-2713572fef44aa49ec323e813b06d2d9.ssl.cf2.rackcdn.com/1140x_a10-7_cTC/20190122lf-Cats04-2-1549061487.jpg",
        "https://www.draegerparis.com/6115307-large_default/lara-maine-coon-kitten.jpg",
        "http://fallinpets.com/wp-content/uploads/2016/12/Adorable-Maine-Coon-Kitten.jpg"
    )

    private val puppies = listOf(
        "http://www.mishkaranch.com/photos/Turre_1.jpg",
        "https://gfp-2a3tnpzj.stackpathdns.com/wp-content/uploads/2016/07/Alaskan-Malamute-2.jpg",
        "https://s3.envato.com/files/253707280/2016_198_0302_P.jpg",
        "https://render.fineartamerica.com/images/rendered/default/metal-print/10.000/6.625/break/images-medium-5/alaskan-malamute-puppies-playing-in-garden-kurapatka.jpg",
        "https://teja8.kuikr.com/i4/20190520/Alaskan-malamute-puppies-available-VB201705171774173-ak_LWBP90877995-1558337853.jpeg",
        "https://i.ytimg.com/vi/YXxG0Xk3708/maxresdefault.jpg",
        "https://i0.wp.com/doglers.com/wp-content/uploads/2016/02/Alaskan-Malamute-Puppy-Photo.jpg",
        "https://cdn3-www.dogtime.com/assets/uploads/2019/05/alaskan-malamute-puppy-1.jpg",
        "https://www.europuppy.com/wp-content/uploads/2019/01/UA3801alm181023_F1_1.jpg",
        "http://www.cascademalamutes.com/uploads/8/2/9/1/82910972/max25.jpg"
    )

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
        val pageSize = 4

        private val listeners = arrayListOf<ImagePaginator.ImageUpdatesListener>()

        override fun loadNext() {
            synchronized(listeners) {
                listeners.forEach {
                    val newNextImageIndex = nextImageIndex + pageSize - 1
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
package com.project.gallery.search.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.project.gallery.R
import com.project.gallery.image.HttpsBitmapUrlLoader
import com.project.gallery.image.ImageLoader
import com.project.gallery.search.view.GallerySearchPresenter.*
import com.project.gallery.search.view.GallerySearchPresenter.State
import com.project.gallery.search.view.GallerySearchPresenter.State.*
import kotlinx.android.synthetic.main.gallery_search_grid_item.view.*
import kotlinx.android.synthetic.main.gallery_search_grid_view.view.*
import java.util.concurrent.Executors

class GallerySearchView(context: Context, attributeSet: AttributeSet) : GallerySearchPresenter,
    FrameLayout(context, attributeSet) {

    companion object {
        private const val GRID_COLUMN_COUNT = 3
        private const val REMAINING_ITEMS_BEFORE_NEXT_LOAD = 3 * GRID_COLUMN_COUNT
        private const val EXECUTORS_POOL_SIZE = 3
    }

    private val itemsAdapter = ImageAdapter(context)
    private var viewEventsListener: ViewEventsListener? = null
    private val imageLoader = ImageLoader(Executors.newFixedThreadPool(EXECUTORS_POOL_SIZE), HttpsBitmapUrlLoader())


    init {
        LayoutInflater
            .from(context)
            .inflate(R.layout.gallery_search_grid_view, this, true)

        (gallerySearchGrid as RecyclerView).apply {
            layoutManager = GridLayoutManager(context, GRID_COLUMN_COUNT)
            adapter = itemsAdapter
            isNestedScrollingEnabled = false
            addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val totalItemCount = layoutManager?.itemCount ?: 0
                    val lastVisible = (layoutManager as GridLayoutManager).findLastVisibleItemPosition()

                    val endHasBeenReached = lastVisible + REMAINING_ITEMS_BEFORE_NEXT_LOAD >= totalItemCount
                    if (totalItemCount > 0 && endHasBeenReached) {
                        viewEventsListener?.loadNext()
                    }
                }
            })
        }
    }

    override fun updateState(state: State) {
        when (state) {
            is Ready -> {
                showSearchResult()
                itemsAdapter.setItems(state.images)
            }

            is NoInternet -> showNoInternet()
            is Loading -> showInitialLoading()
        }
    }

    private fun showInitialLoading() {
        gallerySearchGrid.visibility = GONE
        noInternet.visibility = GONE

        initialLoading.visibility = VISIBLE
    }

    private fun showNoInternet() {
        initialLoading.visibility = GONE
        gallerySearchGrid.visibility = GONE

        noInternet.visibility = VISIBLE
    }

    private fun showSearchResult() {
        noInternet.visibility = GONE
        initialLoading.visibility = GONE

        gallerySearchGrid.visibility = VISIBLE
    }

    override fun setListener(listener: ViewEventsListener) {
        viewEventsListener = listener
    }

    private inner class ImageAdapter(context: Context) : Adapter<ViewHolder>() {

        private val READY_ITEM_TYPE = 0
        private val LOADING_ITEM_TYPE = 1

        private var items: List<GalleryItem> = emptyList()

        private val inflater: LayoutInflater = LayoutInflater.from(context)

        init {
            setHasStableIds(true)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return when (viewType) {
                READY_ITEM_TYPE -> {
                    SearchResultImageViewHolder(
                        inflater.inflate(
                            R.layout.gallery_search_grid_item,
                            parent,
                            false
                        )
                    )
                }
                else -> LoadingSearchResultImageViewHolder(
                    inflater.inflate(
                        R.layout.loading_gallery_search_grid_item,
                        parent,
                        false
                    )
                )
            }
        }

        override fun getItemCount() = items.size

        override fun getItemId(position: Int) = items[position].hashCode().toLong()

        override fun getItemViewType(position: Int): Int {
            return when(items[position]){
                is GalleryItem.ImageItem -> READY_ITEM_TYPE
                is GalleryItem.LoadingItem -> LOADING_ITEM_TYPE
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (position !in 0 until items.size) {
                return
            }

            when (holder) {
                is SearchResultImageViewHolder -> holder.setViewModel(items[position] as GalleryItem.ImageItem)
            }
        }

        fun setItems(items: List<GalleryItem>){
            this.items = items
            notifyDataSetChanged()
        }
    }

    private inner class SearchResultImageViewHolder(itemView: View) : ViewHolder(itemView) {
        val image = itemView.gallerySearchImage

        fun setViewModel(model: GalleryItem.ImageItem) {
            imageLoader.load(model.image, image)
        }
    }

    private class LoadingSearchResultImageViewHolder(itemView: View) : ViewHolder(itemView)
}
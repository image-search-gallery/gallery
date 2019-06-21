@file:Suppress("IllegalIdentifier")

package com.project.gallery.search.domain

import com.project.gallery.search.data.repository.InMemoryImageRepository
import com.project.gallery.search.data.repository.InMemoryImageRepository.Companion.kittens
import com.project.gallery.search.data.repository.InMemoryImageRepository.Companion.pageSize
import com.project.gallery.search.view.GallerySearchPresenter
import com.project.gallery.search.view.GallerySearchPresenter.GalleryItem
import com.project.gallery.search.view.GallerySearchPresenter.GalleryItem.ImageItem
import com.project.gallery.search.view.GallerySearchPresenter.GalleryItem.LoadingItem
import com.project.gallery.search.view.GallerySearchPresenter.State.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.inOrder
import org.mockito.junit.MockitoJUnit

class GallerySearchInteractorTest {

    @get:Rule
    var rule = MockitoJUnit.rule()

    @Mock
    lateinit var presenter: GallerySearchPresenter
    val repository = InMemoryImageRepository()

    lateinit var interactor: GallerySearchInteractor

    @Before
    fun setUp() {
        interactor = GallerySearchInteractor(repository, presenter)
    }

    @Test
    fun `Search passes result to presenter`() {
        // Given
        val expectedLoadedState = Ready(kittens
            .take(pageSize)
            .map {
                ImageItem(it)
            }
        )

        // When
        interactor.start()
        interactor.search(InMemoryImageRepository.KITTENS_KEYWORD)

        // Then
        inOrder(presenter).apply {
            verify(presenter).updateState(Empty)
            verify(presenter).updateState(Loading)
            verify(presenter).updateState(expectedLoadedState)
        }
    }

    @Test
    fun `Load next request passes new result to presenter`() {
        // Given
        val expectedFirstLoadedItems = kittens
            .take(pageSize)
            .map {
                ImageItem(it)
            }

        val expectedLoadingState = expectedFirstLoadedItems
            .toMutableList<GalleryItem>()
            .apply { add(LoadingItem) }

        val expectedSecondLoadedItems = kittens
            .take(pageSize * 2)
            .map {
                ImageItem(it)
            }

        // When
        interactor.start()
        interactor.search(InMemoryImageRepository.KITTENS_KEYWORD)
        interactor.loadNext()

        // Then
        inOrder(presenter).apply {
            verify(presenter).updateState(Empty)
            verify(presenter).updateState(Loading)
            verify(presenter).updateState(Ready(expectedFirstLoadedItems))
            verify(presenter).updateState(Ready(expectedLoadingState))
            verify(presenter).updateState(Ready(expectedSecondLoadedItems))
        }
    }
}
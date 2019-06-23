@file:Suppress("IllegalIdentifier")

package com.project.gallery.search.domain

import com.project.gallery.search.data.repository.InMemoryImageRepository
import com.project.gallery.search.data.repository.InMemoryImageRepository.Companion.kittens
import com.project.gallery.search.data.repository.InMemoryImageRepository.Companion.pageSize
import com.project.gallery.search.data.repository.InMemoryImageRepository.Companion.puppies
import com.project.gallery.search.view.GallerySearchPresenter
import com.project.gallery.search.view.GallerySearchPresenter.GalleryItem
import com.project.gallery.search.view.GallerySearchPresenter.GalleryItem.ImageItem
import com.project.gallery.search.view.GallerySearchPresenter.GalleryItem.LoadingItem
import com.project.gallery.search.view.GallerySearchPresenter.State.*
import com.project.gallery.utils.PushImageRepository
import com.project.gallery.utils.Throttler
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit

class GallerySearchInteractorTest {

    @get:Rule
    var rule = MockitoJUnit.rule()

    @Mock
    lateinit var presenter: GallerySearchPresenter
    val inMemoryImageRepository = InMemoryImageRepository()
    val pushImageRepository = PushImageRepository()
    val throttler = Throttler(0)

    @Test
    fun `Search passes result to presenter`() {
        // Given
        val interactor = GallerySearchInteractor(inMemoryImageRepository, presenter, throttler)

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
        val interactor = GallerySearchInteractor(inMemoryImageRepository, presenter, throttler)

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

    @Test
    fun `Changing search request returns new result`(){
        // Given
        val interactor = GallerySearchInteractor(inMemoryImageRepository, presenter, throttler)

        val expectedFirstLoadedItems = kittens
            .take(pageSize)
            .map {
                ImageItem(it)
            }

        val expectedSecondLoadedItems = puppies
            .take(pageSize)
            .map {
                ImageItem(it)
            }

        // When
        interactor.start()
        interactor.search(InMemoryImageRepository.KITTENS_KEYWORD)
        interactor.search(InMemoryImageRepository.PUPPIES_KEYWORD)

        // Then
        inOrder(presenter).apply {
            verify(presenter).updateState(Empty)
            verify(presenter).updateState(Loading)
            verify(presenter).updateState(Ready(expectedFirstLoadedItems))
            verify(presenter).updateState(Loading)
            verify(presenter).updateState(Ready(expectedSecondLoadedItems))
        }
    }

    @Test
    fun `Changing search request cancels old result`(){
        // Given
        val interactor = GallerySearchInteractor(pushImageRepository, presenter, Throttler(0))

        val kittensUpdate = listOf("http://kitten1.jpg", "http://kitten2.jpg")
        val kittensLoadedItems = kittensUpdate
            .map { ImageItem(it) }
        val puppiesUpdate = listOf("http://puppy1.jpg", "http://puppy2.jpg")
        val puppiesLoadedItems = puppiesUpdate
            .map { ImageItem(it) }

        // When
        interactor.start()
        interactor.search(InMemoryImageRepository.KITTENS_KEYWORD)
        interactor.search(InMemoryImageRepository.PUPPIES_KEYWORD)
        pushImageRepository.push(InMemoryImageRepository.PUPPIES_KEYWORD, puppiesUpdate)
        pushImageRepository.push(InMemoryImageRepository.KITTENS_KEYWORD, kittensUpdate)

        // Then
        inOrder(presenter).apply {
            verify(presenter).updateState(Empty)
            verify(presenter, atLeastOnce()).updateState(Loading)
            verify(presenter).updateState(Ready(puppiesLoadedItems))
            verify(presenter, never()).updateState(Ready(kittensLoadedItems))
        }
    }

    @Test
    fun `Starting interactor sets listener for presenter`(){
        // Given
        val interactor = GallerySearchInteractor(inMemoryImageRepository, presenter, throttler)

        // When
        interactor.start()

        // Then
        verify(presenter).setListener(interactor)
    }
}
@file:Suppress("IllegalIdentifier")

package com.project.gallery.search.data.repository

import com.project.gallery.utils.FakeImageRepository
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit

class FakeImageRepositoryTest {
    @get:Rule
    var rule = MockitoJUnit.rule()

    @Mock
    lateinit var listener: ImagePaginator.ImageUpdatesListener

    val paginator = FakeImageRepository()
        .search(FakeImageRepository.KITTENS_KEYWORD)

    @Test
    fun `Subscribing does not trigger updates`() {
        // Given
        paginator.subscribeForImageUpdates(listener)

        // When
        paginator.loadNext()

        // Then
        verify(listener).onUpdate(anyList<String>())
    }

    @Test
    fun `Receive updates`() {
        // Given
        paginator.subscribeForImageUpdates(listener)

        // When
        paginator.loadNext()

        // Then
        verify(listener).onUpdate(anyList<String>())
    }

    @Test
    fun `Do not receive updates when unsubscribed`() {
        // Given
        paginator.subscribeForImageUpdates(listener)
        paginator.unsubscribeFromImageUpdates(listener)

        // When
        paginator.loadNext()

        // Then
        verify(listener, never()).onUpdate(anyList<String>())
    }
}
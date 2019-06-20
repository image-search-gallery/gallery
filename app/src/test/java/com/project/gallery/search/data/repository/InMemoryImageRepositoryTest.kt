@file:Suppress("IllegalIdentifier")

package com.project.gallery.search.data.repository

import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit

class InMemoryImageRepositoryTest {
    @get:Rule
    var rule = MockitoJUnit.rule()

    @Mock
    lateinit var listener: ImagePaginator.ImageUpdatesListener

    val paginator = InMemoryImageRepository().search(InMemoryImageRepository.KITTENS_KEYWORD)

    @Test
    fun `Subscribing does not trigger updates`() {
        // Given
        paginator.subscribeForImageUpdates(listener)

        // When
        paginator.loadNext()

        // Then
        verify(listener).update(anyList<String>())
    }

    @Test
    fun `Receive updates`() {
        // Given
        paginator.subscribeForImageUpdates(listener)

        // When
        paginator.loadNext()

        // Then
        verify(listener).update(anyList<String>())
    }

    @Test
    fun `Do not receive updates when unsubscribed`() {
        // Given
        paginator.subscribeForImageUpdates(listener)
        paginator.unsubscribeFromImageUpdates(listener)

        // When
        paginator.loadNext()

        // Then
        verify(listener, never()).update(anyList<String>())
    }
}
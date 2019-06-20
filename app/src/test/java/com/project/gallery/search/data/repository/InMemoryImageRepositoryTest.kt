@file:Suppress("IllegalIdentifier")

package com.project.gallery.search.data.repository

import org.junit.Before
import org.junit.Rule
import org.mockito.junit.MockitoJUnit

class InMemoryImageRepositoryTest {
    @get:Rule
    var rule = MockitoJUnit.rule()

    val repository = InMemoryImageRepository()


    @Before
    fun setUp(){
    }
}
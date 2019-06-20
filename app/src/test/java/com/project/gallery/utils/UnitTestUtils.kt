package com.project.gallery.utils

import io.reactivex.Flowable
import org.mockito.BDDMockito

/**
 * Contains methods to simply and shorten writing of unit tests
 */

/**
 * Shortens given(...).willReturn(...).
 */
infix fun <T> T.willReturn(result: T) {
    BDDMockito.given(this).willReturn(result)
}
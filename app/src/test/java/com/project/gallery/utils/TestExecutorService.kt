package com.project.gallery.utils

import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.TimeUnit

class TestExecutorService : AbstractExecutorService() {
    override fun isTerminated() = false

    override fun execute(command: Runnable?) {
        command?.run()
    }

    override fun shutdown() = throw UnsupportedOperationException()

    override fun shutdownNow() = throw UnsupportedOperationException()

    override fun isShutdown() = false

    override fun awaitTermination(timeout: Long, unit: TimeUnit?) = throw UnsupportedOperationException()
}
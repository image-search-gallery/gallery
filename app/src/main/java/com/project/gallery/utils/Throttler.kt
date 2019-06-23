package com.project.gallery.utils

import java.util.*

class Throttler(private val throttleByMilliseconds: Long){
    private val timer = Timer()
    private var timerTask: TimerTask? = null

    fun submit(task: () -> Unit) {
        timerTask?.cancel()

        timerTask = object : TimerTask() {
            override fun run() {
                task()
                timerTask = null
            }
        }

        timer.schedule(timerTask, throttleByMilliseconds)
    }
}
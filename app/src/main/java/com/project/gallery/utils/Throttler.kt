package com.project.gallery.utils

import java.lang.IllegalArgumentException
import java.util.*

class Throttler(private val throttleByMilliseconds: Long){
    private val timer = Timer()
    private var timerTask: TimerTask? = null

    init {
        if(throttleByMilliseconds < 0){
            throw IllegalArgumentException("Throttle interval must not be negative.")
        }
    }

    fun submit(task: () -> Unit) {
        if(throttleByMilliseconds == 0L){
            task()
            return
        }

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
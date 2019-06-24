package com.project.gallery.utils

import java.lang.IllegalArgumentException
import java.util.*

/**
 * Throttles incoming tasks by a given interval in milliseconds.
 * @param throttleByMilliseconds must be not negative.
 * @throws IllegalArgumentException if [throttleByMilliseconds] is negative.
 */
class Throttler(private val throttleByMilliseconds: Long){
    private val timer = Timer()
    private var timerTask: TimerTask? = null

    init {
        if(throttleByMilliseconds < 0){
            throw IllegalArgumentException("Throttle interval must not be negative.")
        }
    }

    /**
     * Submits task which will be either run or rejected depending on a time passed between current and previous submit
     * calls.
     */
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
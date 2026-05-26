package com.denseframe.capturearcore

import java.util.ArrayDeque

class ArCoreBoundedFrameQueue<T>(
    private val capacity: Int,
) {
    private val queue = ArrayDeque<T>()
    var dropped: Int = 0
        private set

    init {
        require(capacity > 0) { "capacity must be positive" }
    }

    fun offer(value: T): Boolean {
        if (queue.size >= capacity) {
            dropped += 1
            return false
        }
        queue.addLast(value)
        return true
    }

    fun poll(): T? = if (queue.isEmpty()) null else queue.removeFirst()
    fun size(): Int = queue.size
}

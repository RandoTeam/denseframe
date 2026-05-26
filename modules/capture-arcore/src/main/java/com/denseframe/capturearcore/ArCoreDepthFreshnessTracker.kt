package com.denseframe.capturearcore

class ArCoreDepthFreshnessTracker {
    private var lastRawDepthTimestampNanos: Long? = null

    fun classify(frameTimestampNanos: Long, rawDepthTimestampNanos: Long): ArCoreDepthFreshness {
        val freshness = if (rawDepthTimestampNanos == frameTimestampNanos && lastRawDepthTimestampNanos != rawDepthTimestampNanos) {
            ArCoreDepthFreshness.NEW_DEPTH
        } else {
            ArCoreDepthFreshness.REPROJECTED_DEPTH
        }
        lastRawDepthTimestampNanos = rawDepthTimestampNanos
        return freshness
    }
}

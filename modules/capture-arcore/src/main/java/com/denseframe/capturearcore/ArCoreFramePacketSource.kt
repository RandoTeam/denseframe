package com.denseframe.capturearcore

import com.denseframe.captureapi.ConfidenceFrame
import com.denseframe.captureapi.DepthFrame
import com.denseframe.captureapi.FramePacket
import com.denseframe.captureapi.FrameQualityMetrics
import com.denseframe.captureapi.FrameTimestamp
import com.denseframe.captureapi.TrackingState

class ArCoreFramePacketSource(
    private val receivedNanos: () -> Long = { System.nanoTime() },
    private val metricsCounter: ArCoreCaptureMetricsCounter = ArCoreCaptureMetricsCounter(),
    private val freshnessTracker: ArCoreDepthFreshnessTracker = ArCoreDepthFreshnessTracker(),
) {
    fun acquire(frame: ArCoreFrameLike): ArCoreFramePacketResult {
        return try {
            val camera = frame.camera()
            val trackingState = ArCoreTrackingMapper.map(camera.trackingStateName())
            if (trackingState != TrackingState.TRACKING) {
                return ArCoreFramePacketResult.TrackingUnavailable(
                    trackingState = trackingState,
                    failureReason = camera.trackingFailureReasonName(),
                    metrics = metricsCounter.trackingLost(),
                )
            }

            val pose = ArCorePoseMapper.map(camera.pose())
            val intrinsics = ArCoreIntrinsicsMapper.map(camera.imageIntrinsics())
            var rawDepth: ArCoreImageLike? = null
            var confidence: ArCoreImageLike? = null
            try {
                rawDepth = frame.acquireRawDepthImage16Bits()
                confidence = frame.acquireRawDepthConfidenceImage()
                if (rawDepth.width != confidence.width || rawDepth.height != confidence.height) {
                    return ArCoreFramePacketResult.Rejected("Raw depth and confidence dimensions differ", metricsCounter.rejected())
                }
                val depthBytes = ArCoreImagePlanePacker.packDepthU16(rawDepth)
                val confidenceBytes = ArCoreImagePlanePacker.packConfidenceU8(confidence)
                val freshness = freshnessTracker.classify(frame.timestampNanos, rawDepth.timestampNanos)
                val packet = FramePacket(
                    timestamp = FrameTimestamp(
                        sensorNanos = frame.timestampNanos,
                        receivedNanos = receivedNanos(),
                    ),
                    intrinsics = intrinsics,
                    pose = pose,
                    depth = DepthFrame(
                        width = rawDepth.width,
                        height = rawDepth.height,
                        depthU16 = depthBytes,
                        metersPerUnit = 0.001f,
                    ),
                    confidence = ConfidenceFrame(
                        width = confidence.width,
                        height = confidence.height,
                        confidenceU8 = confidenceBytes,
                    ),
                    color = null,
                    quality = FrameQualityMetrics(
                        trackingState = trackingState,
                        depthConfidence = confidenceBytes.averageConfidence(),
                        coverage = depthBytes.depthCoverage(),
                        motionRisk = 0f,
                        blurRisk = 0f,
                    ),
                )
                ArCoreFramePacketResult.FramePacketReady(
                    packet = packet,
                    depthFreshness = freshness,
                    rawDepthTimestampNanos = rawDepth.timestampNanos,
                    metrics = metricsCounter.accepted(frame.timestampNanos, rawDepth.timestampNanos, freshness),
                )
            } finally {
                confidence?.close()
                rawDepth?.close()
            }
        } catch (throwable: Throwable) {
            val error = ArCoreExceptionMapper.map(throwable)
            when (error.code) {
                "not_yet_available" -> ArCoreFramePacketResult.DepthUnavailable(
                    reason = error.message,
                    depthFreshness = ArCoreDepthFreshness.NO_DEPTH,
                    metrics = metricsCounter.noDepth(notYetAvailable = true),
                )
                "resource_exhausted" -> ArCoreFramePacketResult.Rejected(error.message, metricsCounter.resourceExhausted())
                else -> ArCoreFramePacketResult.Fatal(error, metricsCounter.rejected())
            }
        }
    }

    fun metrics(): ArCoreCaptureMetrics = metricsCounter.snapshot()

    private fun ByteArray.averageConfidence(): Float {
        if (isEmpty()) return 0f
        var sum = 0L
        forEach { sum += it.toInt() and 0xFF }
        return (sum.toFloat() / size.toFloat() / 255f).coerceIn(0f, 1f)
    }

    private fun ByteArray.depthCoverage(): Float {
        if (isEmpty()) return 0f
        var valid = 0
        var index = 0
        while (index < size) {
            val lo = this[index].toInt() and 0xFF
            val hi = this[index + 1].toInt() and 0xFF
            if ((hi shl 8) or lo > 0) valid += 1
            index += 2
        }
        return (valid.toFloat() / (size / 2).toFloat()).coerceIn(0f, 1f)
    }
}

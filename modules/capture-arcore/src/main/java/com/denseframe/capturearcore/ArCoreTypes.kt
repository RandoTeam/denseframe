package com.denseframe.capturearcore

import com.denseframe.captureapi.FramePacket
import com.denseframe.captureapi.TrackingState

sealed class ArCoreAvailabilityStatus {
    data object SupportedInstalled : ArCoreAvailabilityStatus()
    data object SupportedApkTooOld : ArCoreAvailabilityStatus()
    data object SupportedNotInstalled : ArCoreAvailabilityStatus()
    data object Checking : ArCoreAvailabilityStatus()
    data object TimedOut : ArCoreAvailabilityStatus()
    data object UnsupportedDevice : ArCoreAvailabilityStatus()
    data object Unknown : ArCoreAvailabilityStatus()
    data class Error(val message: String) : ArCoreAvailabilityStatus()
}

sealed class ArCoreInstallResult {
    data object Installed : ArCoreInstallResult()
    data object InstallRequested : ArCoreInstallResult()
    data class Failed(val error: ArCoreSessionError) : ArCoreInstallResult()
}

sealed class ArCoreDepthSupport {
    data object RawDepthOnly : ArCoreDepthSupport()
    data object AutomaticFallback : ArCoreDepthSupport()
    data object Unsupported : ArCoreDepthSupport()
}

data class ArCoreCapabilityResult(
    val depthSupport: ArCoreDepthSupport,
    val selectedDepthModeName: String?,
)

enum class ArCoreSessionState {
    IDLE,
    CHECKING_AVAILABILITY,
    INSTALL_REQUIRED,
    READY_TO_CREATE,
    CREATED,
    CONFIGURED,
    RESUMED,
    PAUSED,
    CLOSED,
    FAILED,
}

data class ArCoreSessionError(
    val code: String,
    val message: String,
    val recoverable: Boolean,
)

enum class ArCoreDepthFreshness {
    NEW_DEPTH,
    REPROJECTED_DEPTH,
    NO_DEPTH,
}

sealed class ArCoreFramePacketResult {
    data class FramePacketReady(
        val packet: FramePacket,
        val depthFreshness: ArCoreDepthFreshness,
        val rawDepthTimestampNanos: Long,
        val metrics: ArCoreCaptureMetrics,
    ) : ArCoreFramePacketResult()

    data class TrackingUnavailable(
        val trackingState: TrackingState,
        val failureReason: String,
        val metrics: ArCoreCaptureMetrics,
    ) : ArCoreFramePacketResult()

    data class DepthUnavailable(
        val reason: String,
        val depthFreshness: ArCoreDepthFreshness,
        val metrics: ArCoreCaptureMetrics,
    ) : ArCoreFramePacketResult()

    data class Rejected(val reason: String, val metrics: ArCoreCaptureMetrics) : ArCoreFramePacketResult()
    data class Fatal(val error: ArCoreSessionError, val metrics: ArCoreCaptureMetrics) : ArCoreFramePacketResult()
}

data class ArCoreCaptureMetrics(
    val acceptedFrames: Int = 0,
    val rejectedFrames: Int = 0,
    val droppedQueueFull: Int = 0,
    val skippedFpsThrottle: Int = 0,
    val trackingLostFrames: Int = 0,
    val noDepthFrames: Int = 0,
    val reprojectedDepthFrames: Int = 0,
    val newDepthFrames: Int = 0,
    val storageFailedFrames: Int = 0,
    val resourceExhaustedFrames: Int = 0,
    val notYetAvailableFrames: Int = 0,
    val lastFrameTimestampNanos: Long? = null,
    val lastRawDepthTimestampNanos: Long? = null,
)

class ArCoreCaptureMetricsCounter {
    private var metrics = ArCoreCaptureMetrics()

    fun snapshot(): ArCoreCaptureMetrics = metrics

    fun accepted(frameTimestamp: Long, rawDepthTimestamp: Long, freshness: ArCoreDepthFreshness): ArCoreCaptureMetrics {
        metrics = metrics.copy(
            acceptedFrames = metrics.acceptedFrames + 1,
            newDepthFrames = metrics.newDepthFrames + if (freshness == ArCoreDepthFreshness.NEW_DEPTH) 1 else 0,
            reprojectedDepthFrames = metrics.reprojectedDepthFrames + if (freshness == ArCoreDepthFreshness.REPROJECTED_DEPTH) 1 else 0,
            lastFrameTimestampNanos = frameTimestamp,
            lastRawDepthTimestampNanos = rawDepthTimestamp,
        )
        return metrics
    }

    fun rejected(): ArCoreCaptureMetrics {
        metrics = metrics.copy(rejectedFrames = metrics.rejectedFrames + 1)
        return metrics
    }

    fun trackingLost(): ArCoreCaptureMetrics {
        metrics = metrics.copy(trackingLostFrames = metrics.trackingLostFrames + 1, rejectedFrames = metrics.rejectedFrames + 1)
        return metrics
    }

    fun noDepth(notYetAvailable: Boolean): ArCoreCaptureMetrics {
        metrics = metrics.copy(
            noDepthFrames = metrics.noDepthFrames + 1,
            notYetAvailableFrames = metrics.notYetAvailableFrames + if (notYetAvailable) 1 else 0,
            rejectedFrames = metrics.rejectedFrames + 1,
        )
        return metrics
    }

    fun resourceExhausted(): ArCoreCaptureMetrics {
        metrics = metrics.copy(resourceExhaustedFrames = metrics.resourceExhaustedFrames + 1, rejectedFrames = metrics.rejectedFrames + 1)
        return metrics
    }
}

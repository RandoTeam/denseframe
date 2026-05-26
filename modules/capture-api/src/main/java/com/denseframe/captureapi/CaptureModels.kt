package com.denseframe.captureapi

interface CaptureSource {
    val sourceId: String
    val supportedModes: Set<CaptureMode>

    fun supports(preset: CapturePreset): Boolean
}

enum class CaptureSessionState {
    IDLE,
    CHECKING_PERMISSIONS,
    INITIALIZING_AR,
    WARMING_UP_TRACKING,
    TRACKING_READY,
    CAPTURING,
    PAUSED_BY_USER,
    PAUSED_TRACKING_LOST,
    FINALIZING,
    COMPLETED,
    FAILED,
}

enum class TrackingState {
    UNKNOWN,
    TRACKING,
    LIMITED,
    LOST,
}

enum class CaptureMode {
    OBJECT,
    ROOM,
    FREEFORM,
    PREVIEW,
}

data class CapturePreset(
    val mode: CaptureMode,
    val targetFrameRate: Int,
    val maxQueuedFrames: Int,
    val minDepthConfidence: Float,
) {
    init {
        require(targetFrameRate > 0) { "targetFrameRate must be positive." }
        require(maxQueuedFrames > 0) { "maxQueuedFrames must be positive." }
        require(minDepthConfidence in 0f..1f) { "minDepthConfidence must be in 0..1." }
    }
}

data class FrameTimestamp(
    val sensorNanos: Long,
    val receivedNanos: Long,
) {
    init {
        require(sensorNanos >= 0) { "sensorNanos must be non-negative." }
        require(receivedNanos >= 0) { "receivedNanos must be non-negative." }
    }
}

data class CameraIntrinsics(
    val width: Int,
    val height: Int,
    val fx: Float,
    val fy: Float,
    val cx: Float,
    val cy: Float,
) {
    init {
        require(width > 0) { "width must be positive." }
        require(height > 0) { "height must be positive." }
        require(fx > 0f) { "fx must be positive." }
        require(fy > 0f) { "fy must be positive." }
    }
}

/**
 * 4x4 transform that maps homogeneous camera-space points into world space:
 * p_world = T_world_camera * p_camera.
 *
 * Values are column-major to match the documented DenseFrame convention.
 */
data class PoseMatrix4x4(
    val valuesColumnMajor: List<Float>,
) {
    init {
        require(valuesColumnMajor.size == 16) { "PoseMatrix4x4 must contain 16 values." }
    }

    companion object {
        const val CONVENTION = "T_world_camera_column_major"
    }
}

data class DepthFrame(
    val width: Int,
    val height: Int,
    val depthU16: ByteArray,
    val metersPerUnit: Float,
) {
    init {
        require(width > 0) { "width must be positive." }
        require(height > 0) { "height must be positive." }
        require(depthU16.size == width * height * 2) { "depthU16 must contain width * height * 2 bytes." }
        require(metersPerUnit > 0f) { "metersPerUnit must be positive." }
    }

    override fun equals(other: Any?): Boolean =
        other is DepthFrame &&
            width == other.width &&
            height == other.height &&
            depthU16.contentEquals(other.depthU16) &&
            metersPerUnit == other.metersPerUnit

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + depthU16.contentHashCode()
        result = 31 * result + metersPerUnit.hashCode()
        return result
    }
}

data class ConfidenceFrame(
    val width: Int,
    val height: Int,
    val confidenceU8: ByteArray,
) {
    init {
        require(width > 0) { "width must be positive." }
        require(height > 0) { "height must be positive." }
        require(confidenceU8.size == width * height) { "confidenceU8 must contain width * height bytes." }
    }

    override fun equals(other: Any?): Boolean =
        other is ConfidenceFrame &&
            width == other.width &&
            height == other.height &&
            confidenceU8.contentEquals(other.confidenceU8)

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + confidenceU8.contentHashCode()
        return result
    }
}

data class ColorFramePayload(
    val format: String,
    val width: Int,
    val height: Int,
    val byteCount: Long,
) {
    init {
        require(format.isNotBlank()) { "format must be set." }
        require(width > 0) { "width must be positive." }
        require(height > 0) { "height must be positive." }
        require(byteCount >= 0) { "byteCount must be non-negative." }
    }
}

data class FrameQualityMetrics(
    val trackingState: TrackingState,
    val depthConfidence: Float,
    val coverage: Float,
    val motionRisk: Float,
    val blurRisk: Float,
) {
    init {
        require(depthConfidence in 0f..1f) { "depthConfidence must be in 0..1." }
        require(coverage in 0f..1f) { "coverage must be in 0..1." }
        require(motionRisk in 0f..1f) { "motionRisk must be in 0..1." }
        require(blurRisk in 0f..1f) { "blurRisk must be in 0..1." }
    }
}

data class FramePacket(
    val timestamp: FrameTimestamp,
    val intrinsics: CameraIntrinsics,
    val pose: PoseMatrix4x4,
    val depth: DepthFrame?,
    val confidence: ConfidenceFrame?,
    val color: ColorFramePayload?,
    val quality: FrameQualityMetrics,
)

data class CaptureError(
    val code: String,
    val message: String,
    val recoverable: Boolean,
) {
    init {
        require(code.isNotBlank()) { "code must be set." }
        require(message.isNotBlank()) { "message must be set." }
    }
}

sealed interface CaptureCommand {
    data class Start(val preset: CapturePreset) : CaptureCommand
    data object Pause : CaptureCommand
    data object Resume : CaptureCommand
    data object Stop : CaptureCommand
}

sealed interface CaptureEvent {
    data object UserStartedNewScan : CaptureEvent
    data object PermissionsGranted : CaptureEvent
    data class PermissionsDenied(val error: CaptureError) : CaptureEvent
    data object ArSessionReady : CaptureEvent
    data class ArSessionFailed(val error: CaptureError) : CaptureEvent
    data object TrackingStable : CaptureEvent
    data object TrackingLost : CaptureEvent
    data object TrackingRecovered : CaptureEvent
    data object UserPressedRecord : CaptureEvent
    data object UserPressedPause : CaptureEvent
    data object UserPressedResume : CaptureEvent
    data object UserPressedStop : CaptureEvent
    data class StoragePressure(val remainingBytes: Long) : CaptureEvent
    data class FatalError(val error: CaptureError) : CaptureEvent
}

package com.denseframe.capturestore

import com.denseframe.captureapi.FramePacket
import com.denseframe.captureapi.PoseMatrix4x4
import com.denseframe.captureapi.TrackingState
import com.denseframe.projectstore.CameraIntrinsics as DfrCameraIntrinsics
import com.denseframe.projectstore.FrameQuality
import com.denseframe.projectstore.FrameWriteRequest
import com.denseframe.projectstore.PoseMatrix

class CaptureFrameDfrMapper(
    private val payloadWriter: CaptureFramePayloadWriter = CaptureFramePayloadWriter(),
) {
    fun validate(
        packet: FramePacket,
        policy: CaptureToDfrWritePolicy,
        previousTimestampNanos: Long? = null,
    ): CaptureFrameValidationResult {
        val depth = packet.depth
        val confidence = packet.confidence
        val reasons = buildList {
            if (packet.pose.valuesColumnMajor.size != 16) {
                add("pose matrix must contain 16 values using ${PoseMatrix4x4.CONVENTION}")
            }
            if (packet.intrinsics.fx <= 0f || packet.intrinsics.fy <= 0f) {
                add("intrinsics fx/fy must be positive")
            }
            if (depth != null) {
                if (depth.width <= 0 || depth.height <= 0) {
                    add("depth dimensions must be positive")
                }
                if (depth.depthU16.size != depth.width * depth.height * 2) {
                    add("depth payload byte count must equal width * height * 2")
                }
            } else if (!policy.allowMissingDepth) {
                add("depth payload is missing")
            }
            if (confidence != null) {
                if (confidence.width <= 0 || confidence.height <= 0) {
                    add("confidence dimensions must be positive")
                }
                if (confidence.confidenceU8.size != confidence.width * confidence.height) {
                    add("confidence payload byte count must equal width * height")
                }
            } else if (!policy.allowMissingConfidence) {
                add("confidence payload is missing")
            }
            if (packet.color == null && !policy.allowMissingColor) {
                add("color payload is missing")
            }
            if (depth != null && confidence != null) {
                if (depth.width != confidence.width || depth.height != confidence.height) {
                    add("depth and confidence dimensions must match")
                }
            }
            if (policy.requireTrackedFrames && packet.quality.trackingState != TrackingState.TRACKING) {
                add("tracking state must be TRACKING")
            }
            if (
                policy.strictMonotonicTimestamps &&
                previousTimestampNanos != null &&
                packet.timestamp.sensorNanos <= previousTimestampNanos
            ) {
                add("timestamp must be strictly greater than the previous accepted frame timestamp")
            }
        }
        return if (reasons.isEmpty()) {
            CaptureFrameValidationResult.Valid
        } else {
            CaptureFrameValidationResult.Rejected(reasons)
        }
    }

    fun toFrameWriteRequest(
        packet: FramePacket,
        policy: CaptureToDfrWritePolicy,
    ): FrameWriteRequest = FrameWriteRequest(
        timestampNanos = packet.timestamp.sensorNanos,
        trackingState = packet.quality.trackingState.name.lowercase(),
        cameraIntrinsics = DfrCameraIntrinsics(
            width = packet.intrinsics.width,
            height = packet.intrinsics.height,
            fx = packet.intrinsics.fx,
            fy = packet.intrinsics.fy,
            cx = packet.intrinsics.cx,
            cy = packet.intrinsics.cy,
        ),
        pose = PoseMatrix(packet.pose.valuesColumnMajor),
        quality = FrameQuality(
            depthConfidence = packet.quality.depthConfidence,
            coverage = packet.quality.coverage,
            motionRisk = packet.quality.motionRisk,
            blurRisk = packet.quality.blurRisk,
            accepted = packet.quality.trackingState == TrackingState.TRACKING,
            dropReason = if (packet.quality.trackingState == TrackingState.TRACKING) {
                null
            } else {
                "tracking_${packet.quality.trackingState.name.lowercase()}"
            },
        ),
        payloads = payloadWriter.toPayloadSources(packet, policy),
    )
}

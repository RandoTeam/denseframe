package com.denseframe.projectstore

import java.nio.file.Path

@JvmInline
value class DfrSchemaVersion(val value: Int) {
    companion object {
        val V1 = DfrSchemaVersion(1)
    }
}

@JvmInline
value class ProjectId(val value: String)

object ProjectIdGenerator {
    fun generate(): ProjectId = ProjectId(java.util.UUID.randomUUID().toString())
}

data class ProjectManifest(
    val format: String = DFR_FORMAT,
    val schemaVersion: DfrSchemaVersion = DfrSchemaVersion.V1,
    val projectId: ProjectId,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
    val device: DeviceInfo,
    val capture: CaptureInfo,
    val reconstructionDefaults: ReconstructionDefaults,
    val frames: List<FrameManifest> = emptyList(),
) {
    companion object {
        const val DFR_FORMAT = "denseframe.raw"
    }
}

data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val osVersion: String,
)

data class CaptureInfo(
    val mode: String,
    val coordinateConvention: String,
)

data class ReconstructionDefaults(
    val minDepthMeters: Float,
    val maxDepthMeters: Float,
    val minConfidence: Int,
)

data class FrameManifest(
    val frameId: Int,
    val timestampNanos: Long,
    val trackingState: String,
    val cameraIntrinsics: CameraIntrinsics,
    val pose: PoseMatrix,
    val quality: FrameQuality,
    val payloads: FramePayloadRefs,
    val checksums: List<Checksum>,
)

data class CameraIntrinsics(
    val width: Int,
    val height: Int,
    val fx: Float,
    val fy: Float,
    val cx: Float,
    val cy: Float,
)

data class PoseMatrix(
    val values: List<Float>,
) {
    init {
        require(values.size == 16) { "PoseMatrix must contain 16 values." }
    }
}

data class FrameQuality(
    val depthConfidence: Float,
    val coverage: Float,
    val motionRisk: Float,
    val blurRisk: Float = 0f,
    val accepted: Boolean,
    val dropReason: String? = null,
)

data class FramePayloadRefs(
    val colorYuv: String?,
    val depthU16: String?,
    val confidenceU8: String?,
    val colorFormat: String? = null,
    val depthFormat: String? = null,
    val confidenceFormat: String? = null,
)

data class Checksum(
    val path: String,
    val algorithm: String,
    val value: String,
)

data class FramePayloadSources(
    val colorYuv: PayloadSource? = null,
    val depthU16: PayloadSource? = null,
    val confidenceU8: PayloadSource? = null,
    val colorFormat: String? = null,
    val depthFormat: String? = null,
    val confidenceFormat: String? = null,
)

data class PayloadSource(
    val fileName: String,
    val openStream: () -> java.io.InputStream,
)

data class FrameWriteRequest(
    val timestampNanos: Long,
    val trackingState: String,
    val cameraIntrinsics: CameraIntrinsics,
    val pose: PoseMatrix,
    val quality: FrameQuality,
    val payloads: FramePayloadSources,
)

sealed class AppendFrameResult {
    data class Appended(val frameManifest: FrameManifest) : AppendFrameResult()
    data class Failed(val reason: String, val cause: Throwable? = null) : AppendFrameResult()
}

sealed class ProjectOpenResult {
    data class Opened(
        val manifest: ProjectManifest,
        val frameIssues: List<FrameIssue>,
    ) : ProjectOpenResult()

    data class Failed(val reason: String, val cause: Throwable? = null) : ProjectOpenResult()
}

data class FrameIssue(
    val frameDirectory: String,
    val reason: String,
)

data class ValidationResult(
    val valid: Boolean,
    val issues: List<String>,
)

data class CreatedProject(
    val projectDir: Path,
    val manifest: ProjectManifest,
)

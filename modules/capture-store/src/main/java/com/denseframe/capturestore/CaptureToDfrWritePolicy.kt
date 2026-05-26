package com.denseframe.capturestore

enum class DfrPayloadFormat(val storageName: String) {
    DEPTH_U16_MILLIMETERS_LITTLE_ENDIAN("DEPTH_U16_MILLIMETERS_LITTLE_ENDIAN"),
    CONFIDENCE_U8_LINEAR_0_255("CONFIDENCE_U8_LINEAR_0_255"),
}

data class CaptureToDfrWritePolicy(
    val requireTrackedFrames: Boolean = true,
    val allowMissingDepth: Boolean = false,
    val allowMissingConfidence: Boolean = false,
    val allowMissingColor: Boolean = true,
    val strictMonotonicTimestamps: Boolean = true,
    val depthPayloadFormat: DfrPayloadFormat = DfrPayloadFormat.DEPTH_U16_MILLIMETERS_LITTLE_ENDIAN,
    val confidencePayloadFormat: DfrPayloadFormat = DfrPayloadFormat.CONFIDENCE_U8_LINEAR_0_255,
)

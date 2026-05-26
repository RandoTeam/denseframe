package com.denseframe.capturestore

import com.denseframe.projectstore.FrameManifest

sealed class CaptureFrameValidationResult {
    data object Valid : CaptureFrameValidationResult()
    data class Rejected(val reasons: List<String>) : CaptureFrameValidationResult()

    val isValid: Boolean
        get() = this is Valid
}

sealed class CaptureFrameWriteResult {
    data class Written(val frameManifest: FrameManifest) : CaptureFrameWriteResult()
    data class Rejected(val reasons: List<String>) : CaptureFrameWriteResult()
    data class Failed(val reason: String, val cause: Throwable? = null) : CaptureFrameWriteResult()
}

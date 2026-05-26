package com.denseframe.capturearcore

import com.denseframe.captureapi.CameraIntrinsics
import com.denseframe.captureapi.PoseMatrix4x4
import com.denseframe.captureapi.TrackingState

object ArCoreTrackingMapper {
    fun map(name: String): TrackingState = when (name) {
        "TRACKING" -> TrackingState.TRACKING
        "PAUSED" -> TrackingState.LIMITED
        "STOPPED" -> TrackingState.LOST
        else -> TrackingState.UNKNOWN
    }
}

object ArCorePoseMapper {
    fun map(pose: ArCorePoseLike): PoseMatrix4x4 {
        val values = FloatArray(16)
        pose.toMatrix(values, 0)
        return PoseMatrix4x4(values.toList())
    }
}

object ArCoreIntrinsicsMapper {
    fun map(intrinsics: ArCoreCameraIntrinsicsLike): CameraIntrinsics {
        val dimensions = intrinsics.imageDimensions()
        val focal = intrinsics.focalLength()
        val principal = intrinsics.principalPoint()
        require(dimensions.size >= 2) { "ARCore image dimensions must contain width and height" }
        require(focal.size >= 2) { "ARCore focal length must contain fx and fy" }
        require(principal.size >= 2) { "ARCore principal point must contain cx and cy" }
        return CameraIntrinsics(
            width = dimensions[0],
            height = dimensions[1],
            fx = focal[0],
            fy = focal[1],
            cx = principal[0],
            cy = principal[1],
        )
    }
}

object ArCoreExceptionMapper {
    fun map(throwable: Throwable): ArCoreSessionError {
        val name = throwable::class.java.simpleName
        return when (name) {
            "SecurityException" -> ArCoreSessionError("camera_permission_missing", "Camera permission is required before creating an ARCore session.", true)
            "UnavailableArcoreNotInstalledException" -> ArCoreSessionError("arcore_not_installed", "Google Play Services for AR is not installed.", true)
            "UnavailableApkTooOldException" -> ArCoreSessionError("arcore_apk_too_old", "Google Play Services for AR must be updated.", true)
            "UnavailableSdkTooOldException" -> ArCoreSessionError("arcore_sdk_too_old", "The bundled ARCore SDK is too old for this device.", false)
            "UnavailableDeviceNotCompatibleException" -> ArCoreSessionError("arcore_device_not_compatible", "This device is not compatible with ARCore.", false)
            "UnavailableUserDeclinedInstallationException" -> ArCoreSessionError("arcore_install_declined", "ARCore install or update was declined.", true)
            "CameraNotAvailableException" -> ArCoreSessionError("camera_not_available", "The camera is not available to ARCore.", true)
            "UnsupportedConfigurationException" -> ArCoreSessionError("unsupported_configuration", "The requested ARCore configuration is not supported.", false)
            "SessionPausedException" -> ArCoreSessionError("session_paused", "ARCore session is paused.", true)
            "SessionNotPausedException" -> ArCoreSessionError("session_not_paused", "ARCore session must be paused for this operation.", true)
            "MissingGlContextException" -> ArCoreSessionError("missing_gl_context", "ARCore update requires an active GL context.", true)
            "TextureNotSetException" -> ArCoreSessionError("camera_texture_not_set", "ARCore camera texture must be set before frame update.", true)
            "NotYetAvailableException" -> ArCoreSessionError("not_yet_available", "ARCore frame data is not yet available.", true)
            "NotTrackingException" -> ArCoreSessionError("not_tracking", "ARCore is not tracking.", true)
            "ResourceExhaustedException" -> ArCoreSessionError("resource_exhausted", "ARCore image resources are exhausted; images must be closed.", true)
            "DeadlineExceededException" -> ArCoreSessionError("deadline_exceeded", "ARCore frame data is no longer current.", true)
            "FatalException" -> ArCoreSessionError("fatal_arcore_error", "ARCore reported a fatal error.", false)
            "IllegalStateException" -> ArCoreSessionError("illegal_state", throwable.message ?: "ARCore operation was called in an invalid state.", true)
            else -> ArCoreSessionError("arcore_error", throwable.message ?: name, false)
        }
    }
}

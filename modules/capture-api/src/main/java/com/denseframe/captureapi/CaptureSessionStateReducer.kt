package com.denseframe.captureapi

sealed interface TransitionResult {
    val from: CaptureSessionState
    val event: CaptureEvent

    data class Accepted(
        override val from: CaptureSessionState,
        override val event: CaptureEvent,
        val to: CaptureSessionState,
    ) : TransitionResult

    data class Rejected(
        override val from: CaptureSessionState,
        override val event: CaptureEvent,
        val reason: String,
    ) : TransitionResult
}

object CaptureSessionStateReducer {
    fun reduce(
        state: CaptureSessionState,
        event: CaptureEvent,
    ): TransitionResult {
        if (state.isTerminal()) {
            return TransitionResult.Rejected(state, event, "State $state is terminal.")
        }
        if (event is CaptureEvent.FatalError) {
            return TransitionResult.Accepted(state, event, CaptureSessionState.FAILED)
        }

        val next = when (state) {
            CaptureSessionState.IDLE -> when (event) {
                CaptureEvent.UserStartedNewScan -> CaptureSessionState.CHECKING_PERMISSIONS
                else -> null
            }
            CaptureSessionState.CHECKING_PERMISSIONS -> when (event) {
                CaptureEvent.PermissionsGranted -> CaptureSessionState.INITIALIZING_AR
                is CaptureEvent.PermissionsDenied -> CaptureSessionState.FAILED
                else -> null
            }
            CaptureSessionState.INITIALIZING_AR -> when (event) {
                CaptureEvent.ArSessionReady -> CaptureSessionState.WARMING_UP_TRACKING
                is CaptureEvent.ArSessionFailed -> CaptureSessionState.FAILED
                else -> null
            }
            CaptureSessionState.WARMING_UP_TRACKING -> when (event) {
                CaptureEvent.TrackingStable -> CaptureSessionState.TRACKING_READY
                CaptureEvent.TrackingLost -> CaptureSessionState.PAUSED_TRACKING_LOST
                CaptureEvent.UserPressedStop -> CaptureSessionState.FINALIZING
                else -> null
            }
            CaptureSessionState.TRACKING_READY -> when (event) {
                CaptureEvent.UserPressedRecord -> CaptureSessionState.CAPTURING
                CaptureEvent.TrackingLost -> CaptureSessionState.PAUSED_TRACKING_LOST
                CaptureEvent.UserPressedStop -> CaptureSessionState.FINALIZING
                else -> null
            }
            CaptureSessionState.CAPTURING -> when (event) {
                CaptureEvent.TrackingLost -> CaptureSessionState.PAUSED_TRACKING_LOST
                CaptureEvent.UserPressedPause -> CaptureSessionState.PAUSED_BY_USER
                CaptureEvent.UserPressedStop -> CaptureSessionState.FINALIZING
                is CaptureEvent.StoragePressure -> CaptureSessionState.FINALIZING
                else -> null
            }
            CaptureSessionState.PAUSED_BY_USER -> when (event) {
                CaptureEvent.UserPressedResume -> CaptureSessionState.CAPTURING
                CaptureEvent.TrackingLost -> CaptureSessionState.PAUSED_TRACKING_LOST
                CaptureEvent.UserPressedStop -> CaptureSessionState.FINALIZING
                is CaptureEvent.StoragePressure -> CaptureSessionState.FINALIZING
                else -> null
            }
            CaptureSessionState.PAUSED_TRACKING_LOST -> when (event) {
                CaptureEvent.TrackingRecovered -> CaptureSessionState.TRACKING_READY
                CaptureEvent.UserPressedStop -> CaptureSessionState.FINALIZING
                is CaptureEvent.StoragePressure -> CaptureSessionState.FINALIZING
                else -> null
            }
            CaptureSessionState.FINALIZING -> when (event) {
                CaptureEvent.UserPressedStop -> CaptureSessionState.COMPLETED
                else -> null
            }
            CaptureSessionState.COMPLETED,
            CaptureSessionState.FAILED,
            -> null
        }

        return if (next == null) {
            TransitionResult.Rejected(state, event, "Event ${event.name()} is invalid from $state.")
        } else {
            TransitionResult.Accepted(state, event, next)
        }
    }

    private fun CaptureSessionState.isTerminal(): Boolean =
        this == CaptureSessionState.COMPLETED || this == CaptureSessionState.FAILED

    private fun CaptureEvent.name(): String = this::class.simpleName ?: this.toString()
}

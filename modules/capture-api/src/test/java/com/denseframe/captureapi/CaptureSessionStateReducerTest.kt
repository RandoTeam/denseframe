package com.denseframe.captureapi

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CaptureSessionStateReducerTest {
    @Test
    fun validHappyPath() {
        val events = listOf(
            CaptureEvent.UserStartedNewScan,
            CaptureEvent.PermissionsGranted,
            CaptureEvent.ArSessionReady,
            CaptureEvent.TrackingStable,
            CaptureEvent.UserPressedRecord,
            CaptureEvent.UserPressedStop,
            CaptureEvent.UserPressedStop,
        )

        val finalState = events.fold(CaptureSessionState.IDLE) { state, event ->
            val result = CaptureSessionStateReducer.reduce(state, event)
            assertTrue(result is TransitionResult.Accepted)
            (result as TransitionResult.Accepted).to
        }

        assertEquals(CaptureSessionState.COMPLETED, finalState)
    }

    @Test
    fun permissionDeniedFails() {
        assertTransition(
            CaptureSessionState.CHECKING_PERMISSIONS,
            CaptureEvent.PermissionsDenied(error("permission_denied")),
            CaptureSessionState.FAILED,
        )
    }

    @Test
    fun arInitFailureFails() {
        assertTransition(
            CaptureSessionState.INITIALIZING_AR,
            CaptureEvent.ArSessionFailed(error("ar_failed")),
            CaptureSessionState.FAILED,
        )
    }

    @Test
    fun trackingLossAndRecovery() {
        assertTransition(
            CaptureSessionState.CAPTURING,
            CaptureEvent.TrackingLost,
            CaptureSessionState.PAUSED_TRACKING_LOST,
        )
        assertTransition(
            CaptureSessionState.PAUSED_TRACKING_LOST,
            CaptureEvent.TrackingRecovered,
            CaptureSessionState.TRACKING_READY,
        )
    }

    @Test
    fun pauseAndResume() {
        assertTransition(
            CaptureSessionState.CAPTURING,
            CaptureEvent.UserPressedPause,
            CaptureSessionState.PAUSED_BY_USER,
        )
        assertTransition(
            CaptureSessionState.PAUSED_BY_USER,
            CaptureEvent.UserPressedResume,
            CaptureSessionState.CAPTURING,
        )
    }

    @Test
    fun stopAndFinalizeFromCapturingAndPausedStates() {
        assertTransition(
            CaptureSessionState.CAPTURING,
            CaptureEvent.UserPressedStop,
            CaptureSessionState.FINALIZING,
        )
        assertTransition(
            CaptureSessionState.PAUSED_BY_USER,
            CaptureEvent.UserPressedStop,
            CaptureSessionState.FINALIZING,
        )
        assertTransition(
            CaptureSessionState.PAUSED_TRACKING_LOST,
            CaptureEvent.UserPressedStop,
            CaptureSessionState.FINALIZING,
        )
        assertTransition(
            CaptureSessionState.FINALIZING,
            CaptureEvent.UserPressedStop,
            CaptureSessionState.COMPLETED,
        )
    }

    @Test
    fun terminalStateRejectsFurtherTransitions() {
        assertRejected(CaptureSessionState.COMPLETED, CaptureEvent.UserStartedNewScan)
        assertRejected(CaptureSessionState.FAILED, CaptureEvent.UserStartedNewScan)
        assertRejected(CaptureSessionState.FAILED, CaptureEvent.FatalError(error("again")))
    }

    @Test
    fun storagePressureFinalizesActiveCaptureStates() {
        val event = CaptureEvent.StoragePressure(remainingBytes = 128)
        assertTransition(CaptureSessionState.CAPTURING, event, CaptureSessionState.FINALIZING)
        assertTransition(CaptureSessionState.PAUSED_BY_USER, event, CaptureSessionState.FINALIZING)
        assertTransition(CaptureSessionState.PAUSED_TRACKING_LOST, event, CaptureSessionState.FINALIZING)
    }

    @Test
    fun fatalErrorFromMultipleNonTerminalStatesFails() {
        listOf(
            CaptureSessionState.IDLE,
            CaptureSessionState.CHECKING_PERMISSIONS,
            CaptureSessionState.INITIALIZING_AR,
            CaptureSessionState.WARMING_UP_TRACKING,
            CaptureSessionState.TRACKING_READY,
            CaptureSessionState.CAPTURING,
            CaptureSessionState.PAUSED_BY_USER,
            CaptureSessionState.PAUSED_TRACKING_LOST,
            CaptureSessionState.FINALIZING,
        ).forEach { state ->
            assertTransition(state, CaptureEvent.FatalError(error("fatal")), CaptureSessionState.FAILED)
        }
    }

    @Test
    fun invalidTransitionsReturnRejectedResult() {
        assertRejected(CaptureSessionState.IDLE, CaptureEvent.UserPressedRecord)
        assertRejected(CaptureSessionState.CHECKING_PERMISSIONS, CaptureEvent.UserPressedRecord)
        assertRejected(CaptureSessionState.TRACKING_READY, CaptureEvent.UserPressedResume)
        assertRejected(CaptureSessionState.FINALIZING, CaptureEvent.UserPressedRecord)
    }

    @Test
    fun everyDocumentedAcceptedTransitionIsCovered() {
        val expected = setOf(
            CaptureSessionState.IDLE to CaptureEvent.UserStartedNewScan,
            CaptureSessionState.CHECKING_PERMISSIONS to CaptureEvent.PermissionsGranted,
            CaptureSessionState.CHECKING_PERMISSIONS to CaptureEvent.PermissionsDenied(error("permission_denied")),
            CaptureSessionState.INITIALIZING_AR to CaptureEvent.ArSessionReady,
            CaptureSessionState.INITIALIZING_AR to CaptureEvent.ArSessionFailed(error("ar_failed")),
            CaptureSessionState.WARMING_UP_TRACKING to CaptureEvent.TrackingStable,
            CaptureSessionState.WARMING_UP_TRACKING to CaptureEvent.TrackingLost,
            CaptureSessionState.WARMING_UP_TRACKING to CaptureEvent.UserPressedStop,
            CaptureSessionState.TRACKING_READY to CaptureEvent.UserPressedRecord,
            CaptureSessionState.TRACKING_READY to CaptureEvent.TrackingLost,
            CaptureSessionState.TRACKING_READY to CaptureEvent.UserPressedStop,
            CaptureSessionState.CAPTURING to CaptureEvent.TrackingLost,
            CaptureSessionState.CAPTURING to CaptureEvent.UserPressedPause,
            CaptureSessionState.CAPTURING to CaptureEvent.UserPressedStop,
            CaptureSessionState.CAPTURING to CaptureEvent.StoragePressure(1),
            CaptureSessionState.PAUSED_BY_USER to CaptureEvent.UserPressedResume,
            CaptureSessionState.PAUSED_BY_USER to CaptureEvent.TrackingLost,
            CaptureSessionState.PAUSED_BY_USER to CaptureEvent.UserPressedStop,
            CaptureSessionState.PAUSED_BY_USER to CaptureEvent.StoragePressure(1),
            CaptureSessionState.PAUSED_TRACKING_LOST to CaptureEvent.TrackingRecovered,
            CaptureSessionState.PAUSED_TRACKING_LOST to CaptureEvent.UserPressedStop,
            CaptureSessionState.PAUSED_TRACKING_LOST to CaptureEvent.StoragePressure(1),
            CaptureSessionState.FINALIZING to CaptureEvent.UserPressedStop,
        )

        expected.forEach { (state, event) ->
            val result = CaptureSessionStateReducer.reduce(state, event)
            assertTrue("Expected $state + $event to be accepted", result is TransitionResult.Accepted)
        }
    }

    private fun assertTransition(
        from: CaptureSessionState,
        event: CaptureEvent,
        to: CaptureSessionState,
    ) {
        val result = CaptureSessionStateReducer.reduce(from, event)
        assertTrue(result is TransitionResult.Accepted)
        assertEquals(to, (result as TransitionResult.Accepted).to)
    }

    private fun assertRejected(from: CaptureSessionState, event: CaptureEvent) {
        val result = CaptureSessionStateReducer.reduce(from, event)
        assertTrue(result is TransitionResult.Rejected)
    }

    private fun error(code: String): CaptureError = CaptureError(
        code = code,
        message = "Test error: $code",
        recoverable = false,
    )
}

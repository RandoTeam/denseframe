package com.denseframe.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.denseframe.app.capture.ArCoreCaptureCoordinator
import com.denseframe.app.capture.ArCoreCaptureUiState
import com.denseframe.app.ui.screens.CaptureShellScreen
import com.denseframe.app.ui.screens.ModeSelectScreen
import com.denseframe.app.ui.screens.ProcessingScreen
import com.denseframe.app.ui.screens.ProjectGalleryScreen
import com.denseframe.app.ui.screens.SaveReviewScreen
import com.denseframe.app.ui.screens.SceneViewerShellScreen
import com.denseframe.designsystem.DenseFrameTheme

@Composable
fun DenseFrameApp(
    captureCoordinator: ArCoreCaptureCoordinator? = null,
    onRequestCameraPermission: () -> Unit = {},
) {
    var destination by remember { mutableStateOf(ShellDestination.Gallery) }

    when (destination) {
        ShellDestination.Gallery -> ProjectGalleryScreen(
            onNewScan = { destination = ShellDestination.ModeSelect },
            onOpenSampleProject = { destination = ShellDestination.Viewer },
        )
        ShellDestination.ModeSelect -> ModeSelectScreen(
            onBack = { destination = ShellDestination.Gallery },
            onModeSelected = { destination = ShellDestination.Capture },
        )
        ShellDestination.Capture -> {
            LaunchedEffect(captureCoordinator) {
                captureCoordinator?.refreshReadiness()
            }
            CaptureShellScreen(
                state = captureCoordinator?.state?.value ?: ArCoreCaptureUiState(),
                onRequestPermission = onRequestCameraPermission,
                onStart = { captureCoordinator?.startCapture() },
                onStop = { destination = ShellDestination.SaveReview },
                onStopCapture = { captureCoordinator?.stopCapture() },
                onBack = { destination = ShellDestination.ModeSelect },
                captureSurface = {
                    captureCoordinator?.let { com.denseframe.app.ui.ArCoreCaptureSurface(it) }
                },
            )
        }
        ShellDestination.SaveReview -> SaveReviewScreen(
            captureState = captureCoordinator?.state?.value ?: ArCoreCaptureUiState(),
            onProcess = { destination = ShellDestination.Processing },
            onBack = { destination = ShellDestination.Capture },
        )
        ShellDestination.Processing -> ProcessingScreen(
            onOpenViewer = { destination = ShellDestination.Viewer },
            onBack = { destination = ShellDestination.SaveReview },
        )
        ShellDestination.Viewer -> SceneViewerShellScreen(
            onBack = { destination = ShellDestination.Gallery },
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF090D12)
@Composable
private fun DenseFrameAppPreview() {
    DenseFrameTheme {
        DenseFrameApp()
    }
}

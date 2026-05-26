package com.denseframe.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.denseframe.app.ui.screens.CaptureShellScreen
import com.denseframe.app.ui.screens.ModeSelectScreen
import com.denseframe.app.ui.screens.ProcessingScreen
import com.denseframe.app.ui.screens.ProjectGalleryScreen
import com.denseframe.app.ui.screens.SaveReviewScreen
import com.denseframe.app.ui.screens.SceneViewerShellScreen
import com.denseframe.designsystem.DenseFrameTheme

@Composable
fun DenseFrameApp() {
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
        ShellDestination.Capture -> CaptureShellScreen(
            onStop = { destination = ShellDestination.SaveReview },
            onBack = { destination = ShellDestination.ModeSelect },
        )
        ShellDestination.SaveReview -> SaveReviewScreen(
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

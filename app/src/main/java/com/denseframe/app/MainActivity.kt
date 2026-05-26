package com.denseframe.app

import android.Manifest
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.denseframe.app.capture.ArCoreCaptureCoordinator
import com.denseframe.app.ui.DenseFrameApp
import com.denseframe.designsystem.DenseFrameTheme

class MainActivity : ComponentActivity() {
    private lateinit var captureCoordinator: ArCoreCaptureCoordinator
    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        captureCoordinator.refreshPermissionState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        captureCoordinator = ArCoreCaptureCoordinator(this)
        setContent {
            DenseFrameTheme {
                DenseFrameApp(
                    captureCoordinator = captureCoordinator,
                    onRequestCameraPermission = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::captureCoordinator.isInitialized) captureCoordinator.onResume()
    }

    override fun onPause() {
        if (::captureCoordinator.isInitialized) captureCoordinator.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        if (::captureCoordinator.isInitialized) captureCoordinator.close()
        super.onDestroy()
    }
}

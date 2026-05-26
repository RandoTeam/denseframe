package com.denseframe.app.ui

import android.opengl.GLSurfaceView
import android.view.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.denseframe.app.capture.ArCoreCaptureCoordinator
import com.denseframe.capturearcore.ArCoreExternalTexture
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@Composable
fun ArCoreCaptureSurface(
    coordinator: ArCoreCaptureCoordinator,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier,
        factory = {
            GLSurfaceView(context).apply {
                preserveEGLContextOnPause = true
                setEGLContextClientVersion(2)
                setRenderer(DenseFrameArCoreRenderer(coordinator))
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            }
        },
    )
}

private class DenseFrameArCoreRenderer(
    private val coordinator: ArCoreCaptureCoordinator,
) : GLSurfaceView.Renderer {
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        coordinator.onGlSurfaceCreated(ArCoreExternalTexture.create())
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        coordinator.onDisplayGeometryChanged(Surface.ROTATION_0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        coordinator.onDrawFrame()
    }
}

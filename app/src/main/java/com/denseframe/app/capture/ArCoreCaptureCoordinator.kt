package com.denseframe.app.capture

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.denseframe.captureapi.PoseMatrix4x4
import com.denseframe.capturearcore.ArCoreAvailabilityChecker
import com.denseframe.capturearcore.ArCoreAvailabilityStatus
import com.denseframe.capturearcore.ArCoreCaptureMetrics
import com.denseframe.capturearcore.ArCoreDepthFreshness
import com.denseframe.capturearcore.ArCoreDepthSupport
import com.denseframe.capturearcore.ArCoreFramePacketResult
import com.denseframe.capturearcore.ArCoreFramePacketSource
import com.denseframe.capturearcore.ArCoreInstallCoordinator
import com.denseframe.capturearcore.ArCoreInstallResult
import com.denseframe.capturearcore.ArCoreSessionController
import com.denseframe.capturearcore.ArCoreSessionError
import com.denseframe.capturestore.CaptureFrameDfrWriter
import com.denseframe.capturestore.CaptureFrameWriteResult
import com.denseframe.capturestore.CaptureToDfrWritePolicy
import com.denseframe.projectstore.CaptureInfo
import com.denseframe.projectstore.CreatedProject
import com.denseframe.projectstore.DeviceInfo
import com.denseframe.projectstore.DfrProjectValidator
import com.denseframe.projectstore.DfrProjectWriter
import com.denseframe.projectstore.ReconstructionDefaults
import java.nio.file.Path
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ArCoreCaptureCoordinator(
    private val activity: Activity,
    private val availabilityChecker: ArCoreAvailabilityChecker = ArCoreAvailabilityChecker(),
    private val installCoordinator: ArCoreInstallCoordinator = ArCoreInstallCoordinator(),
    private val sessionController: ArCoreSessionController = ArCoreSessionController(),
    private val framePacketSource: ArCoreFramePacketSource = ArCoreFramePacketSource(),
    private val projectWriter: DfrProjectWriter = DfrProjectWriter(),
    private val dfrWriter: CaptureFrameDfrWriter = CaptureFrameDfrWriter(),
) {
    private val mutableState = mutableStateOf(ArCoreCaptureUiState())
    val state: State<ArCoreCaptureUiState> = mutableState

    private val queue = ArrayBlockingQueue<com.denseframe.captureapi.FramePacket>(4)
    private val writeExecutor = Executors.newSingleThreadExecutor()
    private var createdProject: CreatedProject? = null
    private var startElapsedNanos: Long? = null
    private var acceptedWrites = 0
    private var storageFailures = 0
    private var droppedQueueFull = 0
    private var running = true

    init {
        writeExecutor.execute {
            while (running || queue.isNotEmpty()) {
                val packet = queue.poll(100, TimeUnit.MILLISECONDS) ?: continue
                val project = createdProject ?: continue
                when (val result = dfrWriter.appendFrame(project.projectDir, packet, CaptureToDfrWritePolicy())) {
                    is CaptureFrameWriteResult.Written -> {
                        acceptedWrites += 1
                        updateState { it.copy(acceptedFrames = acceptedWrites, storageWriteError = null) }
                    }
                    is CaptureFrameWriteResult.Rejected -> {
                        storageFailures += 1
                        updateState {
                            it.copy(
                                storageFailedFrames = storageFailures,
                                storageWriteError = result.reasons.joinToString("; "),
                            )
                        }
                    }
                    is CaptureFrameWriteResult.Failed -> {
                        storageFailures += 1
                        updateState {
                            it.copy(
                                storageFailedFrames = storageFailures,
                                storageWriteError = result.reason,
                            )
                        }
                    }
                }
            }
        }
        refreshPermissionState()
    }

    fun refreshPermissionState() {
        val granted = hasCameraPermission()
        updateState { it.copy(cameraPermission = if (granted) "Granted" else "Denied", canStart = granted) }
    }

    fun startCapture(): ArCoreSessionError? {
        refreshPermissionState()
        if (!hasCameraPermission()) {
            updateState { it.copy(lastFrameResult = "Camera permission required", canStart = false) }
            return ArCoreSessionError("camera_permission_missing", "Camera permission is required.", true)
        }
        updateState { it.copy(availability = "Checking", sessionState = "Checking availability", canStart = false) }
        val availability = availabilityChecker.check(activity)
        updateState { it.copy(availability = availability.displayName()) }
        when (availability) {
            ArCoreAvailabilityStatus.SupportedInstalled -> Unit
            ArCoreAvailabilityStatus.SupportedApkTooOld,
            ArCoreAvailabilityStatus.SupportedNotInstalled,
            -> {
                when (val install = installCoordinator.requestInstall(activity, userRequestedInstall = true)) {
                    ArCoreInstallResult.Installed -> Unit
                    ArCoreInstallResult.InstallRequested -> {
                        updateState { it.copy(sessionState = "ARCore install requested", canStart = true) }
                        return null
                    }
                    is ArCoreInstallResult.Failed -> {
                        updateFailure(install.error)
                        return install.error
                    }
                }
            }
            else -> {
                val error = ArCoreSessionError("arcore_unavailable", "ARCore is ${availability.displayName()}.", false)
                updateFailure(error)
                return error
            }
        }

        sessionController.create(activity).onFailure {
            val error = com.denseframe.capturearcore.ArCoreExceptionMapper.map(it)
            updateFailure(error)
            return error
        }
        val capability = sessionController.configureDepth()
        updateState {
            it.copy(
                depthSupport = capability.depthSupport.displayName(),
                selectedDepthMode = capability.selectedDepthModeName ?: "None",
                sessionState = sessionController.state.name,
            )
        }
        if (capability.depthSupport == ArCoreDepthSupport.Unsupported) {
            val error = ArCoreSessionError("depth_unsupported", "ARCore raw depth is not supported on this device.", false)
            updateFailure(error)
            return error
        }
        createProjectIfNeeded()
        sessionController.resume()?.let {
            updateFailure(it)
            return it
        }
        startElapsedNanos = System.nanoTime()
        updateState { it.copy(isCapturing = true, sessionState = sessionController.state.name, lastFrameResult = "Capture started") }
        return null
    }

    fun stopCapture() {
        updateState { it.copy(isCapturing = false, sessionState = "Stopping") }
        sessionController.pause()
        val project = createdProject
        val validation = project?.let { DfrProjectValidator().validate(it.projectDir) }
        updateState {
            it.copy(
                sessionState = sessionController.state.name,
                lastFrameResult = if (validation == null) "No project" else if (validation.valid) "DFR validator passed" else validation.issues.joinToString("; "),
                canStart = true,
            )
        }
    }

    fun close() {
        running = false
        sessionController.close()
        writeExecutor.shutdown()
    }

    fun onResume() {
        if (mutableState.value.isCapturing) {
            sessionController.resume()?.let { updateFailure(it) }
        }
    }

    fun onPause() {
        sessionController.pause()
        updateState { it.copy(sessionState = sessionController.state.name) }
    }

    fun onGlSurfaceCreated(textureName: Int) {
        sessionController.setCameraTextureName(textureName)
    }

    fun onDisplayGeometryChanged(rotation: Int, width: Int, height: Int) {
        sessionController.setDisplayGeometry(rotation, width, height)
    }

    fun onDrawFrame() {
        if (!mutableState.value.isCapturing) return
        when (val result = sessionController.update(framePacketSource)) {
            is ArCoreFramePacketResult.FramePacketReady -> {
                val offered = queue.offer(result.packet)
                if (!offered) droppedQueueFull += 1
                updateFromMetrics(
                    metrics = result.metrics,
                    lastFrameResult = if (offered) "Frame queued" else "Frame dropped: queue full",
                    depthFreshness = result.depthFreshness,
                    dropped = droppedQueueFull,
                )
            }
            is ArCoreFramePacketResult.TrackingUnavailable -> {
                updateFromMetrics(result.metrics, "Tracking unavailable", ArCoreDepthFreshness.NO_DEPTH, droppedQueueFull)
                updateState {
                    it.copy(
                        trackingState = result.trackingState.name,
                        trackingFailureReason = result.failureReason,
                    )
                }
            }
            is ArCoreFramePacketResult.DepthUnavailable -> {
                updateFromMetrics(result.metrics, result.reason, result.depthFreshness, droppedQueueFull)
            }
            is ArCoreFramePacketResult.Rejected -> {
                updateFromMetrics(result.metrics, result.reason, ArCoreDepthFreshness.NO_DEPTH, droppedQueueFull)
            }
            is ArCoreFramePacketResult.Fatal -> {
                updateFailure(result.error)
            }
        }
    }

    private fun createProjectIfNeeded() {
        if (createdProject != null) return
        val project = projectWriter.createProject(
            parentDir = activity.filesDir.resolve("denseframe-projects").toPath(),
            deviceInfo = DeviceInfo(
                manufacturer = Build.MANUFACTURER ?: "unknown",
                model = Build.MODEL ?: "unknown",
                osVersion = Build.VERSION.RELEASE ?: "unknown",
            ),
            captureInfo = CaptureInfo(
                mode = "object",
                coordinateConvention = PoseMatrix4x4.CONVENTION,
            ),
            reconstructionDefaults = ReconstructionDefaults(
                minDepthMeters = 0.2f,
                maxDepthMeters = 5.0f,
                minConfidence = 128,
            ),
        )
        createdProject = project
        updateState {
            it.copy(
                projectId = project.manifest.projectId.value,
                projectPath = project.projectDir.displayPath(),
            )
        }
    }

    private fun hasCameraPermission(): Boolean =
        activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    private fun updateFromMetrics(
        metrics: ArCoreCaptureMetrics,
        lastFrameResult: String,
        depthFreshness: ArCoreDepthFreshness,
        dropped: Int,
    ) {
        updateState {
            it.copy(
                rejectedFrames = metrics.rejectedFrames,
                droppedFrames = dropped,
                skippedFpsThrottle = metrics.skippedFpsThrottle,
                noDepthFrames = metrics.noDepthFrames,
                lastFrameResult = lastFrameResult,
                depthFreshness = depthFreshness.name,
                trackingState = "TRACKING",
                elapsedMillis = startElapsedNanos?.let { start -> (System.nanoTime() - start) / 1_000_000L } ?: 0L,
            )
        }
    }

    private fun updateFailure(error: ArCoreSessionError) {
        updateState {
            it.copy(
                sessionState = "FAILED",
                lastFrameResult = error.message,
                storageWriteError = if (error.recoverable) null else error.message,
                canStart = true,
            )
        }
    }

    private fun updateState(block: (ArCoreCaptureUiState) -> ArCoreCaptureUiState) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mutableState.value = block(mutableState.value)
        } else {
            activity.runOnUiThread {
                mutableState.value = block(mutableState.value)
            }
        }
    }

    private fun ArCoreAvailabilityStatus.displayName(): String = when (this) {
        ArCoreAvailabilityStatus.SupportedInstalled -> "Supported installed"
        ArCoreAvailabilityStatus.SupportedApkTooOld -> "Supported update required"
        ArCoreAvailabilityStatus.SupportedNotInstalled -> "Supported install required"
        ArCoreAvailabilityStatus.Checking -> "Checking"
        ArCoreAvailabilityStatus.TimedOut -> "Timed out"
        ArCoreAvailabilityStatus.UnsupportedDevice -> "Unsupported device"
        ArCoreAvailabilityStatus.Unknown -> "Unknown"
        is ArCoreAvailabilityStatus.Error -> message
    }

    private fun ArCoreDepthSupport.displayName(): String = when (this) {
        ArCoreDepthSupport.RawDepthOnly -> "RAW_DEPTH_ONLY"
        ArCoreDepthSupport.AutomaticFallback -> "AUTOMATIC fallback"
        ArCoreDepthSupport.Unsupported -> "Unsupported"
    }

    private fun Path.displayPath(): String = fileName?.toString() ?: toString()
}

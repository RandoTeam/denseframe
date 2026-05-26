package com.denseframe.capturestore

import com.denseframe.captureapi.CameraIntrinsics
import com.denseframe.captureapi.ConfidenceFrame
import com.denseframe.captureapi.DepthFrame
import com.denseframe.captureapi.FramePacket
import com.denseframe.captureapi.FrameQualityMetrics
import com.denseframe.captureapi.FrameTimestamp
import com.denseframe.captureapi.PoseMatrix4x4
import com.denseframe.captureapi.TrackingState
import com.denseframe.projectstore.CaptureInfo
import com.denseframe.projectstore.CreatedProject
import com.denseframe.projectstore.DeviceInfo
import com.denseframe.projectstore.DfrProjectReader
import com.denseframe.projectstore.DfrProjectValidator
import com.denseframe.projectstore.DfrProjectWriter
import com.denseframe.projectstore.ProjectId
import com.denseframe.projectstore.ProjectOpenResult
import com.denseframe.projectstore.ReconstructionDefaults
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.nio.file.Files

class CaptureFrameDfrWriterTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val projectWriter = DfrProjectWriter(clockMillis = { 1_700_000_000_000 })
    private val reader = DfrProjectReader()
    private val validator = DfrProjectValidator(reader)
    private val captureWriter = CaptureFrameDfrWriter(projectWriter = projectWriter)

    @Test
    fun validFrameWithDepthAndConfidenceWritesToDfr() {
        val created = createProject()

        val result = captureWriter.appendFrame(created.projectDir, validFrame())

        assertTrue(result is CaptureFrameWriteResult.Written)
        val frame = (result as CaptureFrameWriteResult.Written).frameManifest
        assertEquals("depth_u16.bin", frame.payloads.depthU16)
        assertEquals("confidence_u8.bin", frame.payloads.confidenceU8)
        assertEquals(DfrPayloadFormat.DEPTH_U16_MILLIMETERS_LITTLE_ENDIAN.storageName, frame.payloads.depthFormat)
        assertEquals(DfrPayloadFormat.CONFIDENCE_U8_LINEAR_0_255.storageName, frame.payloads.confidenceFormat)
        assertTrue(Files.exists(created.projectDir.resolve("frames/00000001/depth_u16.bin")))
        assertTrue(Files.exists(created.projectDir.resolve("frames/00000001/confidence_u8.bin")))
    }

    @Test
    fun validFrameWithoutDepthWritesExplicitMissingPayloadWhenPolicyAllows() {
        val created = createProject()
        val packet = validFrame(depth = null)

        val result = captureWriter.appendFrame(
            projectDir = created.projectDir,
            packet = packet,
            policy = CaptureToDfrWritePolicy(allowMissingDepth = true),
        )

        assertTrue(result is CaptureFrameWriteResult.Written)
        val frame = (result as CaptureFrameWriteResult.Written).frameManifest
        assertNull(frame.payloads.depthU16)
        assertNull(frame.payloads.depthFormat)
        assertEquals("confidence_u8.bin", frame.payloads.confidenceU8)
        assertFalse(Files.exists(created.projectDir.resolve("frames/00000001/depth_u16.bin")))
    }

    @Test(expected = IllegalArgumentException::class)
    fun invalidIntrinsicsRejectedAtCaptureModelBoundary() {
        CameraIntrinsics(width = 2, height = 2, fx = 0f, fy = 100f, cx = 1f, cy = 1f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun invalidPoseRejectedAtCaptureModelBoundary() {
        PoseMatrix4x4(listOf(1f, 2f))
    }

    @Test
    fun depthConfidenceDimensionMismatchRejected() {
        val result = captureWriter.appendFrame(
            projectDir = createProject().projectDir,
            packet = validFrame(
                depth = DepthFrame(2, 2, byteArrayOf(1, 0, 2, 0, 3, 0, 4, 0), 0.001f),
                confidence = ConfidenceFrame(1, 4, byteArrayOf(255.toByte(), 255.toByte(), 255.toByte(), 255.toByte())),
            ),
        )

        assertTrue(result is CaptureFrameWriteResult.Rejected)
        assertTrue((result as CaptureFrameWriteResult.Rejected).reasons.any { it.contains("dimensions must match") })
    }

    @Test(expected = IllegalArgumentException::class)
    fun incorrectPayloadByteCountRejectedAtCaptureModelBoundary() {
        DepthFrame(width = 2, height = 2, depthU16 = byteArrayOf(1, 2), metersPerUnit = 0.001f)
    }

    @Test
    fun checksumIsGeneratedAndReaderValidatesIt() {
        val created = createProject()
        val result = captureWriter.appendFrame(created.projectDir, validFrame())

        assertTrue(result is CaptureFrameWriteResult.Written)
        val checksums = (result as CaptureFrameWriteResult.Written).frameManifest.checksums
        assertTrue(checksums.any { it.path == "depth_u16.bin" && it.algorithm == "sha256" })
        assertTrue(checksums.any { it.path == "confidence_u8.bin" && it.algorithm == "sha256" })
        assertTrue(validator.validate(created.projectDir).valid)
    }

    @Test
    fun incompleteFrameFolderIsReportedByReader() {
        val created = createProject()
        Files.createDirectories(created.projectDir.resolve("frames/00000042"))
        Files.write(created.projectDir.resolve("frames/00000042/depth_u16.bin"), byteArrayOf(1, 0))

        val opened = reader.open(created.projectDir)

        assertTrue(opened is ProjectOpenResult.Opened)
        val issues = (opened as ProjectOpenResult.Opened).frameIssues
        assertTrue(issues.any { it.frameDirectory == "00000042" && it.reason.contains("not referenced") })
        assertTrue(issues.any { it.frameDirectory == "00000042" && it.reason.contains("Missing frame.json") })
    }

    @Test
    fun strictMonotonicTimestampRejectsOlderFrame() {
        val created = createProject()
        assertTrue(captureWriter.appendFrame(created.projectDir, validFrame(sensorNanos = 200)) is CaptureFrameWriteResult.Written)

        val result = captureWriter.appendFrame(created.projectDir, validFrame(sensorNanos = 199))

        assertTrue(result is CaptureFrameWriteResult.Rejected)
        assertTrue((result as CaptureFrameWriteResult.Rejected).reasons.any { it.contains("strictly greater") })
    }

    @Test
    fun storageMappingPreservesTWorldCameraMatrixOrderExactly() {
        val created = createProject()
        val poseValues = List(16) { index -> index.toFloat() + 0.25f }
        val result = captureWriter.appendFrame(created.projectDir, validFrame(poseValues = poseValues))

        assertTrue(result is CaptureFrameWriteResult.Written)
        val frameDir = created.projectDir.resolve("frames/00000001")
        val stored = reader.readFrameManifest(frameDir)

        assertEquals(PoseMatrix4x4.CONVENTION, "T_world_camera_column_major")
        assertEquals(poseValues, stored.pose.values)
    }

    private fun createProject(): CreatedProject = projectWriter.createProject(
        parentDir = temporaryFolder.newFolder("projects").toPath(),
        projectId = ProjectId("capture-store-test-${System.nanoTime()}"),
        deviceInfo = DeviceInfo("DenseFrame", "JUnit", "test"),
        captureInfo = CaptureInfo("object", PoseMatrix4x4.CONVENTION),
        reconstructionDefaults = ReconstructionDefaults(
            minDepthMeters = 0.2f,
            maxDepthMeters = 5.0f,
            minConfidence = 128,
        ),
    )

    private fun validFrame(
        sensorNanos: Long = 123,
        poseValues: List<Float> = listOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f,
        ),
        depth: DepthFrame? = DepthFrame(2, 2, byteArrayOf(1, 0, 2, 0, 3, 0, 4, 0), 0.001f),
        confidence: ConfidenceFrame? = ConfidenceFrame(2, 2, byteArrayOf(255.toByte(), 200.toByte(), 180.toByte(), 160.toByte())),
    ): FramePacket = FramePacket(
        timestamp = FrameTimestamp(sensorNanos = sensorNanos, receivedNanos = sensorNanos + 10),
        intrinsics = CameraIntrinsics(width = 2, height = 2, fx = 100f, fy = 101f, cx = 1f, cy = 1f),
        pose = PoseMatrix4x4(poseValues),
        depth = depth,
        confidence = confidence,
        color = null,
        quality = FrameQualityMetrics(
            trackingState = TrackingState.TRACKING,
            depthConfidence = 0.8f,
            coverage = 0.7f,
            motionRisk = 0.1f,
            blurRisk = 0.2f,
        ),
    )
}

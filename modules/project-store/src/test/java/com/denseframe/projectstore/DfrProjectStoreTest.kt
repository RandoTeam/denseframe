package com.denseframe.projectstore

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

class DfrProjectStoreTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val writer = DfrProjectWriter(clockMillis = { 1_700_000_000_000 })
    private val reader = DfrProjectReader()
    private val validator = DfrProjectValidator(reader)

    @Test
    fun createEmptyProject() {
        val created = createProject()

        assertTrue(Files.exists(created.projectDir.resolve("manifest.json")))
        assertTrue(Files.isDirectory(created.projectDir.resolve("frames")))
        assertTrue(Files.isDirectory(created.projectDir.resolve("reconstruction")))
        assertTrue(Files.isDirectory(created.projectDir.resolve("thumbnails")))
        assertEquals(emptyList<FrameManifest>(), reader.readManifest(created.projectDir).frames)
    }

    @Test
    fun appendOneFrameWithPlaceholderPayloads() {
        val created = createProject()
        val result = appendFrame(created.projectDir)

        assertTrue(result is AppendFrameResult.Appended)
        val frameDir = created.projectDir.resolve("frames").resolve("00000001")
        assertTrue(Files.exists(frameDir.resolve("frame.json")))
        assertTrue(Files.exists(frameDir.resolve("color.yuv")))
        assertTrue(Files.exists(frameDir.resolve("depth_u16.bin")))
        assertTrue(Files.exists(frameDir.resolve("confidence_u8.bin")))
        assertTrue(Files.exists(frameDir.resolve("checksum.sha256")))
    }

    @Test
    fun readProjectManifest() {
        val created = createProject()
        appendFrame(created.projectDir)

        val manifest = reader.readManifest(created.projectDir)

        assertEquals(ProjectManifest.DFR_FORMAT, manifest.format)
        assertEquals(DfrSchemaVersion.V1, manifest.schemaVersion)
        assertEquals(ProjectId("project-test"), manifest.projectId)
        assertEquals(1, manifest.frames.size)
        assertEquals("world_from_camera_column_major", manifest.capture.coordinateConvention)
    }

    @Test
    fun verifyChecksumSuccess() {
        val created = createProject()
        appendFrame(created.projectDir)

        val validation = validator.validate(created.projectDir)

        assertTrue(validation.issues.joinToString(), validation.valid)
    }

    @Test
    fun detectChecksumFailure() {
        val created = createProject()
        appendFrame(created.projectDir)
        Files.write(created.projectDir.resolve("frames/00000001/depth_u16.bin"), byteArrayOf(9, 9, 9))

        val validation = validator.validate(created.projectDir)

        assertFalse(validation.valid)
        assertTrue(validation.issues.any { it.contains("Checksum mismatch") })
    }

    @Test
    fun detectIncompleteFrameFolder() {
        val created = createProject()
        Files.createDirectories(created.projectDir.resolve("frames/00000077"))
        Files.write(created.projectDir.resolve("frames/00000077/color.yuv"), byteArrayOf(1))

        val opened = reader.open(created.projectDir)

        assertTrue(opened is ProjectOpenResult.Opened)
        val issues = (opened as ProjectOpenResult.Opened).frameIssues
        assertTrue(issues.any { it.frameDirectory == "00000077" && it.reason.contains("not referenced") })
        assertTrue(issues.any { it.frameDirectory == "00000077" && it.reason.contains("Missing frame.json") })
    }

    @Test
    fun goldenJsonCompatibilityAllowsUnknownFutureFields() {
        val projectDir = temporaryFolder.newFolder("golden.dfr").toPath()
        Files.createDirectories(projectDir.resolve("frames"))
        Files.createDirectories(projectDir.resolve("reconstruction"))
        Files.createDirectories(projectDir.resolve("thumbnails"))
        Files.write(
            projectDir.resolve("manifest.json"),
            GOLDEN_MANIFEST_WITH_UNKNOWN_FIELD.toByteArray(StandardCharsets.UTF_8),
        )

        val manifest = reader.readManifest(projectDir)

        assertEquals("golden-project", manifest.projectId.value)
        assertEquals("object", manifest.capture.mode)
        assertEquals(0, manifest.frames.size)
    }

    @Test
    fun atomicWriterDoesNotLeaveFinalFileOnSimulatedFailure() {
        val target = temporaryFolder.newFolder("atomic").toPath().resolve("manifest.json")
        val atomic = AtomicFileWriter()

        try {
            atomic.write(target) { temp ->
                Files.write(temp, "partial".toByteArray(StandardCharsets.UTF_8))
                throw IOException("simulated write failure")
            }
        } catch (_: IOException) {
            // Expected.
        }

        assertFalse(Files.exists(target))
    }

    private fun createProject(): CreatedProject = writer.createProject(
        parentDir = temporaryFolder.newFolder("projects").toPath(),
        projectId = ProjectId("project-test"),
        deviceInfo = DeviceInfo(
            manufacturer = "DenseFrame",
            model = "JUnit",
            osVersion = "test",
        ),
        captureInfo = CaptureInfo(
            mode = "object",
            coordinateConvention = "world_from_camera_column_major",
        ),
        reconstructionDefaults = ReconstructionDefaults(
            minDepthMeters = 0.2f,
            maxDepthMeters = 5.0f,
            minConfidence = 128,
        ),
    )

    private fun appendFrame(projectDir: Path): AppendFrameResult = writer.appendFrame(
        projectDir = projectDir,
        request = FrameWriteRequest(
            timestampNanos = 123_456_789,
            trackingState = "tracking",
            cameraIntrinsics = CameraIntrinsics(
                width = 2,
                height = 2,
                fx = 100f,
                fy = 101f,
                cx = 1f,
                cy = 1.5f,
            ),
            pose = PoseMatrix(
                listOf(
                    1f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f,
                    0f, 0f, 1f, 0f,
                    0f, 0f, 0f, 1f,
                ),
            ),
            quality = FrameQuality(
                depthConfidence = 0.75f,
                coverage = 0.5f,
                motionRisk = 0.1f,
                accepted = true,
            ),
            payloads = FramePayloadSources(
                colorYuv = payload("color.yuv", byteArrayOf(1, 2, 3)),
                depthU16 = payload("depth_u16.bin", byteArrayOf(4, 5, 6, 7)),
                confidenceU8 = payload("confidence_u8.bin", byteArrayOf(8, 9)),
            ),
        ),
    )

    private fun payload(fileName: String, bytes: ByteArray): PayloadSource = PayloadSource(fileName) {
        ByteArrayInputStream(bytes)
    }

    private companion object {
        private const val GOLDEN_MANIFEST_WITH_UNKNOWN_FIELD = """
{
  "format": "denseframe.raw",
  "schemaVersion": 1,
  "projectId": "golden-project",
  "createdAtEpochMillis": 1700000000000,
  "updatedAtEpochMillis": 1700000000000,
  "futureField": {
    "ignored": true
  },
  "device": {
    "manufacturer": "DenseFrame",
    "model": "Golden",
    "osVersion": "test"
  },
  "capture": {
    "mode": "object",
    "coordinateConvention": "world_from_camera_column_major"
  },
  "reconstructionDefaults": {
    "minDepthMeters": 0.2,
    "maxDepthMeters": 5.0,
    "minConfidence": 128
  },
  "frames": []
}
"""
    }
}

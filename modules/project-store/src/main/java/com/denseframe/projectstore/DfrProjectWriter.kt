package com.denseframe.projectstore

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.name

class DfrProjectWriter(
    private val atomicFileWriter: AtomicFileWriter = AtomicFileWriter(),
    private val clockMillis: () -> Long = { System.currentTimeMillis() },
) {
    fun createProject(
        parentDir: Path,
        deviceInfo: DeviceInfo,
        captureInfo: CaptureInfo,
        reconstructionDefaults: ReconstructionDefaults,
        projectId: ProjectId = ProjectIdGenerator.generate(),
    ): CreatedProject {
        Files.createDirectories(parentDir)
        val projectDir = parentDir.resolve("${projectId.value}.dfr")
        Files.createDirectories(projectDir)
        Files.createDirectories(projectDir.resolve("frames"))
        Files.createDirectories(projectDir.resolve("reconstruction"))
        Files.createDirectories(projectDir.resolve("thumbnails"))
        val now = clockMillis()
        val manifest = ProjectManifest(
            projectId = projectId,
            createdAtEpochMillis = now,
            updatedAtEpochMillis = now,
            device = deviceInfo,
            capture = captureInfo,
            reconstructionDefaults = reconstructionDefaults,
        )
        writeManifest(projectDir, manifest)
        return CreatedProject(projectDir, manifest)
    }

    fun appendFrame(projectDir: Path, request: FrameWriteRequest): AppendFrameResult = try {
        val manifest = DfrProjectReader().readManifest(projectDir)
        val frameId = (manifest.frames.maxOfOrNull { it.frameId } ?: 0) + 1
        val frameName = frameId.toString().padStart(8, '0')
        val framesDir = projectDir.resolve("frames")
        val finalFrameDir = framesDir.resolve(frameName)
        val tempFrameDir = framesDir.resolve(".$frameName.tmp-${java.util.UUID.randomUUID()}")
        require(!Files.exists(finalFrameDir)) { "Frame directory already exists: $frameName" }
        Files.createDirectories(tempFrameDir)
        try {
            val payloadRefs = FramePayloadRefs(
                colorYuv = request.payloads.colorYuv?.fileName,
                depthU16 = request.payloads.depthU16?.fileName,
                confidenceU8 = request.payloads.confidenceU8?.fileName,
                colorFormat = request.payloads.colorFormat,
                depthFormat = request.payloads.depthFormat,
                confidenceFormat = request.payloads.confidenceFormat,
            )
            val checksums = writePayloads(tempFrameDir, request.payloads, payloadRefs)
            val frameManifest = FrameManifest(
                frameId = frameId,
                timestampNanos = request.timestampNanos,
                trackingState = request.trackingState,
                cameraIntrinsics = request.cameraIntrinsics,
                pose = request.pose,
                quality = request.quality,
                payloads = payloadRefs,
                checksums = checksums,
            )
            atomicFileWriter.writeText(tempFrameDir.resolve("frame.json"), DfrJson.frameToJson(frameManifest))
            atomicFileWriter.writeText(tempFrameDir.resolve("checksum.sha256"), DfrJson.checksumFileToText(checksums))
            AtomicFileWriter.fsyncDirectoryIfPractical(tempFrameDir)
            moveFrameDirectory(tempFrameDir, finalFrameDir)
            val updatedManifest = manifest.copy(
                updatedAtEpochMillis = clockMillis(),
                frames = manifest.frames + frameManifest,
            )
            writeManifest(projectDir, updatedManifest)
            AppendFrameResult.Appended(frameManifest)
        } catch (throwable: Throwable) {
            deleteRecursivelyIfExists(tempFrameDir)
            throw throwable
        }
    } catch (throwable: Throwable) {
        AppendFrameResult.Failed(throwable.message ?: "Failed to append frame", throwable)
    }

    private fun writePayloads(
        frameDir: Path,
        sources: FramePayloadSources,
        refs: FramePayloadRefs,
    ): List<Checksum> {
        val ordered = listOf(
            refs.colorYuv to sources.colorYuv,
            refs.depthU16 to sources.depthU16,
            refs.confidenceU8 to sources.confidenceU8,
        ).filter { (relativePath, source) -> relativePath != null || source != null }
        return ordered.map { (relativePath, source) ->
            val payloadPath = requireNotNull(relativePath) { "Payload reference/source mismatch" }
            val payloadSource = requireNotNull(source) { "Payload reference/source mismatch" }
            require(payloadSource.fileName == payloadPath) { "Payload source file name mismatch: ${payloadSource.fileName}" }
            val destination = frameDir.resolve(payloadPath)
            atomicFileWriter.writeStream(destination, payloadSource.openStream())
            Checksum(
                path = payloadPath,
                algorithm = Sha256Checksum.ALGORITHM,
                value = Sha256Checksum.file(destination),
            )
        }
    }

    private fun writeManifest(projectDir: Path, manifest: ProjectManifest) {
        atomicFileWriter.writeText(projectDir.resolve("manifest.json"), DfrJson.manifestToJson(manifest))
    }

    private fun moveFrameDirectory(source: Path, target: Path) {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE)
        } catch (_: java.nio.file.AtomicMoveNotSupportedException) {
            Files.move(source, target)
        }
        AtomicFileWriter.fsyncDirectoryIfPractical(target.parent)
    }

    private fun deleteRecursivelyIfExists(path: Path) {
        if (!Files.exists(path)) return
        Files.walk(path).use { stream ->
            stream.sorted(Comparator.reverseOrder()).forEach { Files.deleteIfExists(it) }
        }
    }
}

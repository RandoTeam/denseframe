package com.denseframe.projectstore

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name

class DfrProjectReader {
    fun open(projectDir: Path): ProjectOpenResult {
        return try {
            val manifest = readManifest(projectDir)
            if (manifest.schemaVersion != DfrSchemaVersion.V1) {
                ProjectOpenResult.Failed("Unsupported DFR schema version: ${manifest.schemaVersion.value}")
            } else if (manifest.format != ProjectManifest.DFR_FORMAT) {
                ProjectOpenResult.Failed("Unsupported DFR format: ${manifest.format}")
            } else {
                ProjectOpenResult.Opened(manifest, detectFrameIssues(projectDir, manifest))
            }
        } catch (throwable: Throwable) {
            ProjectOpenResult.Failed(throwable.message ?: "Failed to open project", throwable)
        }
    }

    fun readManifest(projectDir: Path): ProjectManifest {
        val json = String(Files.readAllBytes(projectDir.resolve("manifest.json")), StandardCharsets.UTF_8)
        return DfrJson.manifestFromJson(json)
    }

    fun readFrameManifest(frameDir: Path): FrameManifest {
        val json = String(Files.readAllBytes(frameDir.resolve("frame.json")), StandardCharsets.UTF_8)
        return DfrJson.frameFromJson(json)
    }

    private fun detectFrameIssues(projectDir: Path, manifest: ProjectManifest): List<FrameIssue> {
        val issues = mutableListOf<FrameIssue>()
        val framesDir = projectDir.resolve("frames")
        if (!Files.exists(framesDir)) {
            return listOf(FrameIssue("frames", "Missing frames directory"))
        }
        val referenced = manifest.frames.map { it.frameId.toString().padStart(8, '0') }.toSet()
        Files.list(framesDir).use { stream ->
            stream
                .filter { Files.isDirectory(it) }
                .filter { !it.name.startsWith(".") }
                .sorted()
                .forEach { frameDir ->
                    val name = frameDir.name
                    if (name !in referenced) {
                        issues += FrameIssue(name, "Frame directory is not referenced by manifest")
                    }
                    issues += inspectFrameDirectory(frameDir)
                }
        }
        return issues
    }

    private fun inspectFrameDirectory(frameDir: Path): List<FrameIssue> {
        val issues = mutableListOf<FrameIssue>()
        val name = frameDir.name
        val frameJson = frameDir.resolve("frame.json")
        val checksum = frameDir.resolve("checksum.sha256")
        if (!Files.exists(frameJson)) {
            issues += FrameIssue(name, "Missing frame.json")
        }
        if (!Files.exists(checksum)) {
            issues += FrameIssue(name, "Missing checksum.sha256")
        }
        if (issues.isNotEmpty()) return issues
        val frame = try {
            readFrameManifest(frameDir)
        } catch (throwable: Throwable) {
            return listOf(FrameIssue(name, "Invalid frame.json: ${throwable.message}"))
        }
        listOf(frame.payloads.colorYuv, frame.payloads.depthU16, frame.payloads.confidenceU8).forEach { relative ->
            if (!Files.exists(frameDir.resolve(relative))) {
                issues += FrameIssue(name, "Missing payload: $relative")
            }
        }
        return issues
    }
}

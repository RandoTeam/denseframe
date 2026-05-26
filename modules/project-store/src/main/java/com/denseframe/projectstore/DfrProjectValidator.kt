package com.denseframe.projectstore

import java.nio.file.Files
import java.nio.file.Path

class DfrProjectValidator(
    private val reader: DfrProjectReader = DfrProjectReader(),
) {
    fun validate(projectDir: Path): ValidationResult {
        val issues = mutableListOf<String>()
        val open = reader.open(projectDir)
        val manifest = when (open) {
            is ProjectOpenResult.Failed -> return ValidationResult(false, listOf(open.reason))
            is ProjectOpenResult.Opened -> {
                issues += open.frameIssues.map { "${it.frameDirectory}: ${it.reason}" }
                open.manifest
            }
        }
        manifest.frames.forEach { frame ->
            val frameDir = projectDir.resolve("frames").resolve(frame.frameId.toString().padStart(8, '0'))
            if (!Files.exists(frameDir)) {
                issues += "${frame.frameId}: Missing frame directory"
                return@forEach
            }
            frame.checksums.forEach { checksum ->
                val payload = frameDir.resolve(checksum.path)
                if (!Files.exists(payload)) {
                    issues += "${frame.frameId}: Missing checksummed payload ${checksum.path}"
                } else if (checksum.algorithm != Sha256Checksum.ALGORITHM) {
                    issues += "${frame.frameId}: Unsupported checksum algorithm ${checksum.algorithm}"
                } else {
                    val actual = Sha256Checksum.file(payload)
                    if (actual != checksum.value) {
                        issues += "${frame.frameId}: Checksum mismatch for ${checksum.path}"
                    }
                }
            }
        }
        return ValidationResult(valid = issues.isEmpty(), issues = issues)
    }
}

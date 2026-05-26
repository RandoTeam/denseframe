package com.denseframe.projectstore

import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption

class AtomicFileWriter {
    fun writeText(path: Path, text: String) {
        writeBytes(path, text.toByteArray(StandardCharsets.UTF_8))
    }

    fun writeBytes(path: Path, bytes: ByteArray) {
        write(path) { temp ->
            Files.write(temp, bytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)
        }
    }

    fun writeStream(path: Path, input: InputStream) {
        write(path) { temp ->
            input.use { stream ->
                Files.newOutputStream(temp, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE).use { output ->
                    stream.copyTo(output)
                    output.flush()
                }
            }
        }
    }

    fun write(path: Path, writeTemp: (Path) -> Unit) {
        val parent = path.parent
        if (parent != null) {
            Files.createDirectories(parent)
        }
        val temp = path.resolveSibling("${path.fileName}.tmp-${java.util.UUID.randomUUID()}")
        try {
            writeTemp(temp)
            fsyncFileIfPractical(temp)
            moveAtomically(temp, path)
            fsyncDirectoryIfPractical(parent)
        } catch (throwable: Throwable) {
            Files.deleteIfExists(temp)
            throw throwable
        }
    }

    companion object {
        fun moveAtomically(source: Path, target: Path) {
            try {
                Files.move(source, target, StandardCopyOption.ATOMIC_MOVE)
            } catch (_: java.nio.file.AtomicMoveNotSupportedException) {
                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING)
            }
        }

        fun fsyncFileIfPractical(path: Path) {
            try {
                Files.newByteChannel(path, StandardOpenOption.WRITE).use { channel ->
                    if (channel is java.nio.channels.FileChannel) {
                        channel.force(true)
                    }
                }
            } catch (_: RuntimeException) {
                // Some filesystems do not support forcing metadata from this context.
            }
        }

        fun fsyncDirectoryIfPractical(path: Path?) {
            if (path == null) return
            try {
                Files.newByteChannel(path, StandardOpenOption.READ).use { channel ->
                    if (channel is java.nio.channels.FileChannel) {
                        channel.force(true)
                    }
                }
            } catch (_: Exception) {
                // Directory fsync is best-effort on Android and Windows.
            }
        }
    }
}

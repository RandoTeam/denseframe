package com.denseframe.capturearcore

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES20
import com.google.ar.core.Config
import com.google.ar.core.Session

class ArCoreSessionController {
    private var session: Session? = null
    var state: ArCoreSessionState = ArCoreSessionState.IDLE
        private set
    var selectedDepthModeName: String? = null
        private set

    fun create(context: Context): Result<ArCoreSessionState> = runCatching {
        check(session == null) { "ARCore session is already active" }
        state = ArCoreSessionState.CREATED
        session = Session(context)
        state
    }.onFailure { state = ArCoreSessionState.FAILED }

    fun configureDepth(): ArCoreCapabilityResult {
        val active = requireNotNull(session) { "ARCore session is not created" }
        val config = active.config
        val capability = ArCoreDepthModeSelector.select(
            object : ArCoreDepthModeSupport {
                override fun isRawDepthOnlySupported(): Boolean = active.isDepthModeSupported(Config.DepthMode.RAW_DEPTH_ONLY)
                override fun isAutomaticSupported(): Boolean = active.isDepthModeSupported(Config.DepthMode.AUTOMATIC)
            },
        )
        when (capability.depthSupport) {
            ArCoreDepthSupport.RawDepthOnly -> {
                config.depthMode = Config.DepthMode.RAW_DEPTH_ONLY
                selectedDepthModeName = capability.selectedDepthModeName
            }
            ArCoreDepthSupport.AutomaticFallback -> {
                config.depthMode = Config.DepthMode.AUTOMATIC
                selectedDepthModeName = capability.selectedDepthModeName
            }
            ArCoreDepthSupport.Unsupported -> {
                selectedDepthModeName = null
            }
        }
        if (capability.depthSupport != ArCoreDepthSupport.Unsupported) {
            active.configure(config)
            state = ArCoreSessionState.CONFIGURED
        }
        return capability
    }

    fun resume(): ArCoreSessionError? = try {
        session?.resume()
        state = ArCoreSessionState.RESUMED
        null
    } catch (throwable: Throwable) {
        state = ArCoreSessionState.FAILED
        ArCoreExceptionMapper.map(throwable)
    }

    fun pause() {
        session?.pause()
        if (state != ArCoreSessionState.CLOSED) state = ArCoreSessionState.PAUSED
    }

    fun close() {
        session?.close()
        session = null
        state = ArCoreSessionState.CLOSED
    }

    fun setCameraTextureName(textureName: Int) {
        session?.setCameraTextureName(textureName)
    }

    fun setDisplayGeometry(rotation: Int, width: Int, height: Int) {
        session?.setDisplayGeometry(rotation, width, height)
    }

    fun update(packetSource: ArCoreFramePacketSource): ArCoreFramePacketResult {
        val active = session ?: return ArCoreFramePacketResult.Fatal(
            ArCoreSessionError("session_missing", "ARCore session is not active.", true),
            packetSource.metrics(),
        )
        return try {
            packetSource.acquire(RealArCoreFrame(active.update()))
        } catch (throwable: Throwable) {
            ArCoreFramePacketResult.Fatal(ArCoreExceptionMapper.map(throwable), packetSource.metrics())
        }
    }
}

object ArCoreExternalTexture {
    fun create(): Int {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0])
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        return textures[0]
    }
}

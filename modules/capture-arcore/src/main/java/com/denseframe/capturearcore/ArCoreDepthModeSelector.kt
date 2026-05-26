package com.denseframe.capturearcore

interface ArCoreDepthModeSupport {
    fun isRawDepthOnlySupported(): Boolean
    fun isAutomaticSupported(): Boolean
}

object ArCoreDepthModeSelector {
    fun select(support: ArCoreDepthModeSupport): ArCoreCapabilityResult = when {
        support.isRawDepthOnlySupported() -> ArCoreCapabilityResult(ArCoreDepthSupport.RawDepthOnly, "RAW_DEPTH_ONLY")
        support.isAutomaticSupported() -> ArCoreCapabilityResult(ArCoreDepthSupport.AutomaticFallback, "AUTOMATIC")
        else -> ArCoreCapabilityResult(ArCoreDepthSupport.Unsupported, null)
    }
}

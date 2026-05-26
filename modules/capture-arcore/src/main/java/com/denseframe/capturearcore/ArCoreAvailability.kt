package com.denseframe.capturearcore

import android.app.Activity
import android.content.Context
import com.google.ar.core.ArCoreApk

class ArCoreAvailabilityChecker {
    fun check(context: Context): ArCoreAvailabilityStatus = map(ArCoreApk.getInstance().checkAvailability(context))

    fun map(availability: ArCoreApk.Availability): ArCoreAvailabilityStatus = when (availability) {
        ArCoreApk.Availability.SUPPORTED_INSTALLED -> ArCoreAvailabilityStatus.SupportedInstalled
        ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD -> ArCoreAvailabilityStatus.SupportedApkTooOld
        ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> ArCoreAvailabilityStatus.SupportedNotInstalled
        ArCoreApk.Availability.UNKNOWN_CHECKING -> ArCoreAvailabilityStatus.Checking
        ArCoreApk.Availability.UNKNOWN_TIMED_OUT -> ArCoreAvailabilityStatus.TimedOut
        ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE -> ArCoreAvailabilityStatus.UnsupportedDevice
        ArCoreApk.Availability.UNKNOWN_ERROR -> ArCoreAvailabilityStatus.Unknown
    }
}

class ArCoreInstallCoordinator {
    fun requestInstall(activity: Activity, userRequestedInstall: Boolean): ArCoreInstallResult = try {
        when (ArCoreApk.getInstance().requestInstall(activity, userRequestedInstall)) {
            ArCoreApk.InstallStatus.INSTALLED -> ArCoreInstallResult.Installed
            ArCoreApk.InstallStatus.INSTALL_REQUESTED -> ArCoreInstallResult.InstallRequested
        }
    } catch (throwable: Throwable) {
        ArCoreInstallResult.Failed(ArCoreExceptionMapper.map(throwable))
    }
}

package io.snaps.coreui.tools

import android.os.Build

object EmulatorChecker {
    fun isDeviceEmulated(): Boolean {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator") ||
                Build.MODEL.contains("VirtualBox") ||
                ("QC_Reference_Phone" == Build.BOARD && !"Xiaomi".equals(Build.MANUFACTURER, ignoreCase = true)) ||
                Build.HOST == "Build2" ||
                Build.PRODUCT == "google_sdk" ||
                System.getProperties().getProperty("ro.kernel.qemu") == "1" ||
                Build.FINGERPRINT.startsWith("google/sdk_gphone") &&
                Build.FINGERPRINT.endsWith(":user/release-keys") &&
                Build.MANUFACTURER == "Google" &&
                Build.PRODUCT.startsWith("sdk_gphone") &&
                Build.BRAND == "google" &&
                Build.MODEL.startsWith("sdk_gphone")
    }
}
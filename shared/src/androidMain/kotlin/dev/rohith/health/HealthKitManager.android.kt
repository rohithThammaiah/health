package dev.rohith.health

import android.content.Context

lateinit var appContext: Context

actual fun getHealthKitManager(): HealthKitManager {
    return AndroidHealthKitManager(
        appContext,
        appContext.packageName
    )
}

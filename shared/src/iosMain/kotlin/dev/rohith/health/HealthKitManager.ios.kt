package dev.rohith.health

actual fun getHealthKitManager(): HealthKitManager {
    return Any() as HealthKitManager
}
package dev.rohith.health

data class HealthRecord(
    val steps: Long,
    val distanceInMeters: Double,
    val caloriesBurned: Double,
    val maxHeartRate: Long,
)
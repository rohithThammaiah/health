package dev.rohith.health

data class HealthRecord(
    val steps: Long,
    val distanceInMeters: Double,
    val caloriesBurned: Double,
    val maxHeartRate: Long,
)

fun HealthRecord?.orEmptyRecord(): HealthRecord = if (this == null) HealthRecord(
    steps = 0,
    distanceInMeters = 0.0,
    caloriesBurned = 0.0,
    maxHeartRate = 0,
) else this
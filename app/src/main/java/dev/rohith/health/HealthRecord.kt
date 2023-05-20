package dev.rohith.health

data class HealthRecord(
    val steps: Record,
    val distanceInMeters: Record,
    val caloriesBurned: Record,
    val maxHeartRate: Record,
)

data class Record(
    val name: String,
    val value: Double,
)

fun HealthRecord?.orEmptyRecord(): HealthRecord = this
    ?: HealthRecord(
        steps = Record("Steps", 0.0),
        distanceInMeters = Record("Distance", 0.0),
        caloriesBurned = Record("Calories", 0.0),
        maxHeartRate = Record("Peak Heart Rate", 0.0),
    )
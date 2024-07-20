package dev.rohith.health.data

data class HealthRecord(
    val steps: Record,
    val distanceInMeters: Record,
    val caloriesBurned: Record,
    val maxHeartRate: Record,
)

fun HealthRecord?.orEmptyRecord(): HealthRecord = this
    ?: HealthRecord(
        steps = Record("Steps", 0.0, HealthStat.STEPS),
        distanceInMeters = Record("Distance", 0.0, HealthStat.DISTANCE_COVERED),
        caloriesBurned = Record("Calories", 0.0, HealthStat.CALORIES_BURNED),
        maxHeartRate = Record("Peak Heart Rate", 0.0, HealthStat.PEAK_HEART_BEAT),
    )
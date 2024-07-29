package dev.rohith.health

import dev.rohith.health.models.ActivityRecord
import dev.rohith.health.models.HeatMapData
import dev.rohith.health.models.Record
import kotlinx.datetime.Instant

expect fun getHealthKitManager(): HealthKitManager

interface HealthKitManager {

    fun getHeatMapData(): List<ActivityRecord>

    suspend fun getStatsForASingleDay(startTime: Instant, endTime: Instant): List<Record>

    suspend fun getRecentActivities(startTime: Instant, endTime: Instant): List<ActivityRecord>

    suspend fun isPermissionsGranted(): Boolean

    fun isHealthClientAvailable(): Boolean
}
package dev.rohith.health

import dev.rohith.health.models.ActivityRecord
import dev.rohith.health.models.Record
import kotlinx.datetime.Instant

class IosHealthKitManager: HealthKitManager {
    override fun getHeatMapData(): List<ActivityRecord> {
        return emptyList()
    }

    override suspend fun getStatsForASingleDay(startTime: Instant, endTime: Instant): List<Record> {
        return emptyList()
    }

    override suspend fun getRecentActivities(
        startTime: Instant,
        endTime: Instant
    ): List<ActivityRecord> {
        return emptyList()
    }

    override suspend fun isPermissionsGranted(): Boolean {
        return false
    }

    override fun isHealthClientAvailable(): Boolean {
        return false
    }
}
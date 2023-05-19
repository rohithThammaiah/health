package dev.rohith.health

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import java.time.Instant

class HealthKitManager(
    private val context: Context,
    private val providerPackageName: String,
) {

    fun getClient(): Either<HealthSDKError, HealthConnectClient> = either {
        val availabilityStatus = HealthConnectClient.sdkStatus(context, providerPackageName)
        ensure(availabilityStatus != HealthConnectClient.SDK_UNAVAILABLE) {
            SDKNotAvailableException()
        }
        ensure(availabilityStatus != HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
            SDKUpdateRequiredException()
        }
        val healthConnectClient = HealthConnectClient.getOrCreate(context)

        healthConnectClient
    }

    companion object {
        val PERMISSIONS = setOf(
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(DistanceRecord::class),
            HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        )
    }

    suspend fun isPermissionsGranted(healthConnectClient: HealthConnectClient,): Boolean {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        return granted.containsAll(PERMISSIONS)
    }

    suspend fun aggregateDistance(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ) = either<Throwable, HealthRecord>{
        try {
            val response = healthConnectClient.aggregate(
                AggregateRequest(
                    metrics = setOf(
                        DistanceRecord.DISTANCE_TOTAL,
                        StepsRecord.COUNT_TOTAL,
                        ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL,
                        TotalCaloriesBurnedRecord.ENERGY_TOTAL,
                        HeartRateRecord.BPM_MAX,
                        HeartRateRecord.BPM_MIN,
                    ),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            // The result may be null if no data is available in the time range.
            val distanceTotalInMeters = response[DistanceRecord.DISTANCE_TOTAL]?.inMeters ?: 0.0
            val stepsRecord = response[StepsRecord.COUNT_TOTAL] ?: 0L
            val activeCaloriesBurnedRecord = response[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inKilocalories ?: 0.0
            val totalCaloriesBurnedRecord = response[TotalCaloriesBurnedRecord.ENERGY_TOTAL]?.inKilocalories ?: 0.0
            // The result may be null if no data is available in the time range.
            val minimumHeartRate = response[HeartRateRecord.BPM_MIN]
            val maximumHeartRate = response[HeartRateRecord.BPM_MAX] ?: 0L

            HealthRecord(
                steps = stepsRecord,
                distanceInMeters = distanceTotalInMeters,
                caloriesBurned = totalCaloriesBurnedRecord,
                maxHeartRate = maximumHeartRate,
            )
        } catch (e: Exception) {
            // Run error handling here.
            raise(e)
        }
    }

    suspend fun aggregateHeartRate(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ) {
        try {
            val response =
                healthConnectClient.aggregate(
                    AggregateRequest(
                        setOf(HeartRateRecord.BPM_MAX, HeartRateRecord.BPM_MIN),
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                    )
                )
            // The result may be null if no data is available in the time range.
            val minimumHeartRate = response[HeartRateRecord.BPM_MIN]
            val maximumHeartRate = response[HeartRateRecord.BPM_MAX]
        } catch (e: Exception) {
            // Run error handling here.
        }
    }



    suspend fun checkPermissionsAndRun(
        healthConnectClient: HealthConnectClient,
        requestPermissions: ActivityResultLauncher<Set<String>>
    ) {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        if (granted.containsAll(PERMISSIONS)) {
        } else {
            requestPermissions.launch(PERMISSIONS)
        }
    }
}

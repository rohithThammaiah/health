package dev.rohith.health

import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.ui.text.capitalize
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.ExerciseSessionRecord.Companion.EXERCISE_TYPE_INT_TO_STRING_MAP
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import java.time.Duration
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
            HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        )
    }

    suspend fun isPermissionsGranted(healthConnectClient: HealthConnectClient): Boolean {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        return granted.containsAll(PERMISSIONS)
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

    suspend fun readStats(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ) = either<Throwable, List<Record>> {
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
            val activeCaloriesBurnedRecord =
                response[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inKilocalories ?: 0.0
            val totalCaloriesBurnedRecord =
                response[TotalCaloriesBurnedRecord.ENERGY_TOTAL]?.inKilocalories ?: 0.0
            // The result may be null if no data is available in the time range.
            val minimumHeartRate = response[HeartRateRecord.BPM_MIN]
            val maximumHeartRate = response[HeartRateRecord.BPM_MAX] ?: 0L
            val healthRecords = mutableListOf<Record>()
            healthRecords.add(Record("Steps", stepsRecord.toDouble()))
            healthRecords.add(Record("Calories", distanceTotalInMeters))
            healthRecords.add(Record("Distance", distanceTotalInMeters))
            healthRecords.add(Record("Peak heart rate", maximumHeartRate.toDouble()))
            healthRecords
        } catch (e: Exception) {
            // Run error handling here.
            raise(e)
        }
    }

    suspend fun readActivities(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ): MutableList<ActivityRecord> {
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    ExerciseSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    ascendingOrder = false,
                )
            )


        val activities = mutableListOf<ActivityRecord>()

        for (exerciseRecord in response.records) {
            val healthRecord =
                readStats(healthConnectClient, exerciseRecord.startTime, exerciseRecord.endTime)
            val activityRecord = ActivityRecord(
                id = exerciseRecord.exerciseType,
                type = EXERCISE_TYPE_INT_TO_STRING_MAP[exerciseRecord.exerciseType]?.capitalize() ?: "Exercise",
                healthRecord = healthRecord.getOrNull() ?: emptyList(),
                duration = Duration.between(exerciseRecord.startTime, exerciseRecord.endTime),
                timeStamp = exerciseRecord.startTime,
            )
            activities.add(activityRecord)
            Log.e("HealthKitManager", healthRecord.toString())
            Log.e("HealthKitManager", activityRecord.toString())
        }
        return activities
    }
}


data class ActivityRecord(
    val id: Int,
    val type: String,
    val healthRecord: List<Record>,
    val duration: Duration,
    val timeStamp: Instant
)

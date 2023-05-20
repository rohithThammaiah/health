package dev.rohith.health

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
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
import java.util.Locale

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
        if (granted.containsAll(PERMISSIONS).not()) {
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
            val distanceTotalInMeters = response[DistanceRecord.DISTANCE_TOTAL]?.inMeters ?: 0.0
            val stepsRecord = response[StepsRecord.COUNT_TOTAL] ?: 0L
            val totalCaloriesBurnedRecord =
                response[TotalCaloriesBurnedRecord.ENERGY_TOTAL]?.inKilocalories ?: 0.0
            val maximumHeartRate = response[HeartRateRecord.BPM_MAX] ?: 0L
            val healthRecords = mutableListOf<Record>()
            healthRecords.add(Record("Steps", stepsRecord.toDouble(), HealthStat.STEPS))
            healthRecords.add(
                Record(
                    "Calories",
                    totalCaloriesBurnedRecord,
                    HealthStat.CALORIES_BURNED
                )
            )
            healthRecords.add(
                Record(
                    "Distance",
                    distanceTotalInMeters,
                    HealthStat.DISTANCE_COVERED
                )
            )
            healthRecords.add(
                Record(
                    "Peak heart rate",
                    maximumHeartRate.toDouble(),
                    HealthStat.PEAK_HEART_BEAT
                )
            )
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
                type = EXERCISE_TYPE_INT_TO_STRING_MAP[exerciseRecord.exerciseType]?.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                    ?: "Exercise",
                healthRecord = healthRecord.getOrNull() ?: emptyList(),
                duration = Duration.between(exerciseRecord.startTime, exerciseRecord.endTime),
                timeStamp = exerciseRecord.startTime,
            )
            activities.add(activityRecord)
        }
        return activities
    }
}

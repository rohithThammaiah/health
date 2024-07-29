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
import dev.rohith.health.models.ActivityRecord
import dev.rohith.health.models.HealthSDKError
import dev.rohith.health.models.HealthStat
import dev.rohith.health.models.Record
import dev.rohith.health.models.SDKNotAvailableException
import dev.rohith.health.models.SDKUpdateRequiredException
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import java.time.Duration
import java.util.Locale
import kotlin.time.toKotlinDuration

class AndroidHealthKitManager(
    private val context: Context,
    private val providerPackageName: String,
) : HealthKitManager {

    private var healthKitClient: HealthConnectClient? = null

    private fun getClient(): Either<HealthSDKError, HealthConnectClient> = either {
        if (healthKitClient != null)
            healthKitClient

        val availabilityStatus = HealthConnectClient.getSdkStatus(context, "com.google.android.apps.healthdata")
        ensure(availabilityStatus != HealthConnectClient.SDK_UNAVAILABLE) {
            SDKNotAvailableException()
        }
        ensure(availabilityStatus != HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
            SDKUpdateRequiredException()
        }
        healthKitClient = HealthConnectClient.getOrCreate(context)

        healthKitClient!!
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

    suspend fun checkPermissionsAndRun(
        requestPermissions: ActivityResultLauncher<Set<String>>
    ) {
        val granted = getClient().getOrNull()?.permissionController?.getGrantedPermissions()
        if (granted?.containsAll(PERMISSIONS) != true) {
            requestPermissions.launch(PERMISSIONS)
        }
    }

    override suspend fun isPermissionsGranted(): Boolean {
        val granted = getClient().getOrNull()?.permissionController?.getGrantedPermissions()
        return granted?.containsAll(PERMISSIONS) == true
    }

    override fun isHealthClientAvailable(): Boolean {
        return getClient().getOrNull() != null
    }

    override suspend fun getStatsForASingleDay(
        startTime: Instant,
        endTime: Instant
    ): List<Record> {
        try {
            val response = getClient().getOrNull()?.aggregate(
                AggregateRequest(
                    metrics = setOf(
                        DistanceRecord.DISTANCE_TOTAL,
                        StepsRecord.COUNT_TOTAL,
                        ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL,
                        TotalCaloriesBurnedRecord.ENERGY_TOTAL,
                        HeartRateRecord.BPM_MAX,
                        HeartRateRecord.BPM_MIN,
                    ),
                    timeRangeFilter = TimeRangeFilter.between(
                        startTime.toJavaInstant(),
                        endTime.toJavaInstant()
                    )
                )
            ) ?: return emptyList()
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
            return healthRecords
        } catch (e: Exception) {
            // Run error handling here.
            return emptyList()
        }
    }

    override suspend fun getRecentActivities(
        startTime: Instant,
        endTime: Instant
    ): List<ActivityRecord> {
        val response =
            getClient().getOrNull()?.readRecords(
                ReadRecordsRequest(
                    ExerciseSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(
                        startTime.toJavaInstant(),
                        endTime.toJavaInstant()
                    ),
                    ascendingOrder = false,
                )
            ) ?: return emptyList()


        val activities = mutableListOf<ActivityRecord>()

        for (exerciseRecord in response.records) {
            val healthRecord =
                getStatsForASingleDay(
                    exerciseRecord.startTime.toKotlinInstant(),
                    exerciseRecord.endTime.toKotlinInstant()
                )
            val activityRecord = ActivityRecord(
                id = exerciseRecord.exerciseType,
                type = EXERCISE_TYPE_INT_TO_STRING_MAP[exerciseRecord.exerciseType]?.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                    ?: "Exercise",
                healthRecord = healthRecord,
                duration = Duration.between(exerciseRecord.startTime, exerciseRecord.endTime)
                    .toKotlinDuration(),
                timeStamp = exerciseRecord.startTime.toKotlinInstant(),
            )
            activities.add(activityRecord)
        }
        return activities
    }

    override fun getHeatMapData(): List<ActivityRecord> {
        return emptyList()
    }
}
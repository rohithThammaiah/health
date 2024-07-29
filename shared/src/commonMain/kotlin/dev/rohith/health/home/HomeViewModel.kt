package dev.rohith.health.home

import androidx.lifecycle.viewModelScope
import dev.rohith.health.Event
import dev.rohith.health.State
import dev.rohith.health.HealthViewModel
import dev.rohith.health.Async
import dev.rohith.health.HealthKitManager
import dev.rohith.health.RecordUiModel
import dev.rohith.health.Success
import dev.rohith.health.Uninitialized
import dev.rohith.health.models.ActivityRecord
import dev.rohith.health.models.HealthStat
import dev.rohith.health.models.HeatMapData
import dev.rohith.health.resources.Res
import dev.rohith.health.resources.ic_chart_24
import dev.rohith.health.resources.ic_fire_department_24
import dev.rohith.health.resources.ic_map_24
import dev.rohith.health.resources.ic_walk_24
import dev.rohith.health.theme.DarkColorScheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt

data class HomeState(
    val selectedDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).date,
    val isHealthSDKAvailable: Async<Boolean> = Uninitialized,
    val isHealthSDKPermissionGranted: Async<Boolean> = Uninitialized,
    val healthRecord: Async<List<RecordUiModel>> = Uninitialized,
    val activities: Async<List<ActivityRecord>> = Uninitialized,
    val heatMapData: Async<List<HeatMapData>> = Uninitialized,
) : State

sealed class HomeEvents() : Event {
    data class OnDateSelected(val date: LocalDate): HomeEvents()
}

class HomeViewModel(
    state: HomeState,
    private val healthKitManager: HealthKitManager,
) : HealthViewModel<HomeState, HomeEvents>(state) {

    init {
        checkPermission()
        event.onEach {
            when (it) {
                is HomeEvents.OnDateSelected -> {
                    setSelectedDate(it.date)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun checkPermission() {
        viewModelScope.launch {
            val isPermissionGranted = healthKitManager.isPermissionsGranted()
            setState {
                copy(
                    isHealthSDKAvailable = Success(healthKitManager.isHealthClientAvailable()),
                    isHealthSDKPermissionGranted = Success(isPermissionGranted)
                )
            }

            if (isPermissionGranted) {
                getTodayStats()
                getActivities()
                getDataForHeatMap()
            }
        }
    }



    private fun setSelectedDate(selectedDate: LocalDate) {
        withState {
            val previouslySelectedTime = it.selectedDate
            if (selectedDate == previouslySelectedTime)
                return@withState
            setState {
                copy(selectedDate = selectedDate)
            }
            getActivities()
            getTodayStats()
            getDataForHeatMap()
        }

    }

    private fun getTodayStats() {
        withState { state ->
            viewModelScope.launch {
                val end = LocalDateTime(
                    state.selectedDate,
                    LocalTime.fromSecondOfDay(60 * 60 * 23)
                ).toInstant(TimeZone.UTC)
                val start =
                    LocalDateTime(state.selectedDate, LocalTime.fromSecondOfDay(0)).toInstant(
                        TimeZone.UTC
                    )
                val result = healthKitManager.getStatsForASingleDay(start, end)

                result.let {
                    setState {
                        val uiModels = it.map {
                            when (it.type) {
                                HealthStat.STEPS -> {
                                    RecordUiModel(
                                        name = it.name,
                                        value = "${it.value.roundToInt()}",
                                        background = DarkColorScheme.surface,
                                        onBackground = DarkColorScheme.onSurface,
                                        icon = Res.drawable.ic_walk_24,
                                    )
                                }

                                HealthStat.CALORIES_BURNED -> {
                                    RecordUiModel(
                                        name = it.name,
                                        value = "${
                                                (it.value / 1000.0)
                                        } Kcal",
                                        background = DarkColorScheme.surface,
                                        onBackground = DarkColorScheme.onSurface,
                                        icon = Res.drawable.ic_fire_department_24,
                                    )
                                }

                                HealthStat.DISTANCE_COVERED -> {
                                    RecordUiModel(
                                        name = it.name,
                                        value = "${
                                                (it.value / 1000.0)
                                        } Kms",
                                        background = DarkColorScheme.surface,
                                        onBackground = DarkColorScheme.onSurface,
                                        icon = Res.drawable.ic_map_24,
                                    )
                                }

                                HealthStat.PEAK_HEART_BEAT -> {
                                    RecordUiModel(
                                        name = it.name,
                                        value = "${it.value.roundToInt()} bpm",
                                        background = DarkColorScheme.surface,
                                        onBackground = DarkColorScheme.onSurface,
                                        icon = Res.drawable.ic_chart_24,
                                    )
                                }
                            }

                        }
                        copy(healthRecord = Success(uiModels))
                    }
                }
            }
        }
    }

    private fun getActivities() {
        withState { state ->
            viewModelScope.launch {
                val end = LocalDateTime(
                    state.selectedDate,
                    LocalTime.fromSecondOfDay(60 * 60 * 23)
                ).toInstant(TimeZone.UTC)
                val start =
                    LocalDateTime(state.selectedDate.minus(DatePeriod(months = 6)), LocalTime.fromSecondOfDay(0)).toInstant(
                        TimeZone.UTC
                    )
                val result = healthKitManager.getRecentActivities(
                    start,
                    end
                )

                val mapped = result.map { activityRecord ->
                    val final = activityRecord.healthRecord.map {
                        val finalRecord = when (it.type) {
                            HealthStat.PEAK_HEART_BEAT -> it.copy(
                                prettyValue = "${it.value.toInt()}",
                                unit = "bpm"
                            )

                            HealthStat.DISTANCE_COVERED -> it.copy(
                                prettyValue = (it.value / 1000.0).toString(),
                                unit = "km"
                            )

                            HealthStat.STEPS -> it.copy(prettyValue = "${it.value.roundToInt()}")
                            HealthStat.CALORIES_BURNED -> it.copy(
                                prettyValue = "${it.value.roundToInt()}",
                                unit = "kcal"
                            )
                        }

                        finalRecord
                    }
                    activityRecord.copy(healthRecord = final)
                }

                setState {
                    copy(activities = Success(mapped))
                }

            }
        }
    }

    private fun getDataForHeatMap() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
        val pastEightWeek = today.minus(DatePeriod(days = 7 * 8))
        viewModelScope.launch {
            val result = healthKitManager.getHeatMapData()

            val mapped = result.map { activityRecord ->
                val final = activityRecord.healthRecord.map {
                    val finalRecord = when (it.type) {
                        HealthStat.PEAK_HEART_BEAT -> it.copy(
                            prettyValue = "${it.value.toInt()}",
                            unit = "bpm"
                        )

                        HealthStat.DISTANCE_COVERED -> it.copy(
                            prettyValue =(it.value / 1000.0).toString(), unit = "km"
                        )

                        HealthStat.STEPS -> it.copy(prettyValue = "${it.value.roundToInt()}")
                        HealthStat.CALORIES_BURNED -> it.copy(
                            prettyValue = "${it.value.roundToInt()}",
                            unit = "kcal"
                        )
                    }

                    finalRecord
                }
                activityRecord.copy(healthRecord = final)
            }

            val activitiesByDate = mapped.groupBy {
                it.timeStamp.toLocalDateTime(TimeZone.UTC).date
            }

            val dates = mutableListOf<HeatMapData>()

            for (date in pastEightWeek.toEpochDays()..today.toEpochDays()) {
                val data =
                    HeatMapData(
                        date = LocalDate.fromEpochDays(date),
                        count = activitiesByDate[LocalDate.fromEpochDays(date)]?.size ?: 0
                    )
                // Log.e("Date", data.toString())
                dates.add(data)
            }

            setState {
                copy(
                    heatMapData = Success(dates)
                )
            }
        }
    }
}
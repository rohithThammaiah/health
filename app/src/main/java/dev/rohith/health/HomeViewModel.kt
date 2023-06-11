package dev.rohith.health

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.health.connect.client.HealthConnectClient
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import dev.rohith.health.ui.theme.DarkColorScheme
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalField
import kotlin.math.roundToInt

data class RecordUiModel(
    val name: String,
    val value: String,
    val background: Color,
    val onBackground: Color,
    @DrawableRes val icon: Int,
)

data class HomeState(
    val title: String = "Today",
    val isHealthSDKAvailable: Async<Boolean> = Uninitialized,
    val isHealthSDKPermissionGranted: Async<Boolean> = Uninitialized,
    val healthRecord: Async<List<RecordUiModel>> = Uninitialized,
    val activities: Async<List<ActivityRecord>> = Uninitialized,
) : MavericksState

class HomeViewModel(
    initialState: HomeState,
    val healthKitManager: HealthKitManager,
) : MavericksViewModel<HomeState>(initialState) {

    val healthConnectClient: HealthConnectClient
        get() = _healthConnectClient!!

    private var _healthConnectClient: HealthConnectClient? = null

    init {
        _healthConnectClient = healthKitManager.getClient().getOrNull()
        checkPermission()
    }

    private fun checkPermission() {
        viewModelScope.launch {
            val isPermissionGranted = healthKitManager.isPermissionsGranted(healthConnectClient)
            setState {
                copy(
                    isHealthSDKAvailable = Success(_healthConnectClient != null),
                    isHealthSDKPermissionGranted = Success(isPermissionGranted)
                )
            }

            if (isPermissionGranted) {
                getTodayStats()
                getActivities()
            }
        }
    }

    private fun getTodayStats() {
        viewModelScope.launch {
            val end = LocalDateTime.now()
                .with(ChronoField.HOUR_OF_DAY, 23)
                .with(ChronoField.MINUTE_OF_HOUR, 59)
                .with(ChronoField.SECOND_OF_MINUTE, 29)
            val start = end
                .with(ChronoField.HOUR_OF_DAY, 0)
                .with(ChronoField.MINUTE_OF_HOUR, 0)
                .with(ChronoField.SECOND_OF_MINUTE, 0)
            val result = healthKitManager.readStats(healthConnectClient, start.toInstant(ZoneOffset.UTC), end.toInstant(ZoneOffset.UTC))

            result.getOrNull()?.let {
                setState {
                    val uiModels = it.map {
                        when (it.type) {
                            HealthStat.STEPS -> {
                                RecordUiModel(
                                    name = it.name,
                                    value = "${it.value.roundToInt()}",
                                    background = DarkColorScheme.surface,
                                    onBackground = DarkColorScheme.onSurface,
                                    icon = R.drawable.ic_walk_24,
                                )
                            }
                            HealthStat.CALORIES_BURNED -> {
                                RecordUiModel(
                                    name = it.name,
                                    value = "${String.format("%.3f", (it.value / 1000.0))} Kcal",
                                    background = DarkColorScheme.surface,
                                    onBackground = DarkColorScheme.onSurface,
                                    icon = R.drawable.ic_fire_department_24,
                                )
                            }
                            HealthStat.DISTANCE_COVERED -> {
                                RecordUiModel(
                                    name = it.name,
                                    value = "${String.format("%.3f", (it.value / 1000.0))} Kms",
                                    background = DarkColorScheme.surface,
                                    onBackground = DarkColorScheme.onSurface,
                                    icon = R.drawable.ic_map_24,
                                )
                            }
                            HealthStat.PEAK_HEART_BEAT -> {
                                RecordUiModel(
                                    name = it.name,
                                    value = "${it.value.roundToInt()} bpm",
                                    background = DarkColorScheme.surface,
                                    onBackground = DarkColorScheme.onSurface,
                                    icon = R.drawable.ic_chart_24,
                                )
                            }
                        }

                    }
                    copy(healthRecord = Success(uiModels))
                }
            }
        }
    }

    private fun getActivities() {
        viewModelScope.launch {
            val end = LocalDateTime.now()
                .with(ChronoField.HOUR_OF_DAY, 23)
                .with(ChronoField.MINUTE_OF_HOUR, 59)
                .with(ChronoField.SECOND_OF_MINUTE, 29)
            val start = end
                .with(ChronoField.HOUR_OF_DAY, 0)
                .with(ChronoField.MINUTE_OF_HOUR, 0)
                .with(ChronoField.SECOND_OF_MINUTE, 0)
            val result = healthKitManager.readActivities(healthConnectClient, start.toInstant(ZoneOffset.UTC), end.toInstant(ZoneOffset.UTC))

            setState {
                copy(activities = Success(result))
            }

        }
    }

    companion object : MavericksViewModelFactory<HomeViewModel, HomeState> {

        override fun create(viewModelContext: ViewModelContext, state: HomeState): HomeViewModel {
            val healthKitManager =
                HealthKitManager(viewModelContext.activity, "com.google.android.apps.healthdata")
            return HomeViewModel(state, healthKitManager)
        }
    }

}

enum class HealthStat {
    STEPS,
    CALORIES_BURNED,
    DISTANCE_COVERED,
    PEAK_HEART_BEAT
}

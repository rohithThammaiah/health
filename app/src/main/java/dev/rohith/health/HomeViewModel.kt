package dev.rohith.health

import androidx.compose.ui.graphics.Color
import androidx.health.connect.client.HealthConnectClient
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

data class RecordUiModel(
    val name: String,
    val value: String,
    val background: Color,
    val onBackground: Color,
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
            val end = Instant.now()
            val start = end.minus(1, ChronoUnit.DAYS)
            val result = healthKitManager.readStats(healthConnectClient, start, end)

            result.getOrNull()?.let {
                setState {
                    val uiModels = it.map {
                        when (it.type) {
                            HealthStat.STEPS -> {
                                RecordUiModel(
                                    name = it.name,
                                    value = "${it.value.roundToInt()}",
                                    background = Color(0xFFfff2e6),
                                    onBackground = Color(0xFF000000),
                                )
                            }
                            HealthStat.CALORIES_BURNED -> {
                                RecordUiModel(
                                    name = it.name,
                                    value = "${String.format("%.3f", (it.value / 1000.0))} Kcal",
                                    background = Color(0xFFe8fee7),
                                    onBackground = Color(0xFF000000),
                                )
                            }
                            HealthStat.DISTANCE_COVERED -> {
                                RecordUiModel(
                                    name = it.name,
                                    value = "${String.format("%.3f", (it.value / 1000.0))} Kms",
                                    background = Color(0xFF98e1ee),
                                    onBackground = Color(0xFFFFFFFF),
                                )
                            }
                            HealthStat.PEAK_HEART_BEAT -> {
                                RecordUiModel(
                                    name = it.name,
                                    value = "${it.value.roundToInt()} bpm",
                                    background = Color(0xFFff4648),
                                    onBackground = Color(0xFFFFFFFF),
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
            val end = Instant.now()
            val start = end.minus(1, ChronoUnit.DAYS)
            val result = healthKitManager.readActivities(healthConnectClient, start, end)

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

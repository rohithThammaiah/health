package dev.rohith.health

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

data class HomeState(
    val title: String = "Today",
    val isHealthSDKAvailable: Async<Boolean> = Uninitialized,
    val isHealthSDKPermissionGranted: Async<Boolean> = Uninitialized,
    val healthRecord: Async<HealthRecord> = Uninitialized,
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

    fun getTodayStats() {
        viewModelScope.launch {
            val end = Instant.now()
            val start = end.minus(1, ChronoUnit.DAYS)
            val result = healthKitManager.readStats(healthConnectClient, start, end)

            result.getOrNull()?.let {
                setState {
                    copy(healthRecord = Success(it))
                }
            }
        }
    }

    fun getActivities() {
        viewModelScope.launch {
            val end = Instant.now()
            val start = end.minus(1, ChronoUnit.DAYS)
            val result = healthKitManager.readActivities(healthConnectClient, start, end)

            result?.let {
                setState {
                    copy(activities = Success(it))
                }
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

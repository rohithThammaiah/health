package dev.rohith.health

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.coroutineScope
import com.arkivanov.essenty.lifecycle.Lifecycle
import dev.rohith.health.home.HomeEvents
import dev.rohith.health.home.HomeScreen
import dev.rohith.health.home.HomeState
import dev.rohith.health.home.HomeViewModel
import dev.rohith.health.theme.HealthTheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun App(
    permissionRequester: PermissionRequester
) {
    HealthTheme {
        val viewModel: HomeViewModel = remember {
            HomeViewModel(
                state = HomeState(),
                healthKitManager = getHealthKitManager()
            )
        }
        val state: HomeState by viewModel.state.collectAsState()

        HomeScreen(
            title = state.selectedDate.toString(),
            selectedDate = state.selectedDate,
            onDateSelected = {
                viewModel.emitEvent(HomeEvents.OnDateSelected(it))
            },
            isHealthPermissionEnabled = state.isHealthSDKPermissionGranted,
            allowPermission = {
                permissionRequester.requestPermission()
            },
            healthRecord = state.healthRecord,
            activities = state.activities,
            heatMapData = state.heatMapData
        )
    }
}
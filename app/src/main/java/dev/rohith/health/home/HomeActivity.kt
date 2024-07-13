package dev.rohith.health.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.compose.mavericksActivityViewModel
import dev.rohith.health.data.HealthKitManager.Companion.PERMISSIONS
import dev.rohith.health.ui.theme.HealthTheme
import kotlinx.coroutines.launch

class HomeActivity : ComponentActivity() {

    private val requestPermissionActivityContract =
        PermissionController.createRequestPermissionResultContract()

    private val requestPermissions =
        registerForActivityResult(requestPermissionActivityContract) { granted ->
            if (granted.containsAll(PERMISSIONS)) {
                // Permissions successfully granted
                // PERMISSIONS: Set<string> as of Alpha11
            } else {
                // Lack of required permissions
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthTheme {
                val homeViewModel: HomeViewModel = mavericksActivityViewModel()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(
                        allowPermission = {
                            lifecycleScope.launch {
                                homeViewModel.healthConnectClient?.let {
                                    homeViewModel.healthKitManager.checkPermissionsAndRun(it, requestPermissions)
                                }
                            }
                        })
                }
            }
        }
    }


}

package dev.rohith.health

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.Mavericks
import com.airbnb.mvrx.compose.collectAsState
import dev.rohith.health.HealthKitManager.Companion.PERMISSIONS
import dev.rohith.health.ui.theme.HealthTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    val requestPermissionActivityContract =
        PermissionController.createRequestPermissionResultContract()
    val requestPermissions =
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
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(
                        allowPermission = {
                            lifecycleScope.launch {
                                // healthKitManager.checkPermissionsAndRun(homeViewModel.healthConnectClient, requestPermissions)
                            }
                        })
                }
            }
        }
    }


}

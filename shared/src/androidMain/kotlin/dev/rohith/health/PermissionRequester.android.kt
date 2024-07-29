package dev.rohith.health

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.registerForActivityResult
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.arkivanov.essenty.lifecycle.Lifecycle
import dev.rohith.health.AndroidHealthKitManager.Companion.PERMISSIONS
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AndroidPermissionRequester(private val activity: ComponentActivity) : PermissionRequester {

    private val requestPermissionActivityContract =
        PermissionController.createRequestPermissionResultContract()

    private lateinit var requestPermissions: ActivityResultLauncher<Set<String>>

    override fun register() {
        requestPermissions = activity.registerForActivityResult(requestPermissionActivityContract) { granted ->
            if (granted.containsAll(PERMISSIONS)) {
                // Permissions successfully granted
                // PERMISSIONS: Set<string> as of Alpha11
                Log.e("Permission", "Granted")
            } else {
                // Lack of required permissions
                Log.e("Permission", "Not Granted")
            }
        }
    }

    override fun requestPermission() {
        requestPermissions.launch(PERMISSIONS)
    }
}
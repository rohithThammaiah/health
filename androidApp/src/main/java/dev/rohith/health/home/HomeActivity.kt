package dev.rohith.health.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.rohith.health.AndroidPermissionRequester
import dev.rohith.health.App

class HomeActivity : ComponentActivity() {

    private val permissionRequester = AndroidPermissionRequester(this).apply {
        register()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App(
                permissionRequester = permissionRequester
            )
        }
    }
}

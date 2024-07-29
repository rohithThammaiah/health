package dev.rohith.health

import android.app.Application
import com.airbnb.mvrx.Mavericks

class HealthApp: Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}
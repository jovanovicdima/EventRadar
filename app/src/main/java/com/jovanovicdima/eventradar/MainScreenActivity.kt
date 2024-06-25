package com.jovanovicdima.eventradar

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.jovanovicdima.eventradar.navigation.NavBar
import com.jovanovicdima.eventradar.services.LocationService
import com.jovanovicdima.eventradar.ui.theme.EventRadarTheme

class MainScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            0
        )
        requestBGLocationPermission()
        val serviceIntent = Intent(this, LocationService::class.java)
        startService(serviceIntent)

        setContent {
            EventRadarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Load navbar
                    NavBar()
                }
            }
        }
    }


    private fun requestBGLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ),
            0
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    EventRadarTheme {
        NavBar()
    }
}
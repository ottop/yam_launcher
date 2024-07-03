package eu.ottop.yamlauncher

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.function.Consumer

class WeatherSystem {

    fun getWeather() {
        getTempForLocation("latitude=60.16&longitude=24.93")
    }

    fun getWeatherForCurrentLocation(activity: Activity) {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                println(location)
                locationManager.removeUpdates(this)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        // Check for location permissions
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissions are not granted

            return
        }

        // Request location updates from both providers
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)


        // Try to get the last known location from both providers as a fallback
        val currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)


        if (currentLocation != null) {
            getTempForLocation("latitude=${currentLocation.latitude}&longitude=${currentLocation.longitude}")
        }


    }

    private fun getTempForLocation(location: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = URL("https://api.open-meteo.com/v1/forecast?$location&current=temperature_2m")
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"

                inputStream.bufferedReader().use {
                    val response = it.readText()

                    val jsonObject = JSONObject(response)

                    val currentData = jsonObject.getJSONObject("current")
                    val currentWeather = currentData.getInt("temperature_2m")

                    println("Field1: $currentWeather")
                }
            }}
    }
}
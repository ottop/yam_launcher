package eu.ottop.yamlauncher

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class WeatherSystem {

    private val sharedPreferenceManager = SharedPreferenceManager()

    fun getWeatherForCurrentLocation(activity: Activity) {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                println("Location obtained")
                locationManager.removeUpdates(this)
            }
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
            return
        }


        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)


        val currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)


        if (currentLocation != null) {
            sharedPreferenceManager.setWeatherLocation(activity, "latitude=${currentLocation.latitude}&longitude=${currentLocation.longitude}")
        }

        else {
            Toast.makeText(activity, "Unable to get location", Toast.LENGTH_SHORT).show()
        }


    }

    fun getTemp(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val location = sharedPreferenceManager.getWeatherLocation(context)
            if (location != null) {
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
                }
            }
            else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No weather location set", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}
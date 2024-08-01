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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class WeatherSystem {

    private val sharedPreferenceManager = SharedPreferenceManager()
    private val stringUtils = StringUtils()

    fun setGpsLocation(activity: Activity) {
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
            sharedPreferenceManager.setWeatherLocation(activity, "latitude=${currentLocation.latitude}&longitude=${currentLocation.longitude}", sharedPreferenceManager.getWeatherRegion(activity))
        }

        else {
            Toast.makeText(activity, "Unable to get location", Toast.LENGTH_SHORT).show()
        }

    }

    // Run within Dispatchers.IO from the outside (doesn't refresh properly otherwise)
    fun getSearchedLocations(searchTerm: String?) : MutableList<Map<String, String>> {
        val foundLocations = mutableListOf<Map<String, String>>()

            val url = URL("https://geocoding-api.open-meteo.com/v1/search?name=$searchTerm&count=50&language=en&format=json")
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                try {
                inputStream.bufferedReader().use {
                    val response = it.readText()
                    println("yo")
                    val jsonObject = JSONObject(response)
                    val resultArray = jsonObject.getJSONArray("results")

                    for (i in 0 until resultArray.length()) {
                        val resultObject: JSONObject = resultArray.getJSONObject(i)

                        foundLocations.add(mapOf(
                            "name" to resultObject.getString("name"),
                            "latitude" to resultObject.getDouble("latitude").toString(),
                            "longitude" to resultObject.getDouble("longitude").toString(),
                            "country" to resultObject.optString("country", resultObject.optString("country_code","")),
                            "region" to stringUtils.addEndTextIfNotEmpty(resultObject.optString("admin2", resultObject.optString("admin1",resultObject.optString("admin3",""))), ", ")
                        ))
                    }
                }
            }catch (e: Exception){
                    e.printStackTrace()
            }
        }
        return foundLocations
    }

    suspend fun getTemp(context: Context) : String {

            val location = sharedPreferenceManager.getWeatherLocation(context)
            if (location != null) {
                val url = URL("https://api.open-meteo.com/v1/forecast?$location&current=temperature_2m")
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"

                    inputStream.bufferedReader().use {
                        val response = it.readText()

                        val jsonObject = JSONObject(response)

                        val currentData = jsonObject.getJSONObject("current")

                        return currentData.getInt("temperature_2m").toString()

                    }
                }
            }
            else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No weather location set", Toast.LENGTH_SHORT).show()
                }
            }

        return ""

    }
}
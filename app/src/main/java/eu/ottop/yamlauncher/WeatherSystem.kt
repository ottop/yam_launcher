package eu.ottop.yamlauncher

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class WeatherSystem {
    fun getWeather() {
        CoroutineScope(Dispatchers.IO).launch {
        val url = URL("https://api.open-meteo.com/v1/forecast?latitude=60.16&longitude=24.93&current=temperature_2m")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"

            inputStream.bufferedReader().use {
                val response = it.readText()

                // Parse the JSON response
                val jsonObject = JSONObject(response)

                // Access specific fields or nested objects
                val currentData = jsonObject.getJSONObject("current")
                val currentWeather = currentData.getInt("temperature_2m")

                println("Field1: $currentWeather")
            }
        }}
    }
}
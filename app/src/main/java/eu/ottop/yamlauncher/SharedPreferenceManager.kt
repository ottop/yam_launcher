package eu.ottop.yamlauncher

import android.content.Context
import android.widget.TextView
import androidx.preference.PreferenceManager

class SharedPreferenceManager (context: Context) {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun setShortcut(textView: TextView, packageName: String, profile: Int) {
        val editor = preferences.edit()
        val key = "shortcut${textView.id}"
        editor.putString(key, "$packageName§splitter§$profile§splitter§${textView.text}")
        editor.apply()
    }

    fun getShortcut(textView: TextView): List<String>? {
        val key = "shortcut${textView.id}"
        val value = preferences.getString(key, "e§splitter§e")
        return value?.split("§splitter§")
    }

    fun setAppHidden(packageName: String, profile: Int, hidden: Boolean) {
        val editor = preferences.edit()
        val key = "hidden$packageName-$profile"
        editor.putBoolean(key, hidden)
        editor.apply()
    }

    fun isAppHidden(packageName: String, profile: Int): Boolean {
        val key = "hidden$packageName-$profile"
        return preferences.getBoolean(key, false) // Default to false (visible)
    }

    fun setAppVisible(packageName: String, profile: Int) {
        val editor = preferences.edit()
        val key = "hidden$packageName-$profile"
        editor.remove(key)
        editor.apply()
    }

    fun setAppName(packageName: String, profile: Int, newName: String) {
        val editor = preferences.edit()
        val key = "name$packageName-$profile"
        editor.putString(key, newName)
        editor.apply()
    }

    fun getAppName(packageName: String, profile: Int, appName: CharSequence): CharSequence? {
        val key = "name$packageName-$profile"
        return preferences.getString(key, appName.toString())
    }

    fun resetAppName(packageName: String, profile: Int) {
        val editor = preferences.edit()
        val key = "name$packageName-$profile"
        editor.remove(key)
        editor.apply()
    }

    fun setWeatherLocation(location: String, region: String?) {
        val editor = preferences.edit()
        val key = "location"
        val regionKey = "location_region"
        editor.putString(key, location)
        editor.putString(regionKey, region)
        editor.apply()
    }

    fun getWeatherLocation(): String? {
        val key = "location"
        return preferences.getString(key, "")
    }

    fun getWeatherRegion(): String? {
        val key = "location_region"
        return preferences.getString(key, "")
    }

    fun setGestures(direction: String, appName: String?) {
        val editor = preferences.edit()
        val nameKey = "${direction}SwipeApp"
        editor.putString(nameKey, appName)
        editor.apply()
    }

    fun getGestureName(direction: String) : String? {
        val key = "${direction}SwipeApp"
        val name = preferences.getString(key, "")?.split("§splitter§")
        return name?.get(0)
    }

    fun getGestureInfo(direction: String) : List<String>? {
        return preferences.getString("${direction}SwipeApp", "")?.split("§splitter§")
    }

}
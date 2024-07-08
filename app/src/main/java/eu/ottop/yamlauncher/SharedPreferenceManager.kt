package eu.ottop.yamlauncher

import android.content.Context
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SharedPreferenceManager {

    fun setShortcut(cont: Context, textView: TextView, packageName: String, profile: Int) {
        val editor = cont.getSharedPreferences("shortcuts", AppCompatActivity.MODE_PRIVATE).edit()
        val key = textView.id.toString()
        editor.putString(key, "$packageName-$profile-${textView.text}")
        editor.apply()
    }

    fun getShortcut(cont: Context, textView: TextView): List<String>? {
        val sharedPref = cont.getSharedPreferences("shortcuts", AppCompatActivity.MODE_PRIVATE)
        val key = textView.id.toString()
        val value = sharedPref.getString(key, "e-e")
        return value?.split("-")
    }

    fun setAppHidden(cont: Context, packageName: String, profile: Int, hidden: Boolean) {
        val editor = cont.getSharedPreferences("hidden_apps", AppCompatActivity.MODE_PRIVATE).edit()
        val key = "$packageName-$profile"
        editor.putBoolean(key, hidden)
        editor.apply()
    }

    fun isAppHidden(cont: Context, packageName: String, profile: Int): Boolean {
        val sharedPref = cont.getSharedPreferences("hidden_apps", AppCompatActivity.MODE_PRIVATE)
        val key = "$packageName-$profile"
        return sharedPref.getBoolean(key, false) // Default to false (visible)
    }

    fun setAppVisible(cont: Context, packageName: String, profile: Int) {
        val editor = cont.getSharedPreferences("hidden_apps", AppCompatActivity.MODE_PRIVATE).edit()
        val key = "$packageName-$profile"
        editor.remove(key)
        editor.apply()
    }

    fun setAppName(cont: Context, packageName: String, profile: Int, newName: String) {
        val editor = cont.getSharedPreferences("renamed_apps", AppCompatActivity.MODE_PRIVATE).edit()
        val key = "$packageName-$profile"
        editor.putString(key, newName)
        editor.apply()
    }

    fun getAppName(cont: Context, packageName: String, profile: Int, appName: CharSequence): CharSequence? {
        val sharedPreferences = cont.getSharedPreferences("renamed_apps", AppCompatActivity.MODE_PRIVATE)
        val key = "$packageName-$profile"
        return sharedPreferences.getString(key, appName.toString())
    }

    fun resetAppName(cont: Context, packageName: String, profile: Int) {
        val editor = cont.getSharedPreferences("renamed_apps", AppCompatActivity.MODE_PRIVATE).edit()
        val key = "$packageName-$profile"
        editor.remove(key)
        editor.apply()
    }

    fun setWeatherLocation(cont: Context, location: String) {
        val editor = cont.getSharedPreferences("weather_location", AppCompatActivity.MODE_PRIVATE).edit()
        val key = "location"
        editor.putString(key, location)
        editor.apply()
    }

    fun getWeatherLocation(cont: Context) : String? {
        val sharedPreferences = cont.getSharedPreferences("weather_location", AppCompatActivity.MODE_PRIVATE)
        val key = "location"
        return sharedPreferences.getString(key, null)
    }

    fun setClockAlignment(cont: Context, alignment: Int) {
        val editor = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE).edit()
        val key = "clock_alignment"
        editor.putInt(key, alignment)
        editor.apply()
    }

    fun getClockAlignment(cont: Context) : Int {
        val sharedPreferences = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE)
        val key = "clock_alignment"
        return sharedPreferences.getInt(key, 0)
    }

    fun setHomeAppAlignment(cont: Context, alignment: Int) {
        val editor = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE).edit()
        val key = "home_app_alignment"
        editor.putInt(key, alignment)
        editor.apply()
    }

    fun getHomeAppAlignment(cont: Context) : Int {
        val sharedPreferences = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE)
        val key = "home_app_alignment"
        return sharedPreferences.getInt(key, 0)
    }

    fun setAppMenuAlignment(cont: Context, alignment: Int) {
        val editor = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE).edit()
        val key = "app_menu_alignment"
        editor.putInt(key, alignment)
        editor.apply()
    }

    fun getAppMenuAlignment(cont: Context) : Int {
        val sharedPreferences = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE)
        val key = "app_menu_alignment"
        return sharedPreferences.getInt(key, 0)
    }

    fun setSearchAlignment(cont: Context, alignment: Int) {
        val editor = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE).edit()
        val key = "search_alignment"
        editor.putInt(key, alignment)
        editor.apply()
    }

    fun getSearchAlignment(cont: Context) : Int {
        val sharedPreferences = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE)
        val key = "search_alignment"
        return sharedPreferences.getInt(key, 0)
    }

    fun setClockSize(cont: Context, alignment: Int) {
        val editor = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE).edit()
        val key = "clock_size"
        editor.putInt(key, alignment)
        editor.apply()
    }

    fun getClockSize(cont: Context) : Int {
        val sharedPreferences = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE)
        val key = "clock_size"
        return sharedPreferences.getInt(key, 2)
    }

    fun setDateSize(cont: Context, alignment: Int) {
        val editor = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE).edit()
        val key = "date_size"
        editor.putInt(key, alignment)
        editor.apply()
    }

    fun getDateSize(cont: Context) : Int {
        val sharedPreferences = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE)
        val key = "date_size"
        return sharedPreferences.getInt(key, 2)
    }

    fun setShortcutSize(cont: Context, alignment: Int) {
        val editor = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE).edit()
        val key = "shortcut_size"
        editor.putInt(key, alignment)
        editor.apply()
    }

    fun getShortcutSize(cont: Context) : Int {
        val sharedPreferences = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE)
        val key = "shortcut_size"
        return sharedPreferences.getInt(key, 2)
    }

    fun setAppSize(cont: Context, alignment: Int) {
        val editor = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE).edit()
        val key = "app_size"
        editor.putInt(key, alignment)
        editor.apply()
    }

    fun getAppSize(cont: Context) : Int {
        val sharedPreferences = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE)
        val key = "app_size"
        return sharedPreferences.getInt(key, 2)
    }

    fun setSearchSize(cont: Context, alignment: Int) {
        val editor = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE).edit()
        val key = "search_size"
        editor.putInt(key, alignment)
        editor.apply()
    }

    fun getSearchSize(cont: Context) : Int {
        val sharedPreferences = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE)
        val key = "search_size"
        return sharedPreferences.getInt(key, 2)
    }

    fun setCameraEnabled(cont: Context, isEnabled: Boolean) {
        val editor = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE).edit()
        val key = "camera_enabled"
        editor.putBoolean(key, isEnabled)
        editor.apply()
    }

    fun getCameraEnabled(cont: Context) : Boolean {
        val sharedPreferences = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE)
        val key = "camera_enabled"
        return sharedPreferences.getBoolean(key, true)
    }

    fun setContactsEnabled(cont: Context, isEnabled: Boolean) {
        val editor = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE).edit()
        val key = "contacts_enabled"
        editor.putBoolean(key, isEnabled)
        editor.apply()
    }

    fun getContactsEnabled(cont: Context) : Boolean {
        val sharedPreferences = cont.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE)
        val key = "contacts_enabled"
        return sharedPreferences.getBoolean(key, true)
    }
}
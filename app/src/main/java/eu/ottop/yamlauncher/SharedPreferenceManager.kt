package eu.ottop.yamlauncher

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

class SharedPreferenceManager {

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

}
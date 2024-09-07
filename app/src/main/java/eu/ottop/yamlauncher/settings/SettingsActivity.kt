package eu.ottop.yamlauncher.settings

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import eu.ottop.yamlauncher.R
import eu.ottop.yamlauncher.databinding.ActivitySettingsBinding
import org.json.JSONObject
import java.io.IOException

class SettingsActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var performBackup: ActivityResultLauncher<Intent>
    private lateinit var performRestore: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = PreferenceManager.getDefaultSharedPreferences(this@SettingsActivity)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Launcher Settings"
        supportActionBar?.setDisplayShowTitleEnabled(true)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settingsLayout, SettingsFragment())
            .commit()

        supportFragmentManager.addOnBackStackChangedListener {
            updateActionBarTitle()
        }

        performBackup = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    saveSharedPreferencesToFile(uri)
                }
            }
        }

        performRestore = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    restoreSharedPreferencesFromFile(uri)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
        return true
    }

    private fun updateActionBarTitle() {
        val fragment = supportFragmentManager.findFragmentById(R.id.settingsLayout)
        if (fragment is TitleProvider) {
            supportActionBar?.title = fragment.getTitle()
        }
    }

    fun createBackup() {
        val createFileIntent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, "yamlauncher_backup.json")
        }
        performBackup.launch(createFileIntent)
    }

    private fun saveSharedPreferencesToFile(uri: Uri) {
        val sharedPreferences = preferences
        val allEntries = sharedPreferences.all

        val backupData = JSONObject().apply {
            put("app_id", application.packageName)
            val data = JSONObject()
            for ((key, value) in allEntries) {
                val entry = JSONObject().apply {
                    when (value) {
                        is String -> put("value", value).put("type", "String")
                        is Int -> put("value", value).put("type", "Int")
                        is Boolean -> put("value", value).put("type", "Boolean")
                        is Long -> put("value", value).put("type", "Long")
                        is Float -> put("value", value).put("type", "Float")
                    }
                }
                data.put(key, entry)
            }
            put("data", data)
        }

        val sharedPreferencesText = backupData.toString(4)

        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(sharedPreferencesText.toByteArray())
            }
            Toast.makeText(this, "Backup successful :)", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Backup failed :(", Toast.LENGTH_SHORT).show()
        }
    }

    fun restoreBackup() {
        val openFileIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }
        performRestore.launch(openFileIntent)
    }

    private fun restoreSharedPreferencesFromFile(uri: Uri) {
        val jsonData = readJsonFile(uri)
        if (jsonData != null) {
            try {
                val backupData = JSONObject(jsonData)
                if (backupData.getString("app_id") != application.packageName) {
                    throw IllegalArgumentException("Not a YAM Launcher backup")
                }
                val data = backupData.getJSONObject("data")

                val editor = preferences.edit()

                val keys = data.keys()

                while (keys.hasNext()){
                    val key = keys.next()
                    val entry = data.getJSONObject(key)
                    val type = entry.getString("type")

                    when (type) {
                        "String" -> editor.putString(key, entry.getString("value"))
                        "Int" -> editor.putInt(key, entry.getInt("value"))
                        "Boolean" -> editor.putBoolean(key, entry.getBoolean("value"))
                        "Long" -> editor.putLong(key, entry.getLong("value"))
                        "Float" -> editor.putFloat(key, entry.getDouble("value").toFloat())
                    }
                }

                editor.apply()

                Toast.makeText(this, "Restore successful :)", Toast.LENGTH_SHORT).show()
            } catch(e: IllegalArgumentException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Restore failed :( (corrupt file?)", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Failed to read file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readJsonFile(uri: Uri): String? {
        return try {
            contentResolver.openInputStream(uri)?.bufferedReader().use { reader ->
                reader?.readText()
            }
        } catch (e: Exception) {
            null
        }
    }

}
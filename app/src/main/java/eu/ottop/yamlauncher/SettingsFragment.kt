package eu.ottop.yamlauncher

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val gpsLocationPref: SwitchPreference? = findPreference("gps_location")
        val manualLocationPref: Preference? = findPreference("manual_location")

        if (gpsLocationPref != null && manualLocationPref != null) {
            // Initial setup
            manualLocationPref.isEnabled = !gpsLocationPref.isChecked

            // Set up a listener to update the enabled state of manualLocationPref
            gpsLocationPref.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val isGpsEnabled = newValue as Boolean
                    manualLocationPref.isEnabled = !isGpsEnabled
                    true // Returning true means the change is persisted
                }
        }

        findPreference<Preference?>("hidden_apps")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.settings_layout, HiddenAppsFragment())
                    ?.addToBackStack(null)
                    ?.commit()
                true }
    }
}
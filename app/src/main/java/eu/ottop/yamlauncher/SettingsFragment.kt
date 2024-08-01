package eu.ottop.yamlauncher

import android.os.Bundle
import androidx.fragment.app.setFragmentResultListener
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference

class SettingsFragment : PreferenceFragmentCompat() {

    private var manualLocationPref: Preference? = null
    private val sharedPreferenceManager = SharedPreferenceManager()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val weatherSystem = WeatherSystem()

        val gpsLocationPref: SwitchPreference? = findPreference("gps_location")
        manualLocationPref = findPreference("manual_location")

        manualLocationPref?.summary = sharedPreferenceManager.getWeatherRegion(requireContext())

        if (gpsLocationPref != null && manualLocationPref != null) {
            // Initial setup
            manualLocationPref?.isEnabled = !gpsLocationPref.isChecked

            // Set up a listener to update the enabled state of manualLocationPref
            gpsLocationPref.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val isGpsEnabled = newValue as Boolean
                    if (isGpsEnabled) {
                        weatherSystem.setGpsLocation(requireActivity())
                    }
                    manualLocationPref?.isEnabled = !isGpsEnabled
                    true // Returning true means the change is persisted
                }

            manualLocationPref?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    requireActivity().supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.settings_layout, LocationFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
        }

        findPreference<Preference?>("hidden_apps")?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings_layout, HiddenAppsFragment())
                    .addToBackStack(null)
                    .commit()
                true }
    }

    override fun onResume() {
        super.onResume()
        manualLocationPref?.summary = sharedPreferenceManager.getWeatherRegion(requireContext())
    }
}
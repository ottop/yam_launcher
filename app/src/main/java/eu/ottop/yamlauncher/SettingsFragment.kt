package eu.ottop.yamlauncher

import android.os.Bundle
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference

class SettingsFragment : PreferenceFragmentCompat() {

    private var manualLocationPref: Preference? = null
    private val sharedPreferenceManager = SharedPreferenceManager()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val weatherSystem = WeatherSystem()

        val gpsLocationPref: SwitchPreference? = findPreference("gps_location")
        manualLocationPref = findPreference("manual_location")
        val leftSwipePref = findPreference<Preference?>("leftSwipeApp")
        val rightSwipePref = findPreference<Preference?>("rightSwipeApp")
        val aboutPref = findPreference<Preference?>("about_page")

        manualLocationPref?.summary = sharedPreferenceManager.getWeatherRegion(requireContext())
        leftSwipePref?.summary = sharedPreferenceManager.getGestureName(requireContext(), "left")
        rightSwipePref?.summary = sharedPreferenceManager.getGestureName(requireContext(), "right")

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

        leftSwipePref?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings_layout, GestureAppsFragment())
                    .addToBackStack(null)
                    .commit()
                setFragmentResultListener("request_key") { _, bundle ->
                    clearFragmentResultListener("request_key")
                    val result = bundle.getString("gesture_app")
                    val appDetails = result?.split("§splitter§")
                    if (leftSwipePref != null && result != null) {
                        setPreference("leftSwipeApp", result)
                    }
                    sharedPreferenceManager.setGestures(
                        requireContext(), "left",
                        appDetails?.get(0)
                    )
                    val appName = appDetails?.get(0)
                    leftSwipePref?.summary = appName
                }
                true }

        rightSwipePref?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings_layout, GestureAppsFragment())
                    .addToBackStack(null)
                    .commit()
                setFragmentResultListener("request_key") { _, bundle ->
                    clearFragmentResultListener("request_key")
                    val result = bundle.getString("gesture_app")
                    val appDetails = result?.split("§splitter§")
                    if (rightSwipePref != null && result != null) {
                        setPreference("rightSwipeApp", result)
                    }
                    sharedPreferenceManager.setGestures(
                        requireContext(), "right",
                        appDetails?.get(0)
                    )
                    val appName = appDetails?.get(0)
                    rightSwipePref?.summary = appName
                }
                true }

        aboutPref?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings_layout, AboutFragment())
                    .addToBackStack(null)
                    .commit()
                true }
    }

    override fun onResume() {
        super.onResume()
        manualLocationPref?.summary = sharedPreferenceManager.getWeatherRegion(requireContext())
    }

    private fun setPreference(key: String, value: String) {
        // Get the SharedPreferences instance
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        // Edit the SharedPreferences to update the value
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }
}
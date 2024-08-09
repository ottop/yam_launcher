package eu.ottop.yamlauncher

import android.os.Bundle
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference

class SettingsFragment : PreferenceFragmentCompat() {

    private var manualLocationPref: Preference? = null
    private lateinit var sharedPreferenceManager: SharedPreferenceManager

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        sharedPreferenceManager = SharedPreferenceManager(requireContext())

        val gpsLocationPref: SwitchPreference? = findPreference("gps_location")
        manualLocationPref = findPreference("manual_location")
        val leftSwipePref = findPreference<Preference?>("leftSwipeApp")
        val rightSwipePref = findPreference<Preference?>("rightSwipeApp")
        val aboutPref = findPreference<Preference?>("about_page")
        val hiddenPref = findPreference<Preference?>("hidden_apps")

        manualLocationPref?.summary = sharedPreferenceManager.getWeatherRegion()
        leftSwipePref?.summary = sharedPreferenceManager.getGestureName("left")
        rightSwipePref?.summary = sharedPreferenceManager.getGestureName("right")

        if (gpsLocationPref != null && manualLocationPref != null) {
            // Initial setup
            manualLocationPref?.isEnabled = !gpsLocationPref.isChecked

            // Set up a listener to update the enabled state of manualLocationPref
            gpsLocationPref.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val isGpsEnabled = newValue as Boolean
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

        hiddenPref?.onPreferenceClickListener =
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

                    sharedPreferenceManager.setGestures(
                        "left", result
                    )

                    val appName = sharedPreferenceManager.getGestureName("left")
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

                    sharedPreferenceManager.setGestures(
                        "right", result
                    )

                    val appName = sharedPreferenceManager.getGestureName("right")
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
        manualLocationPref?.summary = sharedPreferenceManager.getWeatherRegion()
    }
}
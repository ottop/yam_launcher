package eu.ottop.yamlauncher.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import eu.ottop.yamlauncher.R

class HomeSettingsFragment : PreferenceFragmentCompat() {

    private lateinit var sharedPreferenceManager: SharedPreferenceManager

    private var manualLocationPref: Preference? = null
    private var leftSwipePref: Preference? = null
    private var rightSwipePref: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.home_preferences, rootKey)

        sharedPreferenceManager = SharedPreferenceManager(requireContext())

        val gpsLocationPref = findPreference<SwitchPreference?>("gpsLocation")
        manualLocationPref = findPreference("manualLocation")
        leftSwipePref = findPreference("leftSwipeApp")
        rightSwipePref = findPreference("rightSwipeApp")

        // Only enable manual location when gps location is disabled
        if (gpsLocationPref != null && manualLocationPref != null) {
            manualLocationPref?.isEnabled = !gpsLocationPref.isChecked

            gpsLocationPref.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val isGpsEnabled = newValue as Boolean
                    manualLocationPref?.isEnabled = !isGpsEnabled
                    true
                }

            manualLocationPref?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    requireActivity().supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.settingsLayout, LocationFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
        }

        leftSwipePref?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settingsLayout, GestureAppsFragment("left"))
                    .addToBackStack(null)
                    .commit()
                true }

        rightSwipePref?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settingsLayout, GestureAppsFragment("right"))
                    .addToBackStack(null)
                    .commit()
                true }
    }

    override fun onResume() {
        super.onResume()
        manualLocationPref?.summary = sharedPreferenceManager.getWeatherRegion()

        leftSwipePref?.summary = sharedPreferenceManager.getGestureName("left")

        rightSwipePref?.summary = sharedPreferenceManager.getGestureName("right")
    }
}
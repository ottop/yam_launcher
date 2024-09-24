package eu.ottop.yamlauncher.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import eu.ottop.yamlauncher.R
import eu.ottop.yamlauncher.utils.UIUtils

class HomeSettingsFragment : PreferenceFragmentCompat(), TitleProvider {

    private lateinit var sharedPreferenceManager: SharedPreferenceManager

    private var manualLocationPref: Preference? = null
    private var leftSwipePref: Preference? = null
    private var rightSwipePref: Preference? = null
    private var clockApp: Preference? = null
    private var dateApp: Preference? = null

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.home_preferences, rootKey)
            val uiUtils = UIUtils(requireContext())

            sharedPreferenceManager = SharedPreferenceManager(requireContext())

            clockApp = findPreference("clockSwipeApp")
            dateApp = findPreference("dateSwipeApp")

            manualLocationPref = findPreference("manualLocation")
            leftSwipePref = findPreference("leftSwipeApp")
            rightSwipePref = findPreference("rightSwipeApp")

            manualLocationPref?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    uiUtils.switchFragment(requireActivity(), LocationFragment())
                    true
                }

            leftSwipePref?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    uiUtils.switchFragment(requireActivity(), GestureAppsFragment("left"))
                    true }

            rightSwipePref?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    uiUtils.switchFragment(requireActivity(), GestureAppsFragment("right"))
                    true }

            clockApp?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    uiUtils.switchFragment(requireActivity(), GestureAppsFragment("clock"))
                    true }

            dateApp?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    uiUtils.switchFragment(requireActivity(), GestureAppsFragment("date"))
                    true }
    }

    override fun onResume() {
        super.onResume()
        clockApp?.summary = sharedPreferenceManager.getGestureName("clock")

        dateApp?.summary = sharedPreferenceManager.getGestureName("date")

        manualLocationPref?.summary = sharedPreferenceManager.getWeatherRegion()

        leftSwipePref?.summary = sharedPreferenceManager.getGestureName("left")

        rightSwipePref?.summary = sharedPreferenceManager.getGestureName("right")
    }

    override fun getTitle(): String {
        return "Home Screen Settings"
    }
}
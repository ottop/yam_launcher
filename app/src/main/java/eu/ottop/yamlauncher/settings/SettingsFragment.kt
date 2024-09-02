package eu.ottop.yamlauncher.settings

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import eu.ottop.yamlauncher.R
import eu.ottop.yamlauncher.utils.UIUtils

class SettingsFragment : PreferenceFragmentCompat(), TitleProvider {

    private lateinit var sharedPreferenceManager: SharedPreferenceManager

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val uiUtils = UIUtils(requireContext())

        sharedPreferenceManager = SharedPreferenceManager(requireContext())

        val homePref = findPreference<Preference>("defaultHome")

        val uiSettings = findPreference<Preference>("uiSettings")
        val homeSettings = findPreference<Preference>("homeSettings")
        val appMenuSettings = findPreference<Preference>("appMenuSettings")

        val hiddenPref = findPreference<Preference>("hiddenApps")
        val aboutPref = findPreference<Preference>("aboutPage")
        val resetPref = findPreference<Preference>("resetAll")

        homePref?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                val intent = Intent(Settings.ACTION_HOME_SETTINGS)
                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Unable to launch settings", Toast.LENGTH_SHORT).show()
                }
                true }

        uiSettings?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                uiUtils.switchFragment(requireActivity(), UISettingsFragment())
                true }

        homeSettings?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                uiUtils.switchFragment(requireActivity(), HomeSettingsFragment())
                true }

        appMenuSettings?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                uiUtils.switchFragment(requireActivity(), AppMenuSettingsFragment())
                true }

        hiddenPref?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                uiUtils.switchFragment(requireActivity(), HiddenAppsFragment())
                true }

        aboutPref?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                uiUtils.switchFragment(requireActivity(), AboutFragment())
                true }

        resetPref?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                sharedPreferenceManager.resetAllPreferences(requireActivity())
                true }
    }

    override fun getTitle(): String {
        return "Launcher Settings"
    }



}
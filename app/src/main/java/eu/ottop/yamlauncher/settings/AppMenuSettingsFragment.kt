package eu.ottop.yamlauncher.settings

import android.Manifest
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import eu.ottop.yamlauncher.R
import eu.ottop.yamlauncher.utils.PermissionUtils

class AppMenuSettingsFragment : PreferenceFragmentCompat(), TitleProvider {
    private val permissionUtils = PermissionUtils()
    private var contactPref: SwitchPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_menu_preferences, rootKey)
        contactPref = findPreference("contactsEnabled")
        contactPref?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->

            if (newValue as Boolean && !permissionUtils.hasContactsPermission(requireContext(), Manifest.permission.READ_CONTACTS)) {
                    (requireActivity() as SettingsActivity).requestContactsPermission()
                    return@OnPreferenceChangeListener false
                } else {
                    return@OnPreferenceChangeListener true
                }
        }
    }

    override fun getTitle(): String {
        return "App Menu Settings"
    }

    fun setContactPreference(isEnabled: Boolean) {
        contactPref?.isChecked = isEnabled
    }
}
package eu.ottop.yamlauncher.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import eu.ottop.yamlauncher.R

class AppMenuSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_menu_preferences, rootKey)
    }
}
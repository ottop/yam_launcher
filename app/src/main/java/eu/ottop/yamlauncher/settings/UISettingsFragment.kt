package eu.ottop.yamlauncher.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import eu.ottop.yamlauncher.R

class UISettingsFragment : PreferenceFragmentCompat(), TitleProvider {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.ui_preferences, rootKey)
    }

    override fun getTitle(): String {
        return "General UI Settings"
    }
}
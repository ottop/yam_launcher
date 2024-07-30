package eu.ottop.yamlauncher

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder


class ButtonPreference(context: Context?, attrs: AttributeSet?) :
    Preference(context!!, attrs) {
    init {
        widgetLayoutResource = R.layout.location_button;
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        val view = holder.itemView
        view.setOnClickListener {
            when (this.key) {
                "manual_location" -> {
                    println("Location pressed")
                }

                "hidden_apps" -> {
                    println("Hidden apps pressed")
                }
            }
        }
    }
}
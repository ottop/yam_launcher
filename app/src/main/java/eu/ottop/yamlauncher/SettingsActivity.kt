package eu.ottop.yamlauncher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import eu.ottop.yamlauncher.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val sharedPreferenceManager = SharedPreferenceManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clockAlignment.setSelection(sharedPreferenceManager.getClockAlignment(this@SettingsActivity))

        binding.clockAlignment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Get the selected item
                sharedPreferenceManager.setClockAlignment(this@SettingsActivity, position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        binding.homeAppAlignment.setSelection(sharedPreferenceManager.getHomeAppAlignment(this@SettingsActivity))

        binding.homeAppAlignment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Get the selected item
                sharedPreferenceManager.setHomeAppAlignment(this@SettingsActivity, position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        binding.appAlignment.setSelection(sharedPreferenceManager.getAppMenuAlignment(this@SettingsActivity))

        binding.appAlignment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Get the selected item
                sharedPreferenceManager.setAppMenuAlignment(this@SettingsActivity, position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        binding.appSize.setSelection(sharedPreferenceManager.getAppSize(this@SettingsActivity))

        binding.appSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Get the selected item
                sharedPreferenceManager.setAppSize(this@SettingsActivity, position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }
}
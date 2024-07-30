package eu.ottop.yamlauncher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import eu.ottop.yamlauncher.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivitySettingsBinding.inflate(layoutInflater)
            setContentView(binding.root)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_layout, SettingsFragment())
                .commit()
        }


    /*
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

        binding.searchAlignment.setSelection(sharedPreferenceManager.getSearchAlignment(this@SettingsActivity))

        binding.searchAlignment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Get the selected item
                sharedPreferenceManager.setSearchAlignment(this@SettingsActivity, position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        binding.clockSize.setSelection(sharedPreferenceManager.getClockSize(this@SettingsActivity))

        binding.clockSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Get the selected item
                sharedPreferenceManager.setClockSize(this@SettingsActivity, position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        binding.dateSize.setSelection(sharedPreferenceManager.getDateSize(this@SettingsActivity))

        binding.dateSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Get the selected item
                sharedPreferenceManager.setDateSize(this@SettingsActivity, position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        binding.shortcutSize.setSelection(sharedPreferenceManager.getShortcutSize(this@SettingsActivity))

        binding.shortcutSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Get the selected item
                sharedPreferenceManager.setShortcutSize(this@SettingsActivity, position)
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

        binding.searchSize.setSelection(sharedPreferenceManager.getSearchSize(this@SettingsActivity))

        binding.searchSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Get the selected item
                sharedPreferenceManager.setSearchSize(this@SettingsActivity, position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        binding.camera.isChecked = sharedPreferenceManager.getCameraEnabled(this@SettingsActivity)

        binding.camera.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferenceManager.setCameraEnabled(this@SettingsActivity, isChecked)
        }

        binding.contacts.isChecked = sharedPreferenceManager.getContactsEnabled(this@SettingsActivity)

        binding.contacts.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferenceManager.setContactsEnabled(this@SettingsActivity, isChecked)
        }

    }

     */
}
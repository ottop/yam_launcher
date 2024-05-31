package eu.ottop.yamlauncher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import eu.ottop.yamlauncher.databinding.ActivityMainBinding
import eu.ottop.yamlauncher.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.settings_bg)
    }
}
package eu.ottop.yamlauncher.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import eu.ottop.yamlauncher.R
import eu.ottop.yamlauncher.utils.StringUtils

class AboutFragment : Fragment(), TitleProvider {

    private val stringUtils = StringUtils()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up about page links
        stringUtils.setLink(requireActivity().findViewById(R.id.creditText), getString(R.string.my_website_link))
        stringUtils.setLink(requireActivity().findViewById(R.id.codebergLink), getString(R.string.codeberg_link))
        stringUtils.setLink(requireActivity().findViewById(R.id.githubLink), getString(R.string.github_link))
        stringUtils.setLink(requireActivity().findViewById(R.id.stripeLink), getString(R.string.stripe_link))
        stringUtils.setLink(requireActivity().findViewById(R.id.liberaLink), getString(R.string.libera_link))
        stringUtils.setLink(requireActivity().findViewById(R.id.weatherLink), getString(R.string.weather_link))

        requireActivity().findViewById<ImageView>(R.id.iconView).setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${requireContext().packageName}")
            }
            startActivity(intent)
        }
    }

    override fun getTitle(): String {
        return "About YAM Launcher"
    }
}
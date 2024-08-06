package eu.ottop.yamlauncher

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class AboutFragment : Fragment() {

    private val stringUtils = StringUtils()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stringUtils.setLink(requireActivity().findViewById(R.id.creditText), getString(R.string.my_website_link))
        stringUtils.setLink(requireActivity().findViewById(R.id.codebergLink), getString(R.string.codeberg_link))
        stringUtils.setLink(requireActivity().findViewById(R.id.githubLink), getString(R.string.github_link))
        stringUtils.setLink(requireActivity().findViewById(R.id.stripeLink), getString(R.string.stripe_link))
        stringUtils.setLink(requireActivity().findViewById(R.id.liberaLink), getString(R.string.libera_link))
        stringUtils.setLink(requireActivity().findViewById(R.id.weatherLink), getString(R.string.weather_link))
    }


}
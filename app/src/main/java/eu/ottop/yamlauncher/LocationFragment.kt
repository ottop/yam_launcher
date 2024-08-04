package eu.ottop.yamlauncher

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.setFragmentResult
import androidx.preference.Preference
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class LocationFragment : Fragment(), LocationListAdapter.OnItemClickListener {

    private var adapter: LocationListAdapter? = null
    private val weatherSystem = WeatherSystem()
    private val sharedPreferenceManager = SharedPreferenceManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchView = view.findViewById<EditText>(R.id.locationSearch)

        var locationList = mutableListOf<Map<String, String>>()

        CoroutineScope(Dispatchers.IO).launch {
            locationList = weatherSystem.getSearchedLocations(
                searchView.text.toString()
            )
        }

        adapter = LocationListAdapter(requireContext(), locationList, this)
        val recyclerView = view.findViewById<RecyclerView>(R.id.locationrecycler)
        val appMenuEdgeFactory = AppMenuEdgeFactory(requireActivity())

        recyclerView.edgeEffectFactory = appMenuEdgeFactory
        recyclerView.adapter = adapter

        recyclerView.scrollToPosition(0)



        recyclerView.addOnLayoutChangeListener { _, _, top, _, bottom, _, oldTop, _, oldBottom ->

            if (bottom - top > oldBottom - oldTop) {
                searchView.clearFocus()
            }
        }

        searchView.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                println(searchView.text.toString())
                CoroutineScope(Dispatchers.IO).launch {
                    val locations = weatherSystem.getSearchedLocations(
                        searchView.text.toString()
                    )
                    withContext(Dispatchers.Main) {
                        adapter?.updateApps(locations)
                    }
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun showConfirmationDialog(appName: String?, latitude: String?, longitude: String?) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Confirmation")
            setMessage("Are you sure you want to select $appName?")
            setPositiveButton("Yes") { _, _ ->
                // Perform action on confirmation
                performConfirmedAction(appName, latitude, longitude)
            }
            setNegativeButton("Cancel") { _, _ ->

            }

        }.create().show()
    }

    private fun performConfirmedAction(appName: String?, latitude: String?, longitude: String?) {
        sharedPreferenceManager.setWeatherLocation(requireContext(), "latitude=${latitude}&longitude=${longitude}", appName)
        requireActivity().supportFragmentManager.popBackStack()
    }


    override fun onItemClick(name: String?, latitude: String?, longitude: String?) {
        showConfirmationDialog(name, latitude, longitude)
    }



}
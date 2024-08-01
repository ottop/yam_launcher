package eu.ottop.yamlauncher

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.LauncherActivityInfo
import android.os.Bundle
import android.os.UserHandle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HiddenAppsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HiddenAppsFragment : Fragment(), HiddenAppsAdapter.OnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val appUtils = AppUtils()
    private val sharedPreferenceManager = SharedPreferenceManager()
    private var adapter: HiddenAppsAdapter? = null
    private var stringUtils = StringUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hidden_apps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         adapter = HiddenAppsAdapter(requireContext(), appUtils.getHiddenApps(activity as Activity).toMutableList(), this)
        val recyclerView = view.findViewById<RecyclerView>(R.id.hidden_app_recycler)
        val appMenuEdgeFactory = AppMenuEdgeFactory(requireActivity())

        recyclerView.edgeEffectFactory = appMenuEdgeFactory
        recyclerView.adapter = adapter

        recyclerView.scrollToPosition(0)

        val searchView = view.findViewById<EditText>(R.id.hiddenAppSearch)

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

            }

            override fun afterTextChanged(s: Editable?) {

                filterItems(searchView.text.toString())

            }
        })
    }

    private fun filterItems(query: String?) {

        val cleanQuery = stringUtils.cleanString(query)
        val newFilteredApps = mutableListOf<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>()
        val updatedApps = appUtils.getHiddenApps(requireActivity())

        getFilteredApps(cleanQuery, newFilteredApps, updatedApps)

        applySearch(newFilteredApps)

    }

    private fun getFilteredApps(cleanQuery: String?, newFilteredApps: MutableList<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>, updatedApps: List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>) {
        if (cleanQuery.isNullOrEmpty()) {
            newFilteredApps.addAll(updatedApps)
        } else {
            updatedApps.forEach {
                val cleanItemText = stringUtils.cleanString(sharedPreferenceManager.getAppName(requireActivity(), it.first.applicationInfo.packageName, it.second.second, requireActivity().packageManager.getApplicationLabel(it.first.applicationInfo)).toString())
                if (cleanItemText != null) {
                    if (cleanItemText.contains(cleanQuery, ignoreCase = true)) {
                        newFilteredApps.add(it)
                    }
                }
            }
        }
    }

    private fun applySearch(newFilteredApps: MutableList<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>) {
        adapter?.updateApps(newFilteredApps)
    }

    private fun showConfirmationDialog(appInfo: LauncherActivityInfo, appName: String, profile: Int) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Confirmation")
            setMessage("Are you sure you want to unhide $appName?")
            setPositiveButton("Yes") { _, _ ->
                // Perform action on confirmation
                performConfirmedAction(appInfo, appName, profile)
            }
            setNegativeButton("Cancel") { _, _ ->
                // Handle cancellation
                handleCancellation()
            }
            setOnCancelListener {
                // Handle dialog cancel
                handleCancellation()
            }
        }.create().show()
    }

    private fun performConfirmedAction(appInfo: LauncherActivityInfo, appName: String, profile: Int) {
        sharedPreferenceManager.setAppVisible(requireContext(), appInfo.applicationInfo.packageName, profile)
        adapter?.updateApps(appUtils.getHiddenApps(requireActivity()))
    }

    private fun handleCancellation() {
        // Handle the cancellation of the dialog
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HiddenAppsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HiddenAppsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onItemClick(appInfo: LauncherActivityInfo, profile: Int) {
        showConfirmationDialog(appInfo, sharedPreferenceManager.getAppName(requireContext(), appInfo.applicationInfo.packageName,profile, requireContext().packageManager.getApplicationLabel(appInfo.applicationInfo)).toString(), profile)
    }



}
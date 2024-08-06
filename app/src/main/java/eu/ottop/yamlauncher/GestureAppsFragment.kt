package eu.ottop.yamlauncher

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.os.Bundle
import android.os.UserHandle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.setFragmentResult
import androidx.preference.Preference
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GestureAppsFragment : Fragment(), GestureAppsAdapter.OnItemClickListener {

    private var adapter: GestureAppsAdapter? = null
    private val sharedPreferenceManager = SharedPreferenceManager()
    private var stringUtils = StringUtils()
    private val appUtils = AppUtils()
    private lateinit var launcherApps: LauncherApps

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gesture_apps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {

            withContext(Dispatchers.Default) {

                launcherApps = requireContext().getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

                adapter = GestureAppsAdapter(
                    requireContext(),
                    appUtils.getInstalledApps(activity as Activity, launcherApps).toMutableList(),
                    this@GestureAppsFragment
                )
            }
            val recyclerView = view.findViewById<RecyclerView>(R.id.gesture_app_recycler)
            val appMenuEdgeFactory = AppMenuEdgeFactory(requireActivity())

            recyclerView.edgeEffectFactory = appMenuEdgeFactory
            recyclerView.adapter = adapter

            recyclerView.scrollToPosition(0)

            val searchView = view.findViewById<EditText>(R.id.gestureAppSearch)

            recyclerView.addOnLayoutChangeListener { _, _, top, _, bottom, _, oldTop, _, oldBottom ->

                if (bottom - top > oldBottom - oldTop) {
                    searchView.clearFocus()
                }
            }

            searchView.addTextChangedListener(object :
                TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {

                    filterItems(searchView.text.toString())

                }
            })
        }
    }

    private fun filterItems(query: String?) {

        val cleanQuery = stringUtils.cleanString(query)
        val newFilteredApps = mutableListOf<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>()
        val updatedApps = appUtils.getInstalledApps(requireActivity(), launcherApps)

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
            setMessage("Are you sure you want to set $appName? as the gesture app")
            setPositiveButton("Yes") { _, _ ->
                // Perform action on confirmation
                performConfirmedAction(appInfo, appName, profile)
            }

            setNegativeButton("Cancel") { _, _ ->
            }

        }.create().show()
    }

    private fun performConfirmedAction(appInfo: LauncherActivityInfo, appName: String, profile: Int) {
        val result = Bundle().apply {
            putString("gesture_app", "$appName§splitter§${appInfo.applicationInfo.packageName}§splitter§$profile")
        }
        setFragmentResult("request_key", result)
        requireActivity().supportFragmentManager.popBackStack()
    }


    override fun onItemClick(appInfo: LauncherActivityInfo, profile: Int) {
        showConfirmationDialog(appInfo, sharedPreferenceManager.getAppName(requireContext(), appInfo.applicationInfo.packageName,profile, requireContext().packageManager.getApplicationLabel(appInfo.applicationInfo)).toString(), profile)
    }


}
package eu.ottop.yamlauncher

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.LauncherActivityInfo
import android.os.Bundle
import android.os.UserHandle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText

class HiddenAppsFragment : Fragment(), HiddenAppsAdapter.OnItemClickListener {
    private val appUtils = AppUtils()
    private val sharedPreferenceManager = SharedPreferenceManager()
    private var adapter: HiddenAppsAdapter? = null
    private var stringUtils = StringUtils()
    private val uiUtils = UIUtils()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hidden_apps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HiddenAppsAdapter(requireContext(), appUtils.getHiddenApps(activity as Activity).toMutableList(), this)
        val recyclerView = view.findViewById<RecyclerView>(R.id.hidden_app_recycler)
        val appMenuEdgeFactory = AppMenuEdgeFactory(requireActivity())
        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        recyclerView.edgeEffectFactory = appMenuEdgeFactory
        recyclerView.adapter = adapter

        recyclerView.scrollToPosition(0)

        val searchView = view.findViewById<TextInputEditText>(R.id.hiddenAppSearch)

        uiUtils.setMenuTitleAlignment(preferences, view.findViewById(R.id.hidden_menutitle))
        uiUtils.setSearchAlignment(preferences, searchView)
        uiUtils.setSearchSize(preferences, searchView)

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

        if (PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("autoKeyboard", false)) {
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            searchView.requestFocus()
            imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT)
        }
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
                performConfirmedAction(appInfo, profile)
            }

            setNegativeButton("Cancel") { _, _ ->
            }
        }.create().show()
    }

    private fun performConfirmedAction(appInfo: LauncherActivityInfo, profile: Int) {
        sharedPreferenceManager.setAppVisible(requireContext(), appInfo.applicationInfo.packageName, profile)
        adapter?.updateApps(appUtils.getHiddenApps(requireActivity()))
    }

    override fun onItemClick(appInfo: LauncherActivityInfo, profile: Int) {
        showConfirmationDialog(appInfo, sharedPreferenceManager.getAppName(requireContext(), appInfo.applicationInfo.packageName,profile, requireContext().packageManager.getApplicationLabel(appInfo.applicationInfo)).toString(), profile)
    }

}
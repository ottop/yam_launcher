package eu.ottop.yamlauncher

import android.content.Context
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.UserHandle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import eu.ottop.yamlauncher.databinding.ActivityAppMenuBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WorkAppsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WorkAppsFragment : Fragment() {
    private lateinit var binding: ActivityAppMenuBinding
    private lateinit var searchView: SearchView
    private lateinit var container: LinearLayout
    private lateinit var tabLayout: TabLayout
    private var checkApps: Job? = null
    private lateinit var shownApps: List<LauncherActivityInfo>


    override fun onCreateView(
        inflater: LayoutInflater, viewContainer: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_personal_apps, viewContainer, false)

        // Initialize views from the fragment layout
        container = view.findViewById(R.id.container)
        searchView = requireActivity().findViewById(R.id.searchView)

        shownApps = listOf<LauncherActivityInfo>()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                filterItems(newText)
                return true
            }

        })

        return view
    }

    override fun onPause() {
        super.onPause()
        checkApps?.cancel()
    }

    override fun onStart() {
        super.onStart()
        startTask()
    }

    private fun filterItems(query: String?) {
        val cleanQuery = query?.replace("[^a-zA-Z0-9]".toRegex(), "")

        for (i in 0 until container.childCount) {
            val view = container.getChildAt(i)

            if (view is TextView) {
                val itemText = view.text.toString()
                val cleanItemText = itemText.replace("[^a-zA-Z0-9]".toRegex(), "")

                if (cleanItemText.contains(cleanQuery ?: "", ignoreCase = true)) {
                    view.visibility = View.VISIBLE
                } else {
                    view.visibility = View.GONE
                }
            }
        }
    }

    private fun startTask() {
        checkApps = GlobalScope.launch {
            while (true) {
                Log.d("APPSITUATION1234", "Ahahahaha")
                if (!listsEqual(shownApps, getInstalledWorkApps())) {
                    shownApps = getInstalledWorkApps()
                    requireActivity().runOnUiThread {
                        deleteAppMenuContents()
                        createWorkAppMenu()
                    }

                }
                delay(1000)
            }
        }
    }

    private fun listsEqual(
        list1: List<LauncherActivityInfo>,
        list2: List<LauncherActivityInfo>
    ): Boolean {
        if (list1.size != list2.size) return false

        for (i in list1.indices) {
            if (list1[i].label != list2[i].label) {
                return false
            }
        }

        return true
    }

    private fun getInstalledApps(profile: Int): List<LauncherActivityInfo> {
        val allApps = mutableListOf<LauncherActivityInfo>()
        val launcherApps =
            requireActivity().getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

        allApps.addAll(launcherApps.getActivityList(null, launcherApps.profiles[profile]))

        return allApps.sortedWith(compareBy {
            it.applicationInfo.loadLabel(requireActivity().packageManager).toString().lowercase()
        })
    }

    private fun getInstalledWorkApps(): List<LauncherActivityInfo> {
        return getInstalledApps(1)
    }

    private fun createAppMenu(
        appInfo: LauncherActivityInfo,
        userHandle: UserHandle,
        workProfile: Boolean
    ): Boolean {
        val appInfo = appInfo.activityInfo.applicationInfo
        val textView = TextView(requireContext())
        textView.textSize = 28f
        textView.setPadding(0, 0, 0, 50)

        val states = arrayOf(
            intArrayOf(-android.R.attr.state_pressed),
            intArrayOf(android.R.attr.state_pressed)
        )

        val colors = intArrayOf(
            Color.parseColor("#f3f3f3"),   // Default text color
            Color.parseColor("#c3c3c3") // Text color when pressed
        )

        textView.setTextColor(ColorStateList(states, colors))

        textView.isClickable = true
        textView.focusable = View.FOCUSABLE
        textView.gravity = Gravity.START
        textView.text = appInfo.loadLabel(requireActivity().packageManager)

        textView.setOnLongClickListener {
            Toast.makeText(
                requireContext(),
                "Long press detected on ${appInfo.loadLabel(requireActivity().packageManager)}",
                Toast.LENGTH_SHORT
            ).show()

            true
        }

        textView.setOnClickListener {
            val launcherApps =
                requireActivity().getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
            val packageName = appInfo.packageName

            val mainActivity = launcherApps.getActivityList(packageName, userHandle).firstOrNull()

            if (mainActivity != null) {
                launcherApps.startMainActivity(mainActivity.componentName, userHandle, null, null)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Unable to launch ${appInfo.loadLabel(requireActivity().packageManager)}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        // Add the button to the LinearLayout
        container.addView(textView)

        return true
    }

    private fun createWorkAppMenu(): Boolean {
        val pApps = getInstalledWorkApps()
        pApps.forEach { appInfo ->
            createAppMenu(appInfo, pApps.first().user, false)
        }
        return true
    }

    private fun deleteAppMenuContents(): Boolean {
        binding.container.removeAllViewsInLayout()
        return true

    }

    fun setActivityReferences(binding: ActivityAppMenuBinding) {
        this.binding = binding
    }

    companion object {
        fun newInstance(binding: ActivityAppMenuBinding) = WorkAppsFragment().apply {
            this.binding = binding
        }
    }
}
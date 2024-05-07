package eu.ottop.yamlauncher

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.UserHandle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import eu.ottop.yamlauncher.databinding.ActivityAppMenuBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AppMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppMenuBinding
    private lateinit var searchView: SearchView
    private lateinit var container: LinearLayout
    private lateinit var shownApps: List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>
    private var checkApps: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAppMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(null)
        shownApps = listOf()
        searchView = findViewById(R.id.searchView)
        container = findViewById(R.id.container)

        // Set a listener on the search view
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter items based on the search query
                filterItems(newText)
                return true
            }

        })

        binding.root.addOnLayoutChangeListener { _, _, top, _, bottom, _, oldTop, _, oldBottom ->
            if (bottom - top > oldBottom - oldTop) {
                searchView.clearFocus()
            }
        }

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

    override fun onStop() {
        super.onStop()
        checkApps?.cancel()
    }

    override fun onStart() {
        super.onStart()
        startTask()
    }

    private fun startTask() {
        checkApps = GlobalScope.launch {
            while (true) {
                if (!listsEqual(shownApps, getInstalledApps())) {
                    shownApps = getInstalledApps()
                    runOnUiThread {
                        refreshAppMenu()
                    }

                }
                delay(1000)
            }
        }
    }

    private fun listsEqual(
        list1: List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>,
        list2: List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>
    ): Boolean {
        if (list1.size != list2.size) return false

        for (i in list1.indices) {
            if (list1[i].first.componentName != list2[i].first.componentName || list1[i].second.first != list2[i].second.first) {
                return false
            }
        }

        return true
    }

    private fun refreshAppMenu() {
        deleteAppMenuContents()
        createAppMenu()
    }

    private fun deleteAppMenuContents(): Boolean {
        binding.container.removeAllViewsInLayout()
        return true
    }

    private fun createAppMenu(): Boolean {
        val apps = getInstalledApps()
        apps.forEach { appInfo ->
            createAppText(appInfo.first, appInfo.second.first, appInfo.second.second)
        }
        return true
    }

    private fun getInstalledApps(): List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>> {
        val allApps = mutableListOf<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>()
        val launcherApps = this.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        for (i in launcherApps.profiles.indices) {
            launcherApps.getActivityList(null, launcherApps.profiles[i]).forEach { app ->
                if (!isAppHidden(app.activityInfo.applicationInfo.packageName, i)) {
                    allApps.add(Pair(app, Pair(launcherApps.profiles[i], i)))
                }
            }
        }
        return allApps.sortedBy {
            it.first.applicationInfo.loadLabel(packageManager).toString().lowercase()
        }
    }

    private fun createAppText(
        appInfo: LauncherActivityInfo,
        userHandle: UserHandle,
        workProfile: Int
    ): Boolean {
        val appInfo = appInfo.activityInfo.applicationInfo

        val textView = TextView(this)
        val launcherApps = getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        val mainActivity =
            launcherApps.getActivityList(appInfo.packageName, userHandle).firstOrNull()

        setupTextView(textView, appInfo, workProfile)

        setupTextListeners(textView, appInfo, userHandle, workProfile, launcherApps, mainActivity)

        binding.container.addView(textView)

        return true
    }

    private fun setupTextView(textView: TextView, appInfo: ApplicationInfo, workProfile: Int) {
        val states = arrayOf(
            intArrayOf(-android.R.attr.state_pressed),
            intArrayOf(android.R.attr.state_pressed)
        )

        val colors = intArrayOf(
            Color.parseColor("#f3f3f3"),   // Default text color
            Color.parseColor("#c3c3c3") // Text color when pressed
        )

        with(textView) {
            textSize = 28f
            setPadding(0, 10, 0, 80)
            isClickable = true
            focusable = View.FOCUSABLE
            gravity = Gravity.START

            text = if (workProfile != 0) {
                "*" + appInfo.loadLabel(packageManager)
            } else {
                appInfo.loadLabel(packageManager)
            }
            setTextColor(ColorStateList(states, colors))
        }
    }

    private fun setupTextListeners(
        textView: TextView,
        appInfo: ApplicationInfo,
        userHandle: UserHandle,
        workProfile: Int,
        launcherApps: LauncherApps,
        mainActivity: LauncherActivityInfo?
    ) {
        textView.setOnLongClickListener {
            appActionMenu(textView, appInfo, userHandle, workProfile, launcherApps, mainActivity)
        }

        textView.setOnClickListener {
            if (mainActivity != null) {
                launcherApps.startMainActivity(mainActivity.componentName, userHandle, null, null)
            } else {
                Toast.makeText(
                    this,
                    "Unable to launch ${appInfo.loadLabel(packageManager)}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun appActionMenu(
        textView: TextView,
        appInfo: ApplicationInfo,
        userHandle: UserHandle,
        workProfile: Int,
        launcherApps: LauncherApps,
        mainActivity: LauncherActivityInfo?
    ): Boolean {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.app_action_menu, null)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        popupWindow.animationStyle = android.R.style.Animation_Translucent

        if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
            popupView.findViewById<TextView>(R.id.uninstall).visibility = View.GONE
        }

        textView.visibility = View.INVISIBLE

        popupWindow.showAsDropDown(textView, 0, -textView.height)

        popupWindow.setOnDismissListener {
            textView.visibility = View.VISIBLE
        }

        popupView.findViewById<TextView>(R.id.info).setOnClickListener {
            if (mainActivity != null) {
                launcherApps.startAppDetailsActivity(
                    mainActivity.componentName,
                    userHandle,
                    null,
                    null
                )
            }

            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.uninstall).setOnClickListener {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:${appInfo.packageName}")
            intent.putExtra(Intent.EXTRA_USER, userHandle)
            startActivity(intent)

            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.rename).setOnClickListener {
            // Handle rename action
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.hide).setOnClickListener {
            setAppHidden(appInfo.packageName, workProfile, true)
            refreshAppMenu()

            popupWindow.dismiss()
        }

        return true // Indicate that the long click event is consumed}
    }

    private fun setAppHidden(packageName: String, profile: Int, hidden: Boolean) {
        // Get the shared preferences editor
        val editor = getSharedPreferences("app_data", MODE_PRIVATE).edit()
        val key = "$packageName-${profile}"
        editor.putBoolean(key, hidden)
        editor.apply()
    }

    private fun isAppHidden(packageName: String, profile: Int): Boolean {
        // Get the shared preferences object
        val sharedPref = getSharedPreferences("app_data", MODE_PRIVATE)
        val key = "$packageName-${profile}"
        return sharedPref.getBoolean(key, false) // Default to false (visible)
    }

    private fun setAppVisible(packageName: String, profile: Int) {
        // Get the shared preferences editor
        val editor = getSharedPreferences("app_data", MODE_PRIVATE).edit()
        val key = "$packageName-${profile}"
        editor.remove(key)
        editor.apply()
    }
}
package eu.ottop.yamlauncher

import android.content.Context
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.UserHandle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import eu.ottop.yamlauncher.databinding.ActivityAppMenuBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.SortedMap

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
        shownApps = listOf<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>()
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

        binding.root.addOnLayoutChangeListener { view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom - top > oldBottom - oldTop) {
                searchView.clearFocus()
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
                if (!listsEqual(shownApps, getInstalledPersonalApps())) {
                    Log.d("AHAHHAHAA","AHJAHAHA")
                    shownApps = getInstalledPersonalApps()
                    runOnUiThread {
                        deleteAppMenuContents()
                        createPersonalAppMenu()
                    }

                }
                delay(1000)
            }
        }
    }

    private fun listsEqual(list1: List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>, list2: List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>): Boolean {
        if (list1.size != list2.size) return false

        for (i in list1.indices) {
            if (list1[i].first.componentName != list2[i].first.componentName || list1[i].second.first != list2[i].second.first) {
                return false
            }
        }

        return true
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

    private fun getInstalledApps(): List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>> {
        val allApps = mutableListOf<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>()
        val launcherApps = this.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        for (i in launcherApps.profiles.indices) {
            launcherApps.getActivityList(null, launcherApps.profiles[i]).forEach { app ->
                allApps.add(Pair(app, Pair(launcherApps.profiles[i], i)))
            }

        }
        return allApps.sortedBy { it.first.label.toString().lowercase() }
    }

    private fun getInstalledPersonalApps(): List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>> {
        return getInstalledApps()
    }

    private fun getInstalledWorkApps(): List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>> {
        return getInstalledApps()
    }

    private fun createAppMenu(appInfo: LauncherActivityInfo, userHandle: UserHandle, workProfile: Int): Boolean {
        val appInfo = appInfo.activityInfo.applicationInfo
        val textView = TextView(this)

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
            setPadding(0,0,0,50)
            isClickable = true
            focusable = View.FOCUSABLE
            gravity = Gravity.START

            if (workProfile != 0) {
                text = "*"+appInfo.loadLabel(packageManager)
            }
            else {
                text = appInfo.loadLabel(packageManager)
            }
            setTextColor(ColorStateList(states, colors))
        }


        textView.setOnLongClickListener {
            Toast.makeText(this, "Long press detected on ${appInfo.loadLabel(packageManager)}", Toast.LENGTH_SHORT).show()

            true
        }

        textView.setOnClickListener {
            val launcherApps = getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
            val packageName = appInfo.packageName

            val mainActivity = launcherApps.getActivityList(packageName, userHandle).firstOrNull()

            if (mainActivity != null) {
                launcherApps.startMainActivity(mainActivity.componentName, userHandle, null, null)
            } else {
                Toast.makeText(this, "Unable to launch ${appInfo.loadLabel(packageManager)}", Toast.LENGTH_SHORT).show()
            }

        }

        // Add the button to the LinearLayout
        binding.container.addView(textView)

        return true
    }

    private fun createPersonalAppMenu(): Boolean {
        val pApps = getInstalledPersonalApps()
        pApps.forEach { appInfo ->
            createAppMenu(appInfo.first, appInfo.second.first, appInfo.second.second)
        }
        return true
    }

    private fun deleteAppMenuContents(): Boolean {
        binding.container.removeAllViewsInLayout()
        return true
    }
}

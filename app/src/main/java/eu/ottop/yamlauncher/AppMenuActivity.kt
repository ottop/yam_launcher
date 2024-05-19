package eu.ottop.yamlauncher

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.os.Bundle
import android.os.UserHandle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eu.ottop.yamlauncher.databinding.ActivityAppMenuBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AppMenuActivity : AppCompatActivity(), AppMenuAdapter.OnItemClickListener, AppMenuAdapter.OnShortcutListener, AppMenuAdapter.OnItemLongClickListener {

    private lateinit var binding: ActivityAppMenuBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: EditText
    private lateinit var adapter: AppMenuAdapter
    private lateinit var filteredApps: MutableList<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>
    private lateinit var installedApps: List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>
    private lateinit var job: Job
    private var appActionMenu = AppActionMenu()
    private lateinit var launcherApps: LauncherApps

    private val sharedPreferenceManager = SharedPreferenceManager()

    private lateinit var menuMode: String

    companion object {
        private lateinit var callback: (Pair<Pair<String, Int>, Pair<LauncherActivityInfo, UserHandle>>) -> Unit
        private const val MENU_MODE = "abcd"

        fun start(context: Context, param1: String = "app", callback: (Pair<Pair<String, Int>, Pair<LauncherActivityInfo, UserHandle>>) -> Unit) {
            val intent = Intent(context, AppMenuActivity::class.java).apply {
                putExtra(MENU_MODE, param1)
            }
            context.startActivity(intent)

            this.callback = callback
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        menuMode = intent.getStringExtra(MENU_MODE) ?: "app"

        binding = ActivityAppMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(null)
        launcherApps = getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        searchView = findViewById(R.id.searchView)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        installedApps = getInstalledApps()
        filteredApps = mutableListOf()
        adapter = AppMenuAdapter(this@AppMenuActivity, installedApps, this, this,this, menuMode)
        recyclerView.adapter = adapter

        setupSearch()
    }

    override fun onItemClick(appInfo: LauncherActivityInfo, userHandle: UserHandle) {
        val mainActivity = launcherApps.getActivityList(appInfo.applicationInfo.packageName, userHandle).firstOrNull()
        if (mainActivity != null) {
            launcherApps.startMainActivity(mainActivity.componentName, userHandle, null, null)
            finish()
        } else {
            Toast.makeText(this, "Cannot launch app", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onShortcut(appInfo: LauncherActivityInfo, userHandle: UserHandle, textView: TextView, userProfile: Int) {
        callback.invoke(Pair(Pair(textView.text.toString(), userProfile), Pair(appInfo, userHandle,)))
        finish()
    }

    override fun onItemLongClick(
        appInfo: LauncherActivityInfo,
        userHandle: UserHandle,
        userProfile: Int,
        textView: TextView,
        actionMenuLayout: LinearLayout,
        editView: LinearLayout
    ) {
            textView.visibility = View.INVISIBLE
            actionMenuLayout.visibility = View.VISIBLE
            val mainActivity =
                launcherApps.getActivityList(appInfo.applicationInfo.packageName, userHandle)
                    .firstOrNull()
            appActionMenu.setActionListeners(
                this@AppMenuActivity,
                CoroutineScope(Dispatchers.Main),
                binding,
                textView,
                editView,
                actionMenuLayout,
                searchView,
                appInfo.applicationInfo,
                userHandle,
                userProfile,
                launcherApps,
                mainActivity
            )
    }

    private fun setupSearch() {
        binding.root.addOnLayoutChangeListener { _, _, top, _, bottom, _, oldTop, _, oldBottom ->
            if (bottom - top > oldBottom - oldTop) {
                searchView.clearFocus()
            }
        }

        searchView.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterItems(searchView.text.toString())

            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun filterItems(query: String?) {
        CoroutineScope(Dispatchers.Default).launch {
            val cleanQuery = query?.clean()
            filteredApps.clear()

            if (cleanQuery.isNullOrEmpty()) {
                filteredApps.addAll(installedApps)
            } else {
                installedApps.forEach {
                    val cleanItemText = sharedPreferenceManager.getAppName(this@AppMenuActivity, it.first.applicationInfo.packageName, it.second.second, it.first.applicationInfo.loadLabel(packageManager)).toString().clean()
                    if (cleanItemText.contains(cleanQuery, ignoreCase = true)) {
                        filteredApps.add(it)
                    }
                }
            }
            withContext(Dispatchers.Main) {
                adapter.updateApps(filteredApps)
            }
        }

    }

    private fun String.clean(): String {
        return this.replace("[^a-zA-Z0-9]".toRegex(), "")
    }

    private fun getInstalledApps(): List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>> {
        val allApps = mutableListOf<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>()
        val launcherApps = getSystemService(LAUNCHER_APPS_SERVICE) as LauncherApps
        for (i in launcherApps.profiles.indices) {
            launcherApps.getActivityList(null, launcherApps.profiles[i]).forEach { app ->
                if (!sharedPreferenceManager.isAppHidden(this@AppMenuActivity, app.applicationInfo.packageName, i)) {
                    allApps.add(Pair(app, Pair(launcherApps.profiles[i], i)))
                }
            }
        }
        return allApps.sortedBy {
            sharedPreferenceManager.getAppName(this, it.first.applicationInfo.packageName,it.second.second, it.first.applicationInfo.loadLabel(packageManager)).toString().lowercase()
        }


    }

    override fun onStop() {
        super.onStop()
        job.cancel()

    }

    override fun onStart() {
        super.onStart()
        startTask()
    }

    private fun startTask() {
        job = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                val updatedApps = getInstalledApps()
                if (!listsEqual(installedApps, updatedApps)) {
                    installedApps = updatedApps
                    withContext(Dispatchers.Main) {
                        adapter.updateApps(installedApps)
                    }
                }
                delay(5000)
            }
        }
    }

    fun manualRefreshApps() {
        CoroutineScope(Dispatchers.Default).launch {
            installedApps = getInstalledApps()
            withContext(Dispatchers.Main) {
                adapter.updateApps(installedApps)
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

}
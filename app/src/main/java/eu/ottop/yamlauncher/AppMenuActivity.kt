package eu.ottop.yamlauncher

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
import android.view.inputmethod.InputMethodManager
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
        private const val MENU_MODE = "app"

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

        if (menuMode == "shortcut") {
            binding.menutitle.visibility = View.VISIBLE
        }

        launcherApps = getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        searchView = findViewById(R.id.searchView)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.scrollToPosition(0)
        installedApps = getInstalledApps()
        filteredApps = mutableListOf()
        filteredApps.addAll(installedApps)
        val newApps = mutableListOf<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>()
        newApps.addAll(installedApps)
        adapter = AppMenuAdapter(this@AppMenuActivity, newApps, this, this,this, menuMode)
        recyclerView.adapter = adapter

        setupSearch()
    }

    override fun onItemClick(appInfo: LauncherActivityInfo, userHandle: UserHandle) {
        val mainActivity = launcherApps.getActivityList(appInfo.applicationInfo.packageName, userHandle).firstOrNull()
        if (mainActivity != null) {
            launcherApps.startMainActivity(mainActivity.componentName, userHandle, null, null)
        } else {
            Toast.makeText(this, "Cannot launch app", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onShortcut(appInfo: LauncherActivityInfo, userHandle: UserHandle, textView: TextView, userProfile: Int) {
        callback.invoke(Pair(Pair(textView.text.toString(), userProfile), Pair(appInfo, userHandle)))
        finish()
    }

    override fun onItemLongClick(
        appInfo: LauncherActivityInfo,
        userHandle: UserHandle,
        userProfile: Int,
        textView: TextView,
        actionMenuLayout: LinearLayout,
        editView: LinearLayout,
        position: Int
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
                mainActivity,
                position
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
            val newFilteredApps = mutableListOf<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>()

            if (cleanQuery.isNullOrEmpty()) {
                newFilteredApps.addAll(installedApps)
            } else {
                installedApps.forEachIndexed {index, it ->
                    val cleanItemText = sharedPreferenceManager.getAppName(this@AppMenuActivity, it.first.applicationInfo.packageName, it.second.second, it.first.applicationInfo.loadLabel(packageManager)).toString().clean()
                    if (cleanItemText.contains(cleanQuery, ignoreCase = true)) {
                        newFilteredApps.add(it)
                    }
                }
            }

            val changes = detectChanges(filteredApps, newFilteredApps)
            withContext(Dispatchers.Main) {
                applyChanges(changes, newFilteredApps)
            }
            filteredApps = newFilteredApps
        }

    }

    private fun String.clean(): String {
        return this.replace("[^a-zA-Z0-9]".toRegex(), "")
    }

    fun getInstalledApps(): List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>> {
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
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun startTask() {
        job = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                manualRefresh()
                delay(5000)
            }
        }
    }

    fun manualRefresh() {
        CoroutineScope(Dispatchers.Default).launch {
            val updatedApps = getInstalledApps()
            val changes = detectChanges(installedApps, updatedApps)

            withContext(Dispatchers.Main) {
                applyChanges(changes, installedApps)
            }
            installedApps = updatedApps
        }
        }

    data class Change(val type: ChangeType, val position: Int, val newPosition: Int = 0)

    enum class ChangeType {
        INSERT, REMOVE, UPDATE
    }

    private fun detectChanges(oldList: List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>, newList: List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>): List<Change> {
        val changes = mutableListOf<Change>()
        val removalChanges = mutableListOf<Change>()
        val oldSet = oldList.map { Pair(it.first.applicationInfo.packageName, it.second.second) }.toSet()
        val newSet = newList.map { Pair(it.first.applicationInfo.packageName, it.second.second) }.toSet()

        oldList.forEachIndexed { index, oldItem ->
            if (newSet.contains(Pair(oldItem.first.applicationInfo.packageName, oldItem.second.second))) {
                val newIndex = newList.indexOfFirst { it.first.applicationInfo.packageName == oldItem.first.applicationInfo.packageName && it.second.second == oldItem.second.second }
                if (oldItem.first.componentName != newList[newIndex].first.componentName) {
                    changes.add(Change(ChangeType.UPDATE, index))
                }

            }
        }

        // Detect insertions
        newList.forEachIndexed { index, newItem ->
            if (!oldSet.contains(Pair(newItem.first.applicationInfo.packageName, newItem.second.second))) {
                changes.add(Change(ChangeType.INSERT, index))
            }
        }

        // Detect removals
        oldList.forEachIndexed { index, oldItem ->
            if (!newSet.contains(Pair(oldItem.first.applicationInfo.packageName, oldItem.second.second))) {
                removalChanges.add(Change(ChangeType.REMOVE, index))
            }
        }

        // Detect updates


        changes.addAll(removalChanges.reversed())

        return changes
    }

    private fun applyChanges(changes: List<Change>, updatedApps: List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>) {
        changes.forEach { change ->
            when (change.type) {
                ChangeType.INSERT -> {
                    insertItem(change.position, updatedApps[change.position])
                }
                ChangeType.REMOVE -> {
                    removeItem(change.position)
                }
                ChangeType.UPDATE -> {
                    updateItem(change.position, updatedApps[change.position])
                }
            }
        }
    }

    private fun insertItem(position: Int, app: Pair<LauncherActivityInfo, Pair<UserHandle, Int>>) {
        adapter.addApp(position, app)
        adapter.notifyItemInserted(position)
    }
    private fun removeItem(position: Int) {
        adapter.removeApp(position)
        adapter.notifyItemRemoved(position)
    }

    fun updateItem(position: Int, app: Pair<LauncherActivityInfo, Pair<UserHandle, Int>>) {
        adapter.updateApp(position, app)
        adapter.notifyItemChanged(position)
    }

    fun moveItem(position: Int, newPosition: Int) {
        Log.d("Movestatus","MOVED")
        adapter.moveApp(position, newPosition)
        adapter.notifyItemMoved(position, newPosition)
    }

}

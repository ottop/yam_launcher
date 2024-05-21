package eu.ottop.yamlauncher

import android.content.Context
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.os.Bundle
import android.os.UserHandle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import eu.ottop.yamlauncher.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

class MainActivity : AppCompatActivity(), AppMenuAdapter.OnItemClickListener, AppMenuAdapter.OnShortcutListener, AppMenuAdapter.OnItemLongClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gestureDetector: GestureDetector
    private lateinit var launcherApps: LauncherApps
    private lateinit var installedApps: List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: EditText
    private lateinit var adapter: AppMenuAdapter
    private var job: Job? = null
    private var appActionMenu = AppActionMenu()

    private val sharedPreferenceManager = SharedPreferenceManager()
    private val appUtils = AppUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(null)

        launcherApps = getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

        for (i in findViewById<LinearLayout>(R.id.shortcuts).children) {

            val textView = i as TextView

            val savedView = sharedPreferenceManager.getShortcut(this, textView)

            textView.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(resources, R.drawable.ic_empty, null),null,null,null)

            textView.compoundDrawablePadding = 0

            i.setOnClickListener {
                Toast.makeText(this, "Long click to select an app", Toast.LENGTH_SHORT).show()
            }

            if (savedView?.get(1) != "e") {

                if (savedView?.get(1) != "0") {
                    textView.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(resources, R.drawable.ic_work_app, null),null,null,null)
                }
                else {
                    textView.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(resources, R.drawable.ic_empty, null),null,null,null)
                }
                textView.text = savedView?.get(2)
                textView.setOnClickListener {
                    val mainActivity = launcherApps.getActivityList(savedView?.get(0).toString(), launcherApps.profiles[savedView?.get(1)!!.toInt()]).firstOrNull()
                    if (mainActivity != null) {
                        launcherApps.startMainActivity(mainActivity.componentName,  launcherApps.profiles[savedView[1].toInt()], null, null)
                    } else {
                        Toast.makeText(this, "Cannot launch app", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            i.setOnLongClickListener {
                binding.homeView.visibility = View.GONE

                val newApps = mutableListOf<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>()
                newApps.addAll(installedApps)
                adapter = AppMenuAdapter(this@MainActivity, newApps, this@MainActivity, this@MainActivity, this@MainActivity, "shortcut", i)
                recyclerView.adapter = adapter
                binding.appView.visibility = View.VISIBLE

                return@setOnLongClickListener true
            }}


        gestureDetector = GestureDetector(this, GestureListener())


        //Experimental
        CoroutineScope(Dispatchers.Default).launch {
            installedApps = appUtils.getInstalledApps(this@MainActivity)

            recyclerView = findViewById(R.id.recycler_view)
            recyclerView.scrollToPosition(0)

            searchView = findViewById(R.id.searchView)
            setupSearch()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.appView.visibility = View.GONE
                binding.homeView.visibility = View.VISIBLE

            }
        })
    }

    private fun setupSearch() {
        binding.root.addOnLayoutChangeListener { _, _, top, _, bottom, _, oldTop, _, oldBottom ->
            if (bottom - top > oldBottom - oldTop) {
                searchView.clearFocus()
                if (searchView.text.isNullOrEmpty()) {
                    job?.cancel()
                    startTask()
                }
            }
            else {
                job?.cancel()
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
            val updatedApps = appUtils.getInstalledApps(this@MainActivity)

            if (cleanQuery.isNullOrEmpty()) {
                manualRefresh()
                newFilteredApps.addAll(installedApps)
            } else {
                updatedApps.forEach {
                    val cleanItemText = sharedPreferenceManager.getAppName(this@MainActivity, it.first.applicationInfo.packageName, it.second.second, it.first.applicationInfo.loadLabel(packageManager)).toString().clean()
                    if (cleanItemText.contains(cleanQuery, ignoreCase = true)) {
                        newFilteredApps.add(it)
                    }
                }
            }

            val changes = detectChanges(installedApps, newFilteredApps)
            installedApps = newFilteredApps
            withContext(Dispatchers.Main) {
                applyChanges(changes, installedApps)
            }
        }

    }

    private fun String.clean(): String {
        return this.replace("[^a-zA-Z0-9]".toRegex(), "")
    }

    override fun onStop() {
        super.onStop()
        job?.cancel()

    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    override fun onStart() {
        super.onStart()
        startTask()

        // Keyboard is sometimes open when going back to the app, so close it.
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            // Detect swipe up gesture
            if (e1 != null) {
                val deltaY = e2.y - e1.y
                if (deltaY < -SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    openAppMenuActivity()
                    return true
                }
            }
            return false
        }

    }
    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    fun openAppMenuActivity() {
        //AppMenuActivity.start(this, installedApps) {
        //}
        binding.homeView.visibility = View.GONE
        val newApps = mutableListOf<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>()
        newApps.addAll(installedApps)
        adapter = AppMenuAdapter(this@MainActivity, newApps, this@MainActivity, this@MainActivity, this@MainActivity, "app")
        recyclerView.adapter = adapter
        binding.appView.visibility = View.VISIBLE
    }

    override fun onItemClick(appInfo: LauncherActivityInfo, userHandle: UserHandle) {
        val mainActivity = launcherApps.getActivityList(appInfo.applicationInfo.packageName, userHandle).firstOrNull()
        if (mainActivity != null) {
            launcherApps.startMainActivity(mainActivity.componentName, userHandle, null, null)
        } else {
            Toast.makeText(this, "Cannot launch app", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onShortcut(
        appInfo: LauncherActivityInfo,
        userHandle: UserHandle,
        textView: TextView,
        userProfile: Int,
        shortcutView: TextView
    ) {
        if (userProfile != 0) {
            shortcutView.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(resources, R.drawable.ic_work_app, null),null,null,null)
        }
        else {
            shortcutView.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(resources, R.drawable.ic_empty, null),null,null,null)
        }
        shortcutView.text = textView.text.toString()
        shortcutView.setOnClickListener {
            val mainActivity = launcherApps.getActivityList(appInfo.applicationInfo.packageName, userHandle).firstOrNull()
            if (mainActivity != null) {
                launcherApps.startMainActivity(mainActivity.componentName,  userHandle, null, null)
            } else {
                Toast.makeText(this, "Cannot launch app", Toast.LENGTH_SHORT).show()
            }
        }
        sharedPreferenceManager.setShortcut(this, shortcutView, appInfo.applicationInfo.packageName, userProfile)
        binding.appView.visibility = View.GONE
        binding.homeView.visibility = View.VISIBLE
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
            this@MainActivity,
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

    fun manualRefresh() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val updatedApps = appUtils.getInstalledApps(this@MainActivity)
                val changes = detectChanges(installedApps, updatedApps)
                installedApps = updatedApps
                withContext(Dispatchers.Main) {
                    applyChanges(changes, installedApps)
                }
            }
            catch (_: UninitializedPropertyAccessException) {
            }
            }
        }

    private fun detectChanges(oldList: List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>, newList: List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>): List<Change> {
        val changes = mutableListOf<Change>()
        val removalChanges = mutableListOf<Change>()
        val oldSet = oldList.map { Pair(it.first.applicationInfo.packageName, it.second.second) }.toSet()
        val newSet = newList.map { Pair(it.first.applicationInfo.packageName, it.second.second) }.toSet()

        //Detect updates
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
                    try {
                        removeItem(change.position)
                    }
                    catch (_: IndexOutOfBoundsException) {
                    }
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

data class Change(val type: ChangeType, val position: Int, val newPosition: Int = 0)

enum class ChangeType {
    INSERT, REMOVE, UPDATE
}

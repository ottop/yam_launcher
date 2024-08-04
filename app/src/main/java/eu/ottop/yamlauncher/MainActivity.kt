package eu.ottop.yamlauncher

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.UserHandle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.View.TEXT_ALIGNMENT_TEXT_END
import android.view.View.TEXT_ALIGNMENT_TEXT_START
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.marginLeft
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import eu.ottop.yamlauncher.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import java.lang.reflect.Method


class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener, AppMenuAdapter.OnItemClickListener, AppMenuAdapter.OnShortcutListener, AppMenuAdapter.OnItemLongClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gestureDetector: GestureDetector
    private lateinit var shortcutGestureDetector: GestureDetector
    private lateinit var launcherApps: LauncherApps
    private lateinit var installedApps: List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: TextInputEditText
    private var adapter: AppMenuAdapter? = null
    private var job: Job? = null
    private var weatherJob: Job? = null
    val cameraIntent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE)
    val phoneIntent = Intent(Intent.ACTION_DIAL)
    private var batteryReceiver: BatteryReceiver? = null

    private var appActionMenu = AppActionMenu()
    private val sharedPreferenceManager = SharedPreferenceManager()
    private val appUtils = AppUtils()
    private val appMenuLinearLayoutManager = AppMenuLinearLayoutManager(this@MainActivity)
    private val appMenuEdgeFactory = AppMenuEdgeFactory(this@MainActivity)
    private val animations = Animations()

    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100

    private lateinit var clock: TextClock
    private var clockMargin = 0

    private lateinit var dateText: TextClock

    private lateinit var preferences: SharedPreferences

    private val stringUtils = StringUtils()

    private var dateElements = mutableListOf<String>()

    private val weatherSystem = WeatherSystem()

    private val uiUtils = UIUtils()

    private var isBatteryReceiverRegistered = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(null)

        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        preferences.registerOnSharedPreferenceChangeListener(this)

        window.decorView.setBackgroundColor(
            Color.parseColor(preferences.getString("bgColor",  "#00000000"))
        )

        searchView = findViewById(R.id.searchView)

        launcherApps = getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

        gestureDetector = GestureDetector(this, GestureListener())
        shortcutGestureDetector = GestureDetector(this, TextGestureListener())

        clock = findViewById(R.id.text_clock)

        clockMargin = clock.marginLeft

        dateText = findViewById(R.id.text_date)

        dateElements = mutableListOf(dateText.format12Hour.toString(), dateText.format24Hour.toString(), "", "")

        setClockAlignment(preferences.getString("clockAlignment", "left"))

        setupApps()

        setShortcutAlignment(preferences.getString("shortcutAlignment", "left"), binding.homeView)

        setSearchAlignment(preferences.getString("searchAlignment", "left"))

        setClockSize(preferences.getString("clockSize","medium"))

        setDateSize(preferences.getString("dateSize", "medium"))

        setShortcutSize(binding.homeView)

        setSearchSize(preferences.getString("searchSize", "medium"))

        setSearchColors()

        uiUtils.setAllColors(binding.homeView, Color.parseColor(preferences.getString("textColor",  "#FFF3F3F3")))

        registerBatteryReceiver()

        if (!preferences.getBoolean("battery_enabled", false)) {
            unregisterBatteryReceiver()
        }

        binding.homeView.setOnTouchListener { _, event ->
            super.onTouchEvent(event)
            gestureDetector.onTouchEvent(event)
            true // Return true if the touch event is handled
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backToHome()
            }
        })

    }

    fun modifyDate(value: String, index: Int) {
        dateElements[index] = value
        dateText.format12Hour = "${dateElements[0]}${stringUtils.addStartTextIfNotEmpty(dateElements[2], " | ")}${stringUtils.addStartTextIfNotEmpty(dateElements[3], " | ")}"
        dateText.format24Hour = "${dateElements[1]}${stringUtils.addStartTextIfNotEmpty(dateElements[2], " | ")}${stringUtils.addStartTextIfNotEmpty(dateElements[3], " | ")}"
    }

    private fun startWeatherMonitor() {
        weatherJob?.cancel()
        weatherJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                if (preferences.getBoolean("gps_location", false)) {
                    withContext(Dispatchers.Main) {
                        weatherSystem.setGpsLocation(this@MainActivity)
                    }
                }

                val currentWeather = weatherSystem.getTemp(this@MainActivity)
                withContext(Dispatchers.Main) {
                    modifyDate(currentWeather, 2)
                }
                delay(300000)
            }
        }
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences?, key: String?) {

        when (key) {
            "clockAlignment" -> {
                setClockAlignment(preferences?.getString(key, "left"))
            }

            "shortcutAlignment" -> {
                setShortcutAlignment(preferences?.getString(key, "left"), binding.homeView)
            }

            "searchAlignment" -> {
                setSearchAlignment(preferences?.getString(key, "left"))
            }

            "clockSize" -> {
                setClockSize(preferences?.getString(key,"medium"))
            }

            "dateSize" -> {
                setDateSize(preferences?.getString(key, "medium"))
            }

            "shortcutSize" -> {
                setShortcutSize(binding.homeView)
            }

            "searchSize" -> {
                setSearchSize(preferences?.getString(key, "medium"))
            }

            "bgColor" -> {
                window.setBackgroundDrawable(ColorDrawable(Color.parseColor("#00000000")))
                window.decorView.setBackgroundColor(
                        Color.parseColor(preferences?.getString(key,  "#00000000"))
                    )
            }

            "textColor" -> {
                uiUtils.setAllColors(binding.homeView, Color.parseColor(preferences?.getString(key,  "#FFF3F3F3")))
                setSearchColors()
            }

            "weather_enabled" -> {
                if (preferences?.getBoolean(key, false) == true) {
                    startWeatherMonitor()
                }
                else {
                    weatherJob?.cancel()
                    modifyDate("", 2)
                }
            }

            "battery_enabled" -> {
                if (preferences?.getBoolean(key, false) == true) {
                    registerBatteryReceiver()
                }
                else {
                    unregisterBatteryReceiver()
                    modifyDate("", 3)
                }
            }
        }
    }

    private fun registerBatteryReceiver() {
        if (!isBatteryReceiverRegistered) {
            batteryReceiver = BatteryReceiver.register(this, this@MainActivity)
            isBatteryReceiverRegistered = true
        }
    }

    private fun unregisterBatteryReceiver() {
        if (isBatteryReceiverRegistered) {
            unregisterReceiver(batteryReceiver)
            isBatteryReceiverRegistered = false
        }
    }

    private fun setSearchColors() {
        val viewTreeObserver = searchView.viewTreeObserver
        val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Your code here
                searchView.setTextColor(Color.parseColor(preferences.getString("textColor", "#FFF3F3F3")))
                searchView.setHintTextColor(uiUtils.setAlpha(Color.parseColor(preferences.getString("textColor", "#FFF3F3F3")), "A9"))
                searchView.compoundDrawables[0].mutate().colorFilter =
                    BlendModeColorFilter(Color.parseColor(preferences.getString("textColor", "#FFF3F3F3")), BlendMode.SRC_ATOP)

                // Remove the listener
                if (viewTreeObserver.isAlive) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        }

        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        backToHome()
    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
        weatherJob?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
        unregisterBatteryReceiver()
        preferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onStart() {
        super.onStart()
        startTask()
        if (preferences.getBoolean("weather_enabled", false)) {
            startWeatherMonitor()
        }

        // Keyboard is sometimes open when going back to the app, so close it.
        closeKeyboard()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        binding.homeView.visibility = View.VISIBLE
        binding.appView.visibility = View.INVISIBLE
        adapter?.notifyDataSetChanged()
    }

    open inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        @SuppressLint("WrongConstant")
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 != null) {
                val deltaY = e2.y - e1.y
                val deltaX = e2.x - e1.x

                // Detect swipe up
                if (deltaY < -swipeThreshold && abs(velocityY) > swipeVelocityThreshold) {
                    openAppMenuActivity()
                }

                // Detect swipe down
                else if (deltaY > swipeThreshold && abs(velocityY) > swipeVelocityThreshold) {
                    val statusBarService = getSystemService(Context.STATUS_BAR_SERVICE)
                    val statusBarManager: Class<*> = Class.forName("android.app.StatusBarManager")
                    val expandMethod: Method = statusBarManager.getMethod("expandNotificationsPanel")
                    expandMethod.invoke(statusBarService)
                }

                // Detect swipe left
                else if (deltaX < -swipeThreshold && abs(velocityX) > swipeVelocityThreshold && preferences.getBoolean("cameraSwipe", true)){
                    startActivity(cameraIntent)
                }

                // Detect swipe right
                else if (deltaX > -swipeThreshold && abs(velocityX) > swipeVelocityThreshold && preferences.getBoolean("phoneSwipe", true)) {
                    startActivity(phoneIntent)
                }
            }
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }

    }

    inner class TextGestureListener : GestureListener() {
        override fun onLongPress(e: MotionEvent) {

        }
    }

    private fun setupApps() {
            handleListItems()
        CoroutineScope(Dispatchers.Default).launch {
            installedApps = appUtils.getInstalledApps(this@MainActivity)
            val newApps = installedApps.toMutableList()

            setupRecyclerView(newApps)

            setupSearch()
        }

    }

    private fun handleListItems() {
        for (i in arrayOf(R.id.app1, R.id.app2, R.id.app3, R.id.app4, R.id.app5, R.id.app6, R.id.app7, R.id.app8)) {

            val textView = findViewById<TextView>(i)

            unselectedSetup(textView)

            val savedView = sharedPreferenceManager.getShortcut(this, textView)

            if (savedView?.get(1) != "e") {
                selectedSetup(textView, savedView)
            }

        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun unselectedSetup(textView: TextView) {
        textView.setOnTouchListener {_, event ->
            shortcutGestureDetector.onTouchEvent(event)
            super.onTouchEvent(event)
        }

        textView.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(resources, R.drawable.ic_empty, null),null,null,null)

        unselectedListeners(textView)
    }

    private fun selectedSetup(textView: TextView, savedView: List<String>?) {
        if (savedView?.get(1) != "0") {
            textView.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(resources, R.drawable.ic_work_app, null),null,null,null)
        }
        else {
            textView.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(resources, R.drawable.ic_empty, null),null,null,null)
        }
        textView.text = savedView?.get(2)
        selectedListeners(textView, savedView)
    }

    private fun unselectedListeners(textView: TextView) {
        textView.setOnClickListener {
            Toast.makeText(this, "Long click to select an app", Toast.LENGTH_SHORT).show()
        }
        textView.setOnLongClickListener {
            adapter?.menuMode = "shortcut"
            adapter?.shortcutTextView = textView
            toAppMenu()

            return@setOnLongClickListener true
        }
    }

    private fun selectedListeners(textView: TextView, savedView: List<String>?) {
        textView.setOnClickListener {
            val mainActivity = launcherApps.getActivityList(savedView?.get(0).toString(), launcherApps.profiles[savedView?.get(1)!!.toInt()]).firstOrNull()
            if (mainActivity != null) {
                launcherApps.startMainActivity(mainActivity.componentName,  launcherApps.profiles[savedView[1].toInt()], null, null)
            } else {
                Toast.makeText(this, "Cannot launch app", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun setupRecyclerView(newApps: MutableList<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>) {
        adapter = AppMenuAdapter(this@MainActivity, newApps, this@MainActivity, this@MainActivity, this@MainActivity)
        appMenuLinearLayoutManager.stackFromEnd = true
        recyclerView = findViewById(R.id.recycler_view)
        withContext(Dispatchers.Main) {
            recyclerView.layoutManager = appMenuLinearLayoutManager
            recyclerView.edgeEffectFactory = appMenuEdgeFactory
            recyclerView.adapter = adapter
            recyclerView.scrollToPosition(0)
        }

        setupRecyclerListener()
    }

    private fun setupRecyclerListener() {
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    appMenuLinearLayoutManager.setScrollInfo()
                }
            }
        })
    }

    private fun setupSearch() {
        recyclerView.addOnLayoutChangeListener { _, _, top, _, bottom, _, oldTop, _, oldBottom ->

            if (bottom - top > oldBottom - oldTop) {

                searchView.clearFocus()

                if (searchView.text.isNullOrEmpty()) {
                    startTask()
                }
            }
            else if (bottom - top < oldBottom - oldTop) {
                job?.cancel()
            }
        }

        searchView.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                CoroutineScope(Dispatchers.Default).launch {
                    filterItems(searchView.text.toString())
                }
            }
        })
    }

    private suspend fun filterItems(query: String?) {

            val cleanQuery = stringUtils.cleanString(query)
            val newFilteredApps = mutableListOf<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>()
            val updatedApps = appUtils.getInstalledApps(this@MainActivity)

            getFilteredApps(cleanQuery, newFilteredApps, updatedApps)

            applySearch(newFilteredApps)

    }

    private suspend fun getFilteredApps(cleanQuery: String?, newFilteredApps: MutableList<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>, updatedApps: List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>) {
        if (cleanQuery.isNullOrEmpty()) {
            refreshAppMenu()
            newFilteredApps.addAll(installedApps)
        } else {
            updatedApps.forEach {
                val cleanItemText = stringUtils.cleanString(sharedPreferenceManager.getAppName(this@MainActivity, it.first.applicationInfo.packageName, it.second.second, packageManager.getApplicationLabel(it.first.applicationInfo)).toString())
                if (cleanItemText != null) {
                    if (cleanItemText.contains(cleanQuery, ignoreCase = true)) {
                        newFilteredApps.add(it)
                    }
                }
            }
        }
    }

    private suspend fun applySearch(newFilteredApps: MutableList<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>) {
        if (!listsEqual(installedApps, newFilteredApps)) {
            withContext(Dispatchers.Main) {
                updateMenu(newFilteredApps)
            }
            installedApps = newFilteredApps
        }
    }

    private fun startTask() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                refreshAppMenu()
                delay(5000)
            }
        }
    }

    fun openAppMenuActivity() {
        adapter?.menuMode = "app"
        binding.menutitle.visibility = View.GONE
        toAppMenu()
    }
    
    fun backToHome() {
        closeKeyboard()
        animations.showHome(binding)
        animations.backgroundOut(this@MainActivity, Color.parseColor(preferences.getString("bgColor",  "#00000000")))
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            try {
                binding.menutitle.visibility = View.VISIBLE
                searchView.setText("")
            }
            catch (_: UninitializedPropertyAccessException) {

            }
        }, 100)
        handler.postDelayed({
            CoroutineScope(Dispatchers.Default).launch {
                refreshAppMenu()

            try {
                withContext(Dispatchers.Main) {
                    recyclerView.scrollToPosition(0)
                }
            }
            catch (_: UninitializedPropertyAccessException) {

            }
        }}, 150)

    }

    private fun toAppMenu() {
        animations.showApps(binding)
        animations.backgroundIn(this@MainActivity, Color.parseColor(preferences.getString("bgColor",  "#00000000")))
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
        backToHome()
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
        animations.fadeViewIn(actionMenuLayout)
        val mainActivity =
            launcherApps.getActivityList(appInfo.applicationInfo.packageName, userHandle)
                .firstOrNull()
        appActionMenu.setActionListeners(
            this@MainActivity,
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

    suspend fun refreshAppMenu() {
            try {
                val updatedApps = appUtils.getInstalledApps(this@MainActivity)
                println("update running")
                if (!listsEqual(installedApps, updatedApps)) {
                    withContext(Dispatchers.Main) {
                        updateMenu(updatedApps)
                    }
                    installedApps = updatedApps
                }
            }
            catch (_: UninitializedPropertyAccessException) {
            }

        }

    private fun closeKeyboard() {
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun updateMenu(updatedApps : List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>) {
        adapter?.updateApps(updatedApps)
        println("moved")
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

    private fun setClockAlignment(alignment: String?) {

        when (alignment) {
            "left" -> {
                clock.textAlignment = TEXT_ALIGNMENT_TEXT_START
                dateText.textAlignment = TEXT_ALIGNMENT_TEXT_START
            }
            "center" -> {
                clock.textAlignment = TEXT_ALIGNMENT_CENTER
                dateText.textAlignment = TEXT_ALIGNMENT_CENTER
            }
            "right" -> {
                clock.textAlignment = TEXT_ALIGNMENT_TEXT_END
                dateText.textAlignment = TEXT_ALIGNMENT_TEXT_END
            }
        }
    }

    private fun setShortcutAlignment(alignment: String?, shortcuts: LinearLayout) {
        shortcuts.children.forEach {

            if (it is TextView) {


                when (alignment) {
                    "left" -> {
                        it.setCompoundDrawablesWithIntrinsicBounds(it.compoundDrawables.filterNotNull().first(),null, null, null)
                        it.gravity = Gravity.CENTER_VERTICAL or Gravity.START
                    }
                    "center" -> {
                        it.setCompoundDrawablesWithIntrinsicBounds(it.compoundDrawables.filterNotNull().first(),null,it.compoundDrawables.filterNotNull().first(), null)
                        it.gravity = Gravity.CENTER
                    }
                    "right" -> {
                        it.setCompoundDrawablesWithIntrinsicBounds(null,null, it.compoundDrawables.filterNotNull().first(), null)
                        it.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                    }
                }

            }
        }
    }

    private fun setSearchAlignment(alignment: String?) {

        when (alignment) {
            "left" -> {
                searchView.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            }
            "center" -> {
                searchView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
            "right" -> {
                searchView.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
            }
        }
    }

    private fun setShortcutSize(shortcuts: LinearLayout) {

        val viewTreeObserver = shortcuts.viewTreeObserver
        val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                shortcuts.children.forEach {
                    if (it is TextView) {

                        when (preferences.getString("shortcutSize", "medium")) {
                            "small" -> {
                                it.setPadding(
                                    it.paddingLeft,
                                    it.height / 4,
                                    it.paddingRight,
                                    it.height / 4
                                )
                            }

                            "medium" -> {
                                it.setPadding(
                                    it.paddingLeft,
                                    (it.height / 4.5).toInt(),
                                    it.paddingRight,
                                    (it.height / 4.5).toInt()
                                )
                            }

                            "large" -> {
                                it.setPadding(it.paddingLeft, 0, it.paddingRight, 0)
                            }
                        }

                    }
                }
                if (viewTreeObserver.isAlive) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        }

        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
        }
    }

    private fun setClockSize(size: String?) {
        when (size) {
            "small" -> {
                clock.textSize = 48F
            }
            "medium" -> {
                clock.textSize = 58F
            }
            "large" -> {
                clock.textSize = 68F
            }
        }
    }

    private fun setDateSize(size: String?) {
        when (size) {
            "small" -> {
                dateText.textSize = 17F
            }
            "medium" -> {
                dateText.textSize = 20F
            }
            "large" -> {
                dateText.textSize = 23F
            }
        }
    }

    private fun setSearchSize(size: String?) {
        when (size) {
            "small" -> {
                searchView.textSize = 21F
            }
            "medium" -> {
                searchView.textSize = 23F
            }
            "large" -> {
                searchView.textSize = 25F
            }
        }
    }

    fun isJobActive(): Boolean {
        return if (job != null) {
            job!!.isActive
        } else {
            false
        }
    }
}
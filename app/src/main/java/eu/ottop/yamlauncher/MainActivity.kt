package eu.ottop.yamlauncher

import android.content.Context
import android.content.pm.LauncherApps
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import eu.ottop.yamlauncher.databinding.ActivityMainBinding
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gestureDetector: GestureDetector
    private lateinit var launcherApps: LauncherApps
    private val sharedPreferenceManager = SharedPreferenceManager()

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
                AppMenuActivity.start(this@MainActivity, "shortcut") { newText ->

                    if (newText.first.second != 0) {
                        textView.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(resources, R.drawable.ic_work_app, null),null,null,null)
                    }
                    else {
                        textView.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(resources, R.drawable.ic_empty, null),null,null,null)
                    }
                    textView.text = newText.first.first
                    i.setOnClickListener {
                        val mainActivity = launcherApps.getActivityList(newText.second.first.applicationInfo.packageName, newText.second.second).firstOrNull()
                        if (mainActivity != null) {
                            launcherApps.startMainActivity(mainActivity.componentName,  newText.second.second, null, null)
                        } else {
                            Toast.makeText(this, "Cannot launch app", Toast.LENGTH_SHORT).show()
                        }
                    }
                    sharedPreferenceManager.setShortcut(this, textView, newText.second.first.applicationInfo.packageName, newText.first.second)
                }

                return@setOnLongClickListener true
            }
        }

        gestureDetector = GestureDetector(this, GestureListener())

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
        AppMenuActivity.start(this) {
        }
    }

}


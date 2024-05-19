package eu.ottop.yamlauncher

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import eu.ottop.yamlauncher.databinding.ActivityMainBinding
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gestureDetector: GestureDetector
    private lateinit var launcherApps: LauncherApps

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(null)

        launcherApps = getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

        for (i in findViewById<LinearLayout>(R.id.shortcuts).children) {

            var textView = i as TextView

            i.setOnClickListener {
                Log.d("hHJKJFAF", "Click done")
            }

            i.setOnLongClickListener {
                AppMenuActivity.start(this@MainActivity, "shortcut") { newText ->
                    textView.text = newText.first
                    i.setOnClickListener {
                        val mainActivity = launcherApps.getActivityList(newText.second.first.applicationInfo.packageName, newText.second.second).firstOrNull()
                        if (mainActivity != null) {
                            launcherApps.startMainActivity(mainActivity.componentName,  newText.second.second, null, null)
                        } else {
                            Toast.makeText(this, "Cannot launch app", Toast.LENGTH_SHORT).show()
                        }
                    }
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


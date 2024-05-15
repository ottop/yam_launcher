package eu.ottop.yamlauncher

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import eu.ottop.yamlauncher.databinding.ActivityMainBinding
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gestureDetector: GestureDetector
    private val sharedPreferenceManager = SharedPreferenceManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(null)

        for (i in findViewById<LinearLayout>(R.id.shortcuts).children) {
            i.setOnClickListener {
                Log.d("hHJKJFAF", "Click done")
            }
            i.setOnLongClickListener {
                Log.d("hHJKJFAF", "long click done")
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
        startActivity(Intent(this, AppMenuActivity::class.java))
    }

}
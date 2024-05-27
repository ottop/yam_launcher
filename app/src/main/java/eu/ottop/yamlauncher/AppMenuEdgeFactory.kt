package eu.ottop.yamlauncher

import android.os.Handler
import android.os.Looper
import android.widget.EdgeEffect
import androidx.recyclerview.widget.RecyclerView

class AppMenuEdgeFactory(private val activity: MainActivity) : RecyclerView.EdgeEffectFactory() {

    private var isScrollingUp = false
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            isScrollingUp = dy < 0
        }

    }
    override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
        view.addOnScrollListener(scrollListener)
        return AppMenuEdgeEffect(activity)
    }

    inner class AppMenuEdgeEffect(private val activity: MainActivity) : EdgeEffect(activity) {
        private val animationSpeedFactor = 0.5f
        private val pullDistanceThreshold = 0.03f // Set a suitable threshold
        private val debounceInterval = 100L // Milliseconds
        private var lastActionTime = 0L

        override fun onAbsorb(velocity: Int) {
            super.onAbsorb((velocity * animationSpeedFactor).toInt())
        }

        override fun onPull(deltaDistance: Float, displacement: Float) {
            super.onPull(deltaDistance * animationSpeedFactor, displacement)
            if (shouldTriggerAction(deltaDistance) && isScrollingUp) {
                super.onPull(deltaDistance * animationSpeedFactor, displacement)
                if (activity.recyclerAtTop()) {
                    activity.showHome()
                }
            } else {
                super.onPull(deltaDistance * animationSpeedFactor, displacement)
            }
        }

        override fun onPullDistance(deltaDistance: Float, displacement: Float): Float {
            return super.onPullDistance(deltaDistance * animationSpeedFactor, displacement)
        }

        private fun shouldTriggerAction(deltaDistance: Float): Boolean {
            val currentTime = System.currentTimeMillis()
            return deltaDistance > pullDistanceThreshold && (currentTime - lastActionTime > debounceInterval).also {
                if (it) lastActionTime = currentTime
            }
        }
    }
}
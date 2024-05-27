package eu.ottop.yamlauncher

import android.widget.EdgeEffect
import androidx.recyclerview.widget.RecyclerView

class AppMenuEdgeFactory(private val activity: MainActivity) : RecyclerView.EdgeEffectFactory() {

    override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
        return AppMenuEdgeEffect(activity)
    }
}
class AppMenuEdgeEffect(activity: MainActivity) : EdgeEffect(activity) {
    // Adjust the speed here
    private val animationSpeedFactor = 0.5f

    override fun onAbsorb(velocity: Int) {
        super.onAbsorb((velocity * animationSpeedFactor).toInt())
    }

    override fun onPull(deltaDistance: Float, displacement: Float) {
        super.onPull(deltaDistance * animationSpeedFactor, displacement)
    }

    override fun onRelease() {
        super.onRelease()
    }

    override fun onPullDistance(deltaDistance: Float, displacement: Float): Float {
        return super.onPullDistance(deltaDistance * animationSpeedFactor, displacement)
    }
}
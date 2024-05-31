package eu.ottop.yamlauncher

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.app.Activity
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import eu.ottop.yamlauncher.databinding.ActivityMainBinding

class Animations () {

    fun fadeViewIn(view: View, duration: Long = 100) {
        view.fadeIn(duration)
    }

    fun fadeViewOut(view: View, duration: Long = 100) {
        view.fadeOut(duration)
    }
    fun showHome(binding: ActivityMainBinding) {
        binding.appView.slideOutToBottom()
        binding.homeView.fadeIn()
        binding.menutitle.visibility = View.GONE
    }

    fun showApps(binding: ActivityMainBinding) {
        binding.homeView.fadeOut()
        binding.appView.slideInFromBottom()
    }

    fun backgroundIn(activity: Activity, binding: ActivityMainBinding, duration: Long = 100) {
        val originalColor = ContextCompat.getColor(activity, R.color.original_color)
        val newColor = ContextCompat.getColor(activity, R.color.new_color)

        val backgroundColorAnimator: ObjectAnimator = ObjectAnimator.ofObject(
            binding.root,
            "backgroundColor",
            ArgbEvaluator(),
            originalColor,
            newColor
        )

        backgroundColorAnimator.setDuration(duration)

        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        val statusBarColorAnimator = ObjectAnimator.ofArgb(
            window,
            "statusBarColor",
            originalColor,
            newColor
        )
        statusBarColorAnimator.setDuration(duration)
        backgroundColorAnimator.start()
        statusBarColorAnimator.start()
    }

    fun backgroundOut(activity: Activity, binding: ActivityMainBinding, duration: Long = 100) {
        val originalColor = ContextCompat.getColor(activity, R.color.new_color)
        val newColor = ContextCompat.getColor(activity, R.color.original_color)

        val backgroundColorAnimator: ObjectAnimator = ObjectAnimator.ofObject(
            binding.root,
            "backgroundColor",
            ArgbEvaluator(),
            originalColor,
            newColor
        )

        backgroundColorAnimator.setDuration(duration)

        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        val statusBarColorAnimator = ObjectAnimator.ofArgb(
            window,
            "statusBarColor",
            originalColor,
            newColor
        )
        statusBarColorAnimator.setDuration(duration)
        backgroundColorAnimator.start()
        statusBarColorAnimator.start()
    }
    private fun View.slideInFromBottom(duration: Long = 100) {
        if (visibility != View.VISIBLE) {
            translationY = height.toFloat()/5
            scaleY = 1.2f
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .translationY(0f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(duration)
                .setListener(null)
        }
    }

    private fun View.slideOutToBottom(duration: Long = 50) {
        if (visibility == View.VISIBLE) {
            animate()
                .translationY(height.toFloat() / 5)
                .scaleY(1.2f)
                .alpha(0f)
                .setDuration(duration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.INVISIBLE
                    }
                })
        }
    }

    private fun View.fadeIn(duration: Long = 100) {
        if (visibility != View.VISIBLE) {
            alpha = 0f
            translationY = -height.toFloat()/100
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(duration)
                .setListener(null)
        }
    }

    private fun View.fadeOut(duration: Long = 50) {
        if (visibility == View.VISIBLE) {
            animate()
                .alpha(0f)
                .translationY(-height.toFloat()/100)
                .setDuration(duration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.INVISIBLE
                    }
                })}
    }
}
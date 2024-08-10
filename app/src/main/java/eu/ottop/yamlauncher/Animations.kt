package eu.ottop.yamlauncher

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import eu.ottop.yamlauncher.databinding.ActivityMainBinding

class Animations (context: Context) {

    private val sharedPreferenceManager = SharedPreferenceManager(context)

    fun fadeViewIn(view: View) {
        view.fadeIn()
    }

    fun fadeViewOut(view: View) {
        view.fadeOut()
    }
    fun showHome(homeView: View, appView: View) {
        appView.slideOutToBottom()
        homeView.fadeIn()
    }

    fun showApps(homeView: View, appView: View) {
        appView.slideInFromBottom()
        homeView.fadeOut()
    }

    fun backgroundIn(activity: Activity) {
        val originalColor = sharedPreferenceManager.getBgColor()

        val newColor: Int = if (originalColor == Color.parseColor("#00000000")) {
            Color.parseColor("#3F000000")
        } else {
            originalColor
        }

        val colorDrawable = ColorDrawable(originalColor)
        activity.window.setBackgroundDrawable(colorDrawable)

        val backgroundColorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), originalColor, newColor)
        backgroundColorAnimator.addUpdateListener { animator ->
            colorDrawable.color = animator.animatedValue as Int
        }
        val duration = sharedPreferenceManager.getAnimationSpeed()
        backgroundColorAnimator.duration = duration

        backgroundColorAnimator.start()
    }

    fun backgroundOut(activity: Activity) {
        val newColor = sharedPreferenceManager.getBgColor()

        val originalColor: Int = if (newColor == Color.parseColor("#00000000")) {
            Color.parseColor("#3F000000")
        } else {
            newColor
        }

        val colorDrawable = ColorDrawable(originalColor)
        activity.window.setBackgroundDrawable(colorDrawable)

        val backgroundColorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), originalColor, newColor)
        backgroundColorAnimator.addUpdateListener { animator ->
            colorDrawable.color = animator.animatedValue as Int
        }
        val duration = sharedPreferenceManager.getAnimationSpeed()
        backgroundColorAnimator.duration = duration

        backgroundColorAnimator.start()
    }
    private fun View.slideInFromBottom() {
        if (visibility != View.VISIBLE) {
            translationY = height.toFloat()/5
            scaleY = 1.2f
            alpha = 0f
            visibility = View.VISIBLE
            val duration = sharedPreferenceManager.getAnimationSpeed()

            animate()
                    .translationY(0f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(duration)
                    .setListener(null)
        }
    }

    private fun View.slideOutToBottom() {
        if (visibility == View.VISIBLE) {
            val duration = sharedPreferenceManager.getAnimationSpeed()

            animate()
                .translationY(height.toFloat() / 5)
                .scaleY(1.2f)
                .alpha(0f)
                .setDuration(duration/2)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.INVISIBLE
                    }
                })
        }
    }

    private fun View.fadeIn() {
        if (visibility != View.VISIBLE) {
            alpha = 0f
            translationY = -height.toFloat()/100
            visibility = View.VISIBLE
            val duration = sharedPreferenceManager.getAnimationSpeed()

            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(duration)
                .setListener(null)

        }
    }

    private fun View.fadeOut() {
        if (visibility == View.VISIBLE) {
            val duration = sharedPreferenceManager.getAnimationSpeed()

            animate()
                .alpha(0f)
                .translationY(-height.toFloat()/100)
                .setDuration(duration/2)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.GONE
                    }
                })

        }
    }
}
package eu.ottop.yamlauncher

import android.content.SharedPreferences
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.LinearLayout
import android.widget.TextClock
import android.widget.TextView
import androidx.core.view.children
import com.google.android.material.textfield.TextInputEditText

class UIUtils() {

    fun setBackground(window: Window, preferences: SharedPreferences) {
        window.setBackgroundDrawable(ColorDrawable(Color.parseColor("#00000000")))
        window.decorView.setBackgroundColor(
            Color.parseColor(preferences.getString("bgColor",  "#00000000"))
        )
    }

    fun setTextColors(preferences: SharedPreferences, view: View) {
        val color = Color.parseColor(preferences.getString("textColor",  "#FFF3F3F3"))
        when {
            view is ViewGroup -> {
                view.children.forEach { child ->
                    setTextColors(preferences, child)
                }
            }
            hasMethod(view, "setTextColor") -> {
                (view as? TextView)?.setTextColor(color)
            }
            else -> {
                view.setBackgroundColor(color)
            }
        }
    }

    private fun hasMethod(view: View, methodName: String): Boolean {
        return try {
            view.javaClass.getMethod(methodName, Int::class.java)
            true
        } catch (e: NoSuchMethodException) {
            false
        }
    }

    private fun setAlpha(color: Int, alphaHex: String): Int {
        val newAlpha = Integer.parseInt(alphaHex, 16)

        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        return Color.argb(newAlpha, r, g, b)
    }

    fun setSearchColors(preferences: SharedPreferences, searchView: TextInputEditText) {
        val viewTreeObserver = searchView.viewTreeObserver

        val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                searchView.setTextColor(Color.parseColor(preferences.getString("textColor", "#FFF3F3F3")))
                searchView.setHintTextColor(setAlpha(Color.parseColor(preferences.getString("textColor", "#FFF3F3F3")), "A9"))
                searchView.compoundDrawables[0].mutate().colorFilter =
                    BlendModeColorFilter(Color.parseColor(preferences.getString("textColor", "#FFF3F3F3")), BlendMode.SRC_ATOP)

                if (viewTreeObserver.isAlive) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        }

        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
        }
    }

    fun setClockAlignment(preferences: SharedPreferences, clock: TextClock, dateText: TextClock) {
        setTextAlignment(clock, preferences.getString("clockAlignment", "left"))
        setTextAlignment(dateText, preferences.getString("clockAlignment", "left"))
    }

    fun setShortcutAlignment(preferences: SharedPreferences, shortcuts: LinearLayout) {
        shortcuts.children.forEach {

            if (it is TextView) {

                try {
                    when (preferences.getString("shortcutAlignment", "left")) {
                        "left" -> {
                            it.setCompoundDrawablesWithIntrinsicBounds(
                                it.compoundDrawables.filterNotNull().first(), null, null, null
                            )
                            it.gravity = Gravity.CENTER_VERTICAL or Gravity.START
                        }

                        "center" -> {
                            it.setCompoundDrawablesWithIntrinsicBounds(
                                it.compoundDrawables.filterNotNull().first(),
                                null,
                                it.compoundDrawables.filterNotNull().first(),
                                null
                            )
                            it.gravity = Gravity.CENTER
                        }

                        "right" -> {
                            it.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                null,
                                it.compoundDrawables.filterNotNull().first(),
                                null
                            )
                            it.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                        }
                    }
                } catch(_: Exception) {}
            }
        }
    }

    fun setSearchAlignment(preferences: SharedPreferences, searchView: TextInputEditText) {
        setTextAlignment(searchView, preferences.getString("searchAlignment", "left"))
    }

    private fun setTextAlignment(view: TextView, alignment: String?) {
        view.textAlignment = when (alignment) {
            "left" -> View.TEXT_ALIGNMENT_VIEW_START

            "center" -> View.TEXT_ALIGNMENT_CENTER

            "right" -> View.TEXT_ALIGNMENT_VIEW_END

            else -> View.TEXT_ALIGNMENT_VIEW_START
        }
    }

    fun setClockSize(preferences: SharedPreferences, clock: TextClock) {
        setTextSize(clock, preferences.getString("clockSize","medium"), 48F, 58F, 68F)
    }

    fun setDateSize(preferences: SharedPreferences, dateText: TextClock) {
        setTextSize(dateText, preferences.getString("dateSize", "medium"), 17F, 20F, 23F)
    }

    fun setShortcutSize(preferences: SharedPreferences, shortcuts: LinearLayout) {

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

    fun setSearchSize(preferences: SharedPreferences, searchView: TextInputEditText) {
        setTextSize(searchView, preferences.getString("searchSize", "medium"), 21F, 23F, 25F)
    }

    private fun setTextSize(view: TextView, size: String?, s: Float, m: Float, l: Float) {
        view.textSize = when (size) {
            "small" -> s

            "medium" -> m

            "large" -> l

            else -> {0F}
        }
    }

    fun setStatusBar(window: Window, preferences: SharedPreferences) {
        val windowInsetsController = window.insetsController

        windowInsetsController?.let {
            if (preferences.getBoolean("barVisibility", false)) {
                it.show(WindowInsets.Type.statusBars())
            }
            else {
                it.hide(WindowInsets.Type.statusBars())
                it.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

}
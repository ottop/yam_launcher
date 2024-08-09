package eu.ottop.yamlauncher

import android.content.Context
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
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import com.google.android.material.textfield.TextInputEditText

class UIUtils {

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
        val color = Color.parseColor(preferences.getString("textColor", "#FFF3F3F3"))
        val viewTreeObserver = searchView.viewTreeObserver

        val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                searchView.setTextColor(color)
                searchView.setHintTextColor(setAlpha(Color.parseColor(preferences.getString("textColor", "#FFF3F3F3")), "A9"))
                searchView.compoundDrawables[0].mutate().colorFilter =
                    BlendModeColorFilter(color, BlendMode.SRC_ATOP)

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
        val alignment = preferences.getString("clockAlignment", "left")
        setTextAlignment(clock, alignment)
        setTextAlignment(dateText, alignment)
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

    fun setAppAlignment(activity: Context, preferences: SharedPreferences, textView: TextView, editText: TextInputEditText? = null, regionText: TextView? = null) {
        val alignment = preferences.getString("appMenuAlignment", "left")
        setTextGravity(textView, alignment)

        if (regionText != null) {
            setTextGravity(textView, alignment)
            setTextGravity(regionText, alignment)
            return
        }

        when (alignment) {
            "left" -> {
                textView.setCompoundDrawablesWithIntrinsicBounds(textView.compoundDrawables.filterNotNull().first(),null, ResourcesCompat.getDrawable(activity.resources, R.drawable.ic_empty, null), null)
                editText?.gravity = Gravity.CENTER_VERTICAL or Gravity.START

            }
            "center" -> {
                textView.setCompoundDrawablesWithIntrinsicBounds(textView.compoundDrawables.filterNotNull().first(),null, textView.compoundDrawables.filterNotNull().first(), null)
                editText?.gravity = Gravity.CENTER_VERTICAL or Gravity.END

            }
            "right" -> {
                textView.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(activity.resources, R.drawable.ic_empty, null),null, textView.compoundDrawables.filterNotNull().first(), null)
                editText?.gravity = Gravity.CENTER_VERTICAL or Gravity.END
            }
        }

    }

    fun setSearchAlignment(preferences: SharedPreferences, searchView: TextInputEditText) {
        setTextAlignment(searchView, preferences.getString("searchAlignment", "left"))
    }

    fun setMenuTitleAlignment(preferences: SharedPreferences, menuTitle: TextView) {
        setTextGravity(menuTitle, preferences.getString("appMenuAlignment", "left"))

    }

    private fun setTextAlignment(view: TextView, alignment: String?) {
        view.textAlignment = when (alignment) {
            "left" -> View.TEXT_ALIGNMENT_VIEW_START

            "center" -> View.TEXT_ALIGNMENT_CENTER

            "right" -> View.TEXT_ALIGNMENT_VIEW_END

            else -> View.TEXT_ALIGNMENT_VIEW_START
        }
    }

    private fun setTextGravity(view: TextView, alignment: String?) {
        view.gravity = when (alignment) {
            "left" -> Gravity.CENTER_VERTICAL or Gravity.START

            "center" -> Gravity.CENTER

            "right" -> Gravity.CENTER_VERTICAL or Gravity.END

            else -> Gravity.CENTER_VERTICAL or Gravity.START
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

    fun setAppSize(preferences: SharedPreferences, textView: TextView, editText: TextInputEditText? = null, regionText: TextView? = null) {
        val size = preferences.getString("appMenuSize", "medium")
        setTextSize(textView, size, 24F, 26F, 28F)
        if (editText != null) {
            setTextSize(editText, size, 24F, 26F, 28F)
        }
        if (regionText != null) {
            setTextSize(regionText, size, 14F, 16F, 18F)
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
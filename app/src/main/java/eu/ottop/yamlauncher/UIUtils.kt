package eu.ottop.yamlauncher

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
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

class UIUtils(context: Context) {

    private val sharedPreferenceManager = SharedPreferenceManager(context)

    fun setBackground(window: Window) {
        window.decorView.setBackgroundColor(
            sharedPreferenceManager.getBgColor()
        )
    }

    fun setTextColors(view: View) {
        val color = sharedPreferenceManager.getTextColor()
        when {
            view is ViewGroup -> {
                view.children.forEach { child ->
                    setTextColors(child)
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

    fun setSearchColors(searchView: TextInputEditText) {
        val viewTreeObserver = searchView.viewTreeObserver

        val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val color = sharedPreferenceManager.getTextColor()
                searchView.setTextColor(color)
                searchView.setHintTextColor(setAlpha(color, "A9"))
                searchView.compoundDrawables[0].mutate().colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)

                if (viewTreeObserver.isAlive) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        }

        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
        }
    }

    fun setClockAlignment(clock: TextClock, dateText: TextClock) {
        val alignment = sharedPreferenceManager.getClockAlignment()
        setTextAlignment(clock, alignment)
        setTextAlignment(dateText, alignment)
    }

    fun setShortcutsAlignment(shortcuts: LinearLayout) {
        val alignment = sharedPreferenceManager.getShortcutAlignment()
        shortcuts.children.forEach {
            if (it is TextView) {
                setTextGravity(it, alignment)
                setDrawables(it, alignment)
            }
        }
    }


    fun setDrawables(shortcut: TextView, alignment: String?) {
        try {
            when (alignment) {
                "left" -> {
                    shortcut.setCompoundDrawablesWithIntrinsicBounds(
                        shortcut.compoundDrawables.filterNotNull().first(),
                        null,
                        null,
                        null
                    )
                }

                "center" -> {
                    shortcut.setCompoundDrawablesWithIntrinsicBounds(
                        shortcut.compoundDrawables.filterNotNull().first(),
                        null,
                        shortcut.compoundDrawables.filterNotNull().first(),
                        null
                    )
                }

                "right" -> {
                    shortcut.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        shortcut.compoundDrawables.filterNotNull().first(),
                        null
                    )
                }
            }
        } catch (_: Exception) {}
    }

    fun setAppAlignment(
        textView: TextView,
        editText: TextView? = null,
        regionText: TextView? = null
    ) {
        val alignment = sharedPreferenceManager.getAppAlignment()
        setTextGravity(textView, alignment)

        if (regionText != null) {
            setTextGravity(regionText, alignment)
            return
        }

        if (editText != null) {
            setDrawables(textView, alignment)
            setTextGravity(editText, alignment)
        }

    }

    fun setSearchAlignment(searchView: TextInputEditText) {
        setTextAlignment(searchView, sharedPreferenceManager.getSearchAlignment())
    }

    fun setMenuTitleAlignment(menuTitle: TextView) {
        setTextGravity(menuTitle, sharedPreferenceManager.getAppAlignment())

    }

    private fun setTextAlignment(view: TextView, alignment: String?) {
        try {
            view.textAlignment = when (alignment) {
            "left" -> View.TEXT_ALIGNMENT_VIEW_START

            "center" -> View.TEXT_ALIGNMENT_CENTER

            "right" -> View.TEXT_ALIGNMENT_VIEW_END

            else -> View.TEXT_ALIGNMENT_VIEW_START
            }
        } catch (_: Exception) {}
    }

    private fun setTextGravity(view: TextView, alignment: String?) {
        try {
            view.gravity = when (alignment) {
                "left" -> Gravity.CENTER_VERTICAL or Gravity.START

                "center" -> Gravity.CENTER

                "right" -> Gravity.CENTER_VERTICAL or Gravity.END

                else -> Gravity.CENTER_VERTICAL or Gravity.START
            }
        } catch (_: Exception) {}
    }

    fun setClockSize(clock: TextClock) {
        setTextSize(clock, sharedPreferenceManager.getClockSize(), 58F, 68F, 78F)
    }

    fun setDateSize(dateText: TextClock) {
        setTextSize(dateText, sharedPreferenceManager.getDateSize(), 17F, 20F, 23F)
    }

    fun setShortcutsSize(shortcuts: LinearLayout) {
        val viewTreeObserver = shortcuts.viewTreeObserver
        val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                val size = sharedPreferenceManager.getShortcutSize()

                shortcuts.children.forEach {
                    if (it is TextView) {
                        setShortcutSize(it, size)

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

    private fun setShortcutSize(shortcut: TextView, size: String?) {
        try {
            val layoutParams = shortcut.layoutParams as LinearLayout.LayoutParams
            when (size) {
                "small" -> {
                    layoutParams.weight = 0.08F
                }

                "medium" -> {
                   layoutParams.weight = 0.092F
                }

                "large" -> {
                    layoutParams.weight = 0.1F
                }

                "extra" -> {
                    layoutParams.weight = 0.12F
                }
            }
            shortcut.layoutParams = layoutParams
        } catch(_: Exception) {}
    }

    fun setAppSize(
        textView: TextView,
        editText: TextInputEditText? = null,
        regionText: TextView? = null
    ) {
        val size = sharedPreferenceManager.getAppSize()
        setTextSize(textView, size, 24F, 26F, 30F)
        if (editText != null) {
            setTextSize(editText, size, 24F, 26F, 30F)
        }
        if (regionText != null) {
            setTextSize(regionText, size, 14F, 16F, 20F)
        }
    }

    fun setSearchSize(searchView: TextInputEditText) {
        setTextSize(searchView, sharedPreferenceManager.getSearchSize(), 21F, 23F, 27F)
    }

    private fun setTextSize(view: TextView, size: String?, s: Float, m: Float, l: Float) {
        try {
            view.textSize = when (size) {
                "small" -> s

                "medium" -> m

                "large" -> l

                else -> {
                    0F
                }
            }
        } catch (_: Exception) {}
    }

    fun setStatusBar(window: Window) {
        val windowInsetsController = window.insetsController

        windowInsetsController?.let {
            if (sharedPreferenceManager.isBarVisible()) {
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
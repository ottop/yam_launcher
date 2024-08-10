package eu.ottop.yamlauncher

import android.content.Context
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

class UIUtils(private val context: Context) {

    private val sharedPreferenceManager = SharedPreferenceManager(context)

    fun setBackground(window: Window) {
        window.decorView.background = ColorDrawable(Color.parseColor("#00000000"))

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

    fun setShortcutAlignment(shortcuts: LinearLayout) {
        shortcuts.children.forEach {

            if (it is TextView) {

                try {
                    when (sharedPreferenceManager.getShortcutAlignment()) {
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

    fun setAppAlignment(
        textView: TextView,
        editText: TextInputEditText? = null,
        regionText: TextView? = null
    ) {
        val alignment = sharedPreferenceManager.getAppAlignment()
        setTextGravity(textView, alignment)

        if (regionText != null) {
            setTextGravity(textView, alignment)
            setTextGravity(regionText, alignment)
            return
        }

        when (alignment) {
            "left" -> {
                textView.setCompoundDrawablesWithIntrinsicBounds(textView.compoundDrawables.filterNotNull().first(),null, ResourcesCompat.getDrawable(context.resources, R.drawable.ic_empty, null), null)
                editText?.gravity = Gravity.CENTER_VERTICAL or Gravity.START

            }
            "center" -> {
                textView.setCompoundDrawablesWithIntrinsicBounds(textView.compoundDrawables.filterNotNull().first(),null, textView.compoundDrawables.filterNotNull().first(), null)
                editText?.gravity = Gravity.CENTER_VERTICAL or Gravity.END

            }
            "right" -> {
                textView.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_empty, null),null, textView.compoundDrawables.filterNotNull().first(), null)
                editText?.gravity = Gravity.CENTER_VERTICAL or Gravity.END
            }
        }

    }

    fun setSearchAlignment(searchView: TextInputEditText) {
        setTextAlignment(searchView, sharedPreferenceManager.getSearchAlignment())
    }

    fun setMenuTitleAlignment(menuTitle: TextView) {
        setTextGravity(menuTitle, sharedPreferenceManager.getAppAlignment())

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

    fun setClockSize(clock: TextClock) {
        setTextSize(clock, sharedPreferenceManager.getClockSize(), 48F, 58F, 68F)
    }

    fun setDateSize(dateText: TextClock) {
        setTextSize(dateText, sharedPreferenceManager.getDateSize(), 17F, 20F, 23F)
    }

    fun setShortcutSize(shortcuts: LinearLayout) {

        val viewTreeObserver = shortcuts.viewTreeObserver
        val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                shortcuts.children.forEach {
                    if (it is TextView) {

                        when (sharedPreferenceManager.getShortcutSize()) {
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

    fun setAppSize(
        textView: TextView,
        editText: TextInputEditText? = null,
        regionText: TextView? = null
    ) {
        val size = sharedPreferenceManager.getAppSize()
        setTextSize(textView, size, 24F, 26F, 28F)
        if (editText != null) {
            setTextSize(editText, size, 24F, 26F, 28F)
        }
        if (regionText != null) {
            setTextSize(regionText, size, 14F, 16F, 18F)
        }
    }

    fun setSearchSize(searchView: TextInputEditText) {
        setTextSize(searchView, sharedPreferenceManager.getSearchSize(), 21F, 23F, 25F)
    }

    private fun setTextSize(view: TextView, size: String?, s: Float, m: Float, l: Float) {
        view.textSize = when (size) {
            "small" -> s

            "medium" -> m

            "large" -> l

            else -> {0F}
        }
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
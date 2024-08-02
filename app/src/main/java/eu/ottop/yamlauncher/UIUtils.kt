package eu.ottop.yamlauncher

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children

class UIUtils {

    fun setAllColors(view: View, color: Int) {
        when {
            view is ViewGroup -> {
                view.children.forEach { child ->
                    setAllColors(child, color)
                }
            }
            hasMethod(view, "setTextColor") -> {
                // Check if the method setTextColor exists
                (view as? TextView)?.setTextColor(color)
            }
            else -> {
                view.setBackgroundColor(color)
            }
        }
    }

    // Helper function to check if a view has a method
    private fun hasMethod(view: View, methodName: String): Boolean {
        return try {
            view.javaClass.getMethod(methodName, Int::class.java)
            true
        } catch (e: NoSuchMethodException) {
            false
        }
    }

    private fun setAlpha(color: Int, newAlpha: Int): Int {
        // Extract the RGB components
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        // Combine the new alpha with the RGB components
        return Color.argb(newAlpha, r, g, b)
    }

    fun setAlpha(color: Int, alphaHex: String): Int {
        val newAlpha = Integer.parseInt(alphaHex, 16) // Convert hex alpha to integer
        return setAlpha(color, newAlpha)
    }
}
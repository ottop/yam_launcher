package eu.ottop.yamlauncher.widgets

import android.appwidget.AppWidgetProviderInfo
import android.graphics.drawable.Drawable

data class WidgetItem(
    val label: String,
    val icon: Drawable?,
    val widgetInfo: AppWidgetProviderInfo
)
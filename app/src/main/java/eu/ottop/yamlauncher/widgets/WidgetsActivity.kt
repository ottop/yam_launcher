package eu.ottop.yamlauncher.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eu.ottop.yamlauncher.R
import eu.ottop.yamlauncher.databinding.ActivityMainBinding
import eu.ottop.yamlauncher.databinding.ActivityWidgetsBinding

class WidgetsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWidgetsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWidgetsBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.button.setOnClickListener {
            showWidgetSelectionRecyclerDialog(this) {println("hi")}
        }

    }

    fun showWidgetSelectionRecyclerDialog(context: Context, onWidgetSelected: (AppWidgetProviderInfo) -> Unit) {
        val dialog = AlertDialog.Builder(context).create()
        val recyclerView = RecyclerView(context).apply {
            layoutManager = GridLayoutManager(this@WidgetsActivity, 2)
            adapter = WidgetAdapter(prepareWidgetItems(context), onWidgetSelected)
        }
        dialog.setView(recyclerView)
        dialog.show()
    }

    fun prepareWidgetItems(context: Context): List<WidgetItem> {
        val widgets = getAvailableWidgets(context)
        return widgets.map { widgetInfo ->
            val icon = widgetInfo.loadPreviewImage(this, DisplayMetrics.DENSITY_DEFAULT) ?: widgetInfo.loadIcon(this, DisplayMetrics.DENSITY_DEFAULT)
            WidgetItem(widgetInfo.loadLabel(packageManager), icon, widgetInfo)
        }
    }

    fun getAvailableWidgets(context: Context): List<AppWidgetProviderInfo> {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        return appWidgetManager.installedProviders
    }
}
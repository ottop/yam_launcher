package eu.ottop.yamlauncher.widgets

import android.appwidget.AppWidgetProviderInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eu.ottop.yamlauncher.R

class WidgetAdapter(
    private val widgetItems: List<WidgetItem>,
    private val onWidgetSelected: (AppWidgetProviderInfo) -> Unit
) : RecyclerView.Adapter<WidgetAdapter.WidgetViewHolder>() {

    inner class WidgetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconView: ImageView = view.findViewById(R.id.widget_icon)
        val labelView: TextView = view.findViewById(R.id.widget_label)

        fun bind(item: WidgetItem) {
            iconView.setImageDrawable(item.icon)
            labelView.text = item.label
            itemView.setOnClickListener { onWidgetSelected(item.widgetInfo) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.widget_item, parent, false)
        return WidgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        holder.bind(widgetItems[position])
    }

    override fun getItemCount() = widgetItems.size
}
package eu.ottop.yamlauncher

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.LauncherActivityInfo
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView

class LocationListAdapter(
    activity: Context,
    var apps: MutableList<Map<String, String>>,
    private val itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<LocationListAdapter.AppViewHolder>() {

    private var preferences = PreferenceManager.getDefaultSharedPreferences(activity)

    interface OnItemClickListener {
        fun onItemClick(name: String?, latitude: String?, longitude: String?)
    }

    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val listItem: ConstraintLayout = itemView.findViewById(R.id.location_place)
        val textView: TextView = listItem.findViewById(R.id.location_name)
        val regionText: TextView = listItem.findViewById(R.id.region_name)

        init {

            listItem.setOnClickListener {
                val position = bindingAdapterPosition
                val name = apps[position]["name"]
                val latitude = apps[position]["latitude"]
                val longitude = apps[position]["longitude"]
                itemClickListener.onItemClick(name, latitude, longitude)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_item_layout, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]

        when (preferences.getString("appMenuAlignment", "left")) {
            "left" -> {
                holder.textView.gravity = Gravity.CENTER_VERTICAL or Gravity.START
                holder.regionText.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            }
            "center" -> {
                holder.textView.gravity = Gravity.CENTER
                holder.regionText.gravity = Gravity.CENTER


            }
            "right" -> {
                holder.textView.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                holder.regionText.gravity = Gravity.CENTER_VERTICAL or Gravity.END
            }
        }

        when (preferences.getString("appMenuSize", "medium")) {
            "small" -> {
                holder.textView.textSize = 24F
                holder.regionText.textSize = 14F
            }

            "medium" -> {
                holder.textView.textSize = 26F
                holder.regionText.textSize = 16F
            }

            "large" -> {
                holder.textView.textSize = 28F
                holder.regionText.textSize = 18F
            }
        }

        holder.textView.text = app["name"]
        holder.regionText.text = "${app["region"]}${app["country"]}"

        holder.textView.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateApps(newApps: MutableList<Map<String, String>>) {
        apps.clear()
        apps = newApps
        notifyDataSetChanged()
    }
}
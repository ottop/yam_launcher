package eu.ottop.yamlauncher

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView

class LocationListAdapter(
    private val activity: Context,
    private var apps: MutableList<Map<String, String>>,
    private val itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<LocationListAdapter.AppViewHolder>() {

    private var preferences = PreferenceManager.getDefaultSharedPreferences(activity)
    private val uiUtils = UIUtils()

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

        uiUtils.setAppAlignment(activity, preferences, holder.textView, null ,holder.regionText)

        uiUtils.setAppSize(preferences, holder.textView, null, holder.regionText)

        holder.textView.text = app["name"]
        holder.regionText.text = activity.getString(R.string.region_text, app["region"], app["country"])

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
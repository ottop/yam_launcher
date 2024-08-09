package eu.ottop.yamlauncher

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.LauncherActivityInfo
import android.os.UserHandle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView

class HiddenAppsAdapter(
    private val activity: Context,
    private var apps: MutableList<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>,
    private val itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<HiddenAppsAdapter.AppViewHolder>() {

        private val sharedPreferenceManager = SharedPreferenceManager()
        private var preferences = PreferenceManager.getDefaultSharedPreferences(activity)

        private val uiUtils = UIUtils()

    interface OnItemClickListener {
        fun onItemClick(appInfo: LauncherActivityInfo, profile: Int)
    }

    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val listItem: FrameLayout = itemView.findViewById(R.id.list_item)
        val textView: TextView = listItem.findViewById(R.id.app_name)

        init {

            textView.setOnClickListener {
                val position = bindingAdapterPosition
                val app = apps[position].first
                itemClickListener.onItemClick(app, apps[position].second.second)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.app_item_layout, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]

        if (app.second.second != 0) {
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(activity.resources, R.drawable.ic_work_app, null),null,null,null)
        }
        else {
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(activity.resources, R.drawable.ic_empty, null),null,null,null)
        }

        uiUtils.setAppAlignment(activity, preferences, holder.textView)

        uiUtils.setAppSize(preferences, holder.textView)

        val appInfo = app.first.activityInfo.applicationInfo
        holder.textView.text = sharedPreferenceManager.getAppName(activity, app.first.applicationInfo.packageName,app.second.second, holder.itemView.context.packageManager.getApplicationLabel(appInfo))

        holder.textView.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateApps(newApps: List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>) {
        apps = newApps.toMutableList()
        notifyDataSetChanged()
    }
}
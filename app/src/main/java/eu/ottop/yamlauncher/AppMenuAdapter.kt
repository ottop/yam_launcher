package eu.ottop.yamlauncher

import android.content.pm.LauncherActivityInfo
import android.os.UserHandle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppMenuAdapter(private var apps: List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>, private val itemClickListener: OnItemClickListener, private val itemLongClickListener: OnItemLongClickListener) :
    RecyclerView.Adapter<AppMenuAdapter.AppViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(appInfo: LauncherActivityInfo, userHandle: UserHandle)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(
            appInfo: LauncherActivityInfo,
            userHandle: UserHandle,
            userProfile: Int,
            textView: TextView,
            actionMenuLayout: LinearLayout,
            editView: LinearLayout
        )
    }

    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val listItem: FrameLayout = itemView.findViewById(R.id.list_item)
        val textView: TextView = listItem.findViewById(R.id.app_name)
        private val actionMenuLayout: LinearLayout = listItem.findViewById(R.id.action_menu)
        val editView: LinearLayout = listItem.findViewById(R.id.rename_view)

        init {
            actionMenuLayout.visibility = View.INVISIBLE
            editView.visibility = View.INVISIBLE

            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val app = apps[position].first
                    itemClickListener.onItemClick(app, apps[position].second.first)
                }
            }
            itemView.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val app = apps[position].first
                    itemLongClickListener.onItemLongClick(app, apps[position].second.first, apps[position].second.second, textView, actionMenuLayout, editView)
                    return@setOnLongClickListener true
                }
                false
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
        val appInfo = app.first.activityInfo.applicationInfo
        holder.textView.text = appInfo.loadLabel(holder.itemView.context.packageManager)
        holder.editView.findViewById<EditText>(R.id.app_name_edit).setText(holder.textView.text)
        holder.textView.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    fun updateApps(newApps: List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>) {
        apps = newApps
        notifyDataSetChanged()
    }
}
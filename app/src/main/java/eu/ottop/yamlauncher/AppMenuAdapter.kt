package eu.ottop.yamlauncher

import android.content.Context
import android.content.pm.LauncherActivityInfo
import android.os.UserHandle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView

class AppMenuAdapter(
    private val activity: AppMenuActivity,
    var apps: MutableList<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>,
    private val itemClickListener: OnItemClickListener,
    private val shortcutListener: OnShortcutListener,
    private val itemLongClickListener: OnItemLongClickListener,
    private val menuMode: String
) :
    RecyclerView.Adapter<AppMenuAdapter.AppViewHolder>() {

        private val sharedPreferenceManager = SharedPreferenceManager()

    interface OnItemClickListener {
        fun onItemClick(appInfo: LauncherActivityInfo, userHandle: UserHandle)
    }

    interface OnShortcutListener {
        fun onShortcut(appInfo: LauncherActivityInfo, userHandle: UserHandle, textView: TextView, userProfile: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(
            appInfo: LauncherActivityInfo,
            userHandle: UserHandle,
            userProfile: Int,
            textView: TextView,
            actionMenuLayout: LinearLayout,
            editView: LinearLayout,
            position: Int
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

            textView.setOnClickListener {
                    val position = bindingAdapterPosition
                    val app = apps[position].first
                    if (menuMode == "shortcut") {
                        shortcutListener.onShortcut(app, apps[position].second.first, textView, apps[position].second.second)
                    }
                    else if (menuMode == "app") {
                        itemClickListener.onItemClick(app, apps[position].second.first)
                    }
            }
            if (menuMode == "app") {
                textView.setOnLongClickListener {
                        val position = bindingAdapterPosition

                        val app = apps[position].first
                        itemLongClickListener.onItemLongClick(
                            app,
                            apps[position].second.first,
                            apps[position].second.second,
                            textView,
                            actionMenuLayout,
                            editView,
                            position
                        )
                        return@setOnLongClickListener true
                    }


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

        holder.textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

        if (app.second.second != 0) {
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(activity.resources, R.drawable.ic_work_app, null),null,null,null)
        }
        else {
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(activity.resources, R.drawable.ic_empty, null),null,null,null)
        }
        holder.textView.compoundDrawablePadding = 0

        val appInfo = app.first.activityInfo.applicationInfo
        holder.textView.text = sharedPreferenceManager.getAppName(activity, app.first.applicationInfo.packageName,app.second.second, appInfo.loadLabel(holder.itemView.context.packageManager))
        holder.editView.findViewById<EditText>(R.id.app_name_edit).setText(holder.textView.text)
        holder.textView.visibility = View.VISIBLE
        holder.editView.findViewById<AppCompatButton>(R.id.reset).setOnClickListener {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(holder.editView.windowToken, 0)
            sharedPreferenceManager.resetAppName(activity, app.first.applicationInfo.packageName, app.second.second)
            activity.updateItem(position, app)
        }
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    fun addApp(position: Int, app: Pair<LauncherActivityInfo, Pair<UserHandle, Int>>) {
        apps.add(position, app)
    }

    fun removeApp(position: Int) {
        apps.removeAt(position)
    }

    fun updateApp(position: Int, app: Pair<LauncherActivityInfo, Pair<UserHandle, Int>>) {
        apps[position] = app
    }
}
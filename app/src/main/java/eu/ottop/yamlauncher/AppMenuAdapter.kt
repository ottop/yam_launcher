package eu.ottop.yamlauncher

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherActivityInfo
import android.os.UserHandle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginLeft
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView

class AppMenuAdapter(
    private val activity: Context,
    var apps: MutableList<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>,
    private val itemClickListener: OnItemClickListener,
    private val shortcutListener: OnShortcutListener,
    private val itemLongClickListener: OnItemLongClickListener
) :
    RecyclerView.Adapter<AppMenuAdapter.AppViewHolder>() {

        var menuMode: String = "app"
        var shortcutTextView: TextView? = null

        private val sharedPreferenceManager = SharedPreferenceManager()
        private var preferences = PreferenceManager.getDefaultSharedPreferences(activity)

    interface OnItemClickListener {
        fun onItemClick(appInfo: LauncherActivityInfo, userHandle: UserHandle)
    }

    interface OnShortcutListener {
        fun onShortcut(appInfo: LauncherActivityInfo, userHandle: UserHandle, textView: TextView, userProfile: Int, shortcutView: TextView)
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
        val actionMenuLayout: LinearLayout = listItem.findViewById(R.id.action_menu)
        private val editView: LinearLayout = listItem.findViewById(R.id.rename_view)
        val editText: EditText = editView.findViewById(R.id.app_name_edit)

        init {
            actionMenuLayout.visibility = View.INVISIBLE
            editView.visibility = View.INVISIBLE

            textView.setOnClickListener {
                    val position = bindingAdapterPosition
                    val app = apps[position].first
                    if (menuMode == "shortcut") {
                        shortcutListener.onShortcut(app, apps[position].second.first, textView, apps[position].second.second, shortcutTextView!!)
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

        if (app.second.second != 0) {
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(activity.resources, R.drawable.ic_work_app, null),null,null,null)
        }
        else {
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(activity.resources, R.drawable.ic_empty, null),null,null,null)
        }

        when (preferences.getString("appMenuAlignment", "left")) {
            "left" -> {
                holder.textView.setCompoundDrawablesWithIntrinsicBounds(holder.textView.compoundDrawables.filterNotNull().first(),null, null, null)
                holder.textView.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            }
            "center" -> {
                holder.textView.setCompoundDrawablesWithIntrinsicBounds(holder.textView.compoundDrawables.filterNotNull().first(),null,holder.textView.compoundDrawables.filterNotNull().first(), null)
                holder.textView.gravity = Gravity.CENTER

            }
            "right" -> {
                holder.textView.setCompoundDrawablesWithIntrinsicBounds(null,null, holder.textView.compoundDrawables.filterNotNull().first(), null)
                holder.textView.gravity = Gravity.CENTER_VERTICAL or Gravity.END
            }
        }

        when (preferences.getString("appMenuSize", "medium")) {
            "small" -> {
                holder.textView.textSize = 24F
                holder.editText.textSize = 24F
            }

            "medium" -> {
                holder.textView.textSize = 26F
                holder.editText.textSize = 26F
            }

            "large" -> {
                holder.textView.textSize = 28F
                holder.editText.textSize = 28F
            }
        }

        val appInfo = app.first.activityInfo.applicationInfo
        holder.textView.text = sharedPreferenceManager.getAppName(activity, app.first.applicationInfo.packageName,app.second.second, holder.itemView.context.packageManager.getApplicationLabel(appInfo))
        holder.editText.setText(holder.textView.text)

        if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
            holder.actionMenuLayout.findViewById<TextView>(R.id.uninstall).visibility = View.GONE
        }
        else {
            holder.actionMenuLayout.findViewById<TextView>(R.id.uninstall).visibility = View.VISIBLE
        }

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
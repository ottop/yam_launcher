package eu.ottop.yamlauncher

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.os.UserHandle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import eu.ottop.yamlauncher.databinding.ActivityMainBinding
import eu.ottop.yamlauncher.settings.SharedPreferenceManager
import eu.ottop.yamlauncher.utils.AppUtils
import eu.ottop.yamlauncher.utils.UIUtils


class AppMenuAdapter(
    private val activity: MainActivity,
    binding: ActivityMainBinding,
    private var apps: MutableList<Triple<LauncherActivityInfo, UserHandle, Int>>,
    private val itemClickListener: OnItemClickListener,
    private val shortcutListener: OnShortcutListener,
    private val itemLongClickListener: OnItemLongClickListener,
    private val launcherApps: LauncherApps
) :
    RecyclerView.Adapter<AppMenuAdapter.AppViewHolder>() {

        // If the menu is opened to select shortcuts, the below variable is set
        var shortcutTextView: TextView? = null

        private val sharedPreferenceManager = SharedPreferenceManager(activity)
        private val uiUtils = UIUtils(activity)
        private val appUtils = AppUtils(activity, launcherApps)
        private var appActionMenu = AppActionMenu(activity, binding, launcherApps, activity.findViewById(R.id.searchView))

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
        private val listItem: FrameLayout = itemView.findViewById(R.id.listItem)
        val textView: TextView = listItem.findViewById(R.id.appName)
        val actionMenuLayout: LinearLayout = listItem.findViewById(R.id.actionMenu)
        val editView: LinearLayout = listItem.findViewById(R.id.renameView)
        val editText: TextInputEditText = editView.findViewById(R.id.appNameEdit)

        init {
            textView.setOnClickListener {
                    val position = bindingAdapterPosition
                    val app = apps[position].first

                    // If opened to select a shortcut, set the shortcut instead of launching the app
                    if (shortcutTextView != null) {
                        shortcutListener.onShortcut(app, apps[position].second, textView, apps[position].third, shortcutTextView!!)
                    }
                    else {
                        itemClickListener.onItemClick(app, apps[position].second)
                    }
            }

            textView.setOnLongClickListener {
                val position = bindingAdapterPosition

                val app = apps[position].first

                // If opened to select a shortcut, set the shortcut instead of opening the action menu
                if (shortcutTextView != null) {
                    shortcutListener.onShortcut(app, apps[position].second, textView, apps[position].third, shortcutTextView!!)
                    return@setOnLongClickListener true
                } else {

                itemLongClickListener.onItemLongClick(
                    app,
                    apps[position].second,
                    apps[position].third,
                    textView,
                    actionMenuLayout,
                    editView,
                    position
                )
                return@setOnLongClickListener true
            }}

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.app_item_layout, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.actionMenuLayout.visibility = View.INVISIBLE
        holder.editView.visibility = View.INVISIBLE
        val app = apps[position]

        // Set initial drawables
        if (app.third != 0) {
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(activity.resources, R.drawable.ic_work_app, null),null, ResourcesCompat.getDrawable(activity.resources, R.drawable.ic_empty, null),null)
            holder.textView.compoundDrawables[0].colorFilter =
                BlendModeColorFilter(sharedPreferenceManager.getTextColor(), BlendMode.SRC_ATOP)
        }
        else {
            holder.textView.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(activity.resources, R.drawable.ic_empty, null),null,ResourcesCompat.getDrawable(activity.resources, R.drawable.ic_empty, null),null)
        }

        uiUtils.setAppAlignment(holder.textView, holder.editText)

        uiUtils.setAppSize(holder.textView, holder.editText)

        uiUtils.setAppSpacing(holder.textView)

        // Update the application information (allows updating apps to work)
        val appInfo = appUtils.getAppInfo(
            app.first.applicationInfo.packageName,
            app.third
        )

        holder.textView.setTextColor(sharedPreferenceManager.getTextColor())

        // Set app name on the menu. If the app has been uninstalled, replace it with "Removing" until the app menu updates.
        val appLabel: CharSequence = appInfo?.let { activity.packageManager.getApplicationLabel(it) } ?: "Removing..."

        if (appInfo != null) {
            holder.textView.text = sharedPreferenceManager.getAppName(
                appInfo.packageName,
                app.third,
                appLabel
            )

            holder.editText.setText(holder.textView.text)

            // Remove the uninstall icon for system apps
            if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                holder.actionMenuLayout.findViewById<TextView>(R.id.uninstall).visibility =
                    View.GONE
            } else {
                holder.actionMenuLayout.findViewById<TextView>(R.id.uninstall).visibility =
                    View.VISIBLE
            }
        }
        else {holder.textView.text = appLabel}

        holder.textView.visibility = View.VISIBLE

        if (appInfo != null) {

            val appActivity = launcherApps.getActivityList(appInfo.packageName, app.second).firstOrNull()

            appActionMenu.setActionListeners(
                holder.textView,
                holder.editView,
                holder.actionMenuLayout,
                appInfo,
                app.second,
                app.third,
                appActivity
            )
        }
        ViewCompat.addAccessibilityAction(holder.textView, "Close App Menu") { _, _ ->
            activity.backToHome()
            true
        }
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateApps(newApps: List<Triple<LauncherActivityInfo, UserHandle, Int>>) {
        apps = newApps.toMutableList()
        notifyDataSetChanged()
    }
}
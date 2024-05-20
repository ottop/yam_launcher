package eu.ottop.yamlauncher

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.UserHandle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import eu.ottop.yamlauncher.databinding.ActivityAppMenuBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AppActionMenu {

    private val sharedPreferenceManager = SharedPreferenceManager()

    fun setActionListeners(
        activity: AppMenuActivity,
        uiScope: CoroutineScope,
        binding: ActivityAppMenuBinding,
        textView: TextView,
        editLayout: LinearLayout,
        actionMenu: View,
        searchView: EditText,
        appInfo: ApplicationInfo,
        userHandle: UserHandle,
        workProfile: Int,
        launcherApps: LauncherApps,
        mainActivity: LauncherActivityInfo?,
        position: Int
    ){

        actionMenu.findViewById<TextView>(R.id.info).setOnClickListener {
            if (mainActivity != null) {
                launcherApps.startAppDetailsActivity(
                    mainActivity.componentName,
                    userHandle,
                    null,
                    null
                )
            }

            actionMenu.visibility = View.INVISIBLE
            textView.visibility = View.VISIBLE
        }

        actionMenu.findViewById<TextView>(R.id.uninstall).setOnClickListener {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:${appInfo.packageName}")
            intent.putExtra(Intent.EXTRA_USER, userHandle)
            activity.startActivity(intent)

            actionMenu.visibility = View.INVISIBLE
            textView.visibility = View.VISIBLE
        }

        actionMenu.findViewById<TextView>(R.id.rename).setOnClickListener {
            textView.visibility = View.INVISIBLE
            editLayout.visibility = View.VISIBLE
            actionMenu.visibility = View.INVISIBLE
            val editText = editLayout.findViewById<EditText>(R.id.app_name_edit)
            val resetButton = editLayout.findViewById<AppCompatButton>(R.id.reset)

            val app = Pair(mainActivity!!, Pair(userHandle, workProfile))

            searchView.visibility = View.INVISIBLE
            editText.requestFocus()

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                val imm =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
            }, 100)

            binding.root.addOnLayoutChangeListener { _, _, top, _, bottom, _, oldTop, _, oldBottom ->
                if (bottom - top > oldBottom - oldTop) {
                    editLayout.clearFocus()

                    editLayout.visibility = View.INVISIBLE
                    textView.visibility = View.VISIBLE
                    searchView.visibility = View.VISIBLE
                }
            }

            editText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val imm =
                        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(editText.windowToken, 0)
                    sharedPreferenceManager.setAppName(
                        activity,
                        appInfo.packageName,
                        workProfile,
                        editText.text.toString()
                    )

                    val newPosition = activity.getInstalledApps()
                        .indexOfFirst { it.first.applicationInfo.packageName == appInfo.packageName && it.second.second == workProfile }
                    uiScope.launch {
                        activity.updateItem(position, app)
                        activity.moveItem(position, newPosition)
                        activity.manualRefresh()
                    }

                    return@setOnEditorActionListener true
                }
                false
            }

            resetButton.setOnClickListener {
                val imm =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editLayout.windowToken, 0)
                sharedPreferenceManager.resetAppName(
                    activity,
                    app.first.applicationInfo.packageName,
                    app.second.second
                )
                val newPosition = activity.getInstalledApps()
                    .indexOfFirst { it.first.applicationInfo.packageName == appInfo.packageName && it.second.second == workProfile }
                activity.updateItem(position, app)
                activity.moveItem(position, newPosition)
                activity.manualRefresh()
            }
        }

        actionMenu.findViewById<TextView>(R.id.hide).setOnClickListener {
            editLayout.visibility = View.GONE
            textView.visibility = View.GONE
            actionMenu.visibility = View.GONE
            sharedPreferenceManager.setAppHidden(activity, appInfo.packageName, workProfile, true)
        }

        actionMenu.findViewById<TextView>(R.id.close).setOnClickListener {
            actionMenu.visibility = View.INVISIBLE
            textView.visibility = View.VISIBLE
        }
    }
}
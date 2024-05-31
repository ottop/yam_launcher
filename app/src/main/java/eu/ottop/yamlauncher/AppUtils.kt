package eu.ottop.yamlauncher

import android.app.Activity
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.os.UserHandle
import androidx.appcompat.app.AppCompatActivity

class AppUtils {

    private val sharedPreferenceManager = SharedPreferenceManager()

    fun getInstalledApps(activity: Activity): List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>> {
        val allApps = mutableListOf<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>()
        val launcherApps = activity.getSystemService(AppCompatActivity.LAUNCHER_APPS_SERVICE) as LauncherApps
        for (i in launcherApps.profiles.indices) {
            launcherApps.getActivityList(null, launcherApps.profiles[i]).forEach { app ->
                if (!sharedPreferenceManager.isAppHidden(activity, app.applicationInfo.packageName, i) && app.applicationInfo.packageName != activity.applicationInfo.packageName) {
                    allApps.add(Pair(app, Pair(launcherApps.profiles[i], i)))
                }
            }
        }
        return allApps.sortedBy {
            sharedPreferenceManager.getAppName(activity, it.first.applicationInfo.packageName,it.second.second, activity.packageManager.getApplicationLabel(it.first.applicationInfo)).toString().lowercase()
        }


    }
}
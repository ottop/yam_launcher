package eu.ottop.yamlauncher

import android.app.Activity
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.os.UserHandle
import androidx.appcompat.app.AppCompatActivity

class AppUtils {

    private val sharedPreferenceManager = SharedPreferenceManager()

    fun getInstalledApps(activity: Activity, launcherApps: LauncherApps): List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>> {
        val allApps = mutableListOf<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>()

        for (i in launcherApps.profiles.indices) {
            launcherApps.getActivityList(null, launcherApps.profiles[i]).forEach { app ->
                if (!sharedPreferenceManager.isAppHidden(activity, app.applicationInfo.packageName, i) && app.applicationInfo.packageName != activity.applicationInfo.packageName) {
                    allApps.add(Pair(app, Pair(launcherApps.profiles[i], i)))
                }
            }
        }
        return allApps.sortedBy {
            sharedPreferenceManager.getAppName(activity, it.first.applicationInfo.packageName,it.second.second, it.first.applicationInfo.loadLabel(activity.packageManager)).toString().lowercase()
        }

    }

    fun getHiddenApps(activity: Activity): List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>> {
        val allApps = mutableListOf<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>()
        val launcherApps = activity.getSystemService(AppCompatActivity.LAUNCHER_APPS_SERVICE) as LauncherApps
        for (i in launcherApps.profiles.indices) {
            launcherApps.getActivityList(null, launcherApps.profiles[i]).forEach { app ->
                if (sharedPreferenceManager.isAppHidden(activity, app.applicationInfo.packageName, i)) {
                    allApps.add(Pair(app, Pair(launcherApps.profiles[i], i)))
                }
            }
        }
        return allApps.sortedBy {
            sharedPreferenceManager.getAppName(activity, it.first.applicationInfo.packageName,it.second.second, activity.packageManager.getApplicationLabel(it.first.applicationInfo)).toString().lowercase()
        }
    }

    fun getAppInfo(
        launcherApps: LauncherApps,
        packageName: String,
        profile: Int
    ): ApplicationInfo? {

        return launcherApps.getActivityList(
            packageName, launcherApps.profiles[profile]
        ).firstOrNull()?.applicationInfo
    }
}
package eu.ottop.yamlauncher

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.os.UserHandle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.reflect.InvocationTargetException

class AppUtils {

    private val sharedPreferenceManager = SharedPreferenceManager()

    suspend fun getInstalledApps(activity: Activity, launcherApps: LauncherApps): List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>> {
        val allApps = mutableListOf<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>()
        var sortedApps = listOf<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>()
        withContext(Dispatchers.Default) {
            for (i in launcherApps.profiles.indices) {
                launcherApps.getActivityList(null, launcherApps.profiles[i]).forEach { app ->
                    if (!sharedPreferenceManager.isAppHidden(
                            activity,
                            app.applicationInfo.packageName,
                            i
                        ) && app.applicationInfo.packageName != activity.applicationInfo.packageName
                    ) {
                        allApps.add(Pair(app, Pair(launcherApps.profiles[i], i)))
                    }
                }
            }

            sortedApps = allApps.sortedBy {
                sharedPreferenceManager.getAppName(
                    activity,
                    it.first.applicationInfo.packageName,
                    it.second.second,
                    it.first.applicationInfo.loadLabel(activity.packageManager)
                ).toString().lowercase()
            }
        }
        return sortedApps

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
        return try {
            launcherApps.getApplicationInfo(packageName, 0, launcherApps.profiles[profile])
        } catch (_: Exception) {
            null
        }
    }

    fun launchApp(context: Context, launcherApps: LauncherApps, appInfo: LauncherActivityInfo, userHandle: UserHandle) {
        val mainActivity = launcherApps.getActivityList(appInfo.applicationInfo.packageName, userHandle).firstOrNull()
        if (mainActivity != null) {
            launcherApps.startMainActivity(mainActivity.componentName,  userHandle, null, null)
        } else {
            Toast.makeText(context, "Cannot launch app", Toast.LENGTH_SHORT).show()
        }
    }
}
package eu.ottop.yamlauncher.utils

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.os.Build
import android.os.UserHandle
import android.os.UserManager
import androidx.core.content.ContextCompat.getString
import eu.ottop.yamlauncher.R
import eu.ottop.yamlauncher.settings.SharedPreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.provider.Settings

class AppUtils(private val context: Context, private val launcherApps: LauncherApps) {

    private val sharedPreferenceManager = SharedPreferenceManager(context)

    suspend fun getInstalledApps(showApps: Boolean = false): List<Triple<LauncherActivityInfo, UserHandle, Int>> {
        val allApps = mutableListOf<Triple<LauncherActivityInfo, UserHandle, Int>>()
        var sortedApps = listOf<Triple<LauncherActivityInfo, UserHandle, Int>>()
        withContext(Dispatchers.Default) {
            for (i in launcherApps.profiles.indices) { // Check apps for all
                // Don't show private space apps in app menu
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM && launcherApps.getLauncherUserInfo(launcherApps.profiles[i])?.userType == UserManager.USER_TYPE_PROFILE_PRIVATE) {
                        continue
                }
                allApps.addAll(getAppsForProfile(i, showApps))
            }

            // Sort apps by name
            sortedApps = allApps.sortedWith(
                compareBy<Triple<LauncherActivityInfo, UserHandle, Int>> {
                    !sharedPreferenceManager.isAppPinned(it.first.componentName.flattenToString(), it.third) // This displays the pinned apps for some reason.
                }.thenBy {
                sharedPreferenceManager.getAppName(
                    it.first.componentName.flattenToString(),
                    it.third,
                    it.first.label
                ).toString().lowercase()
                }
            )
        }
        return sortedApps
    }

    private fun getAppsForProfile(profile: Int, showApps: Boolean = false): MutableList<Triple<LauncherActivityInfo, UserHandle, Int>> {
        val apps = mutableListOf<Triple<LauncherActivityInfo, UserHandle, Int>>()
        launcherApps.getActivityList(null, launcherApps.profiles[profile]).forEach { app ->
            if ((!sharedPreferenceManager.isAppHidden( // Only include the app if it isn't set as hidden or in shortcut selection with the appropriate option enabled
                    app.componentName.flattenToString(),
                    profile
                ) or showApps)&& app.applicationInfo.packageName != context.applicationInfo.packageName // Hide the launcher itself
            ) {
                apps.add(Triple(app, launcherApps.profiles[profile], profile)) // The i variable gets used to determine whether an app is in the personal profile or work profile
            }
        }

        return apps
    }

    suspend fun getPrivateApps(): List<Triple<LauncherActivityInfo, UserHandle, Int>> {
        val allApps = mutableListOf<Triple<LauncherActivityInfo, UserHandle, Int>>()
        var sortedApps = listOf<Triple<LauncherActivityInfo, UserHandle, Int>>()
        withContext(Dispatchers.Default) {
            for (i in launcherApps.profiles.indices) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM && launcherApps.getLauncherUserInfo(launcherApps.profiles[i])?.userType == UserManager.USER_TYPE_PROFILE_PRIVATE) {
                    allApps.addAll(getAppsForProfile(i))
                }
            }

            // Sort apps by name
            sortedApps = allApps.sortedWith(
                compareBy<Triple<LauncherActivityInfo, UserHandle, Int>> {
                    !sharedPreferenceManager.isAppPinned(it.first.componentName.flattenToString(), it.third) // This displays the pinned apps for some reason.
                }.thenBy {
                    sharedPreferenceManager.getAppName(
                        it.first.componentName.flattenToString(),
                        it.third,
                        it.first.label
                    ).toString().lowercase()
                }
            )
        }

        return sortedApps
    }

    fun isPrivateSpaceEnabled(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager

            for (profile in userManager.userProfiles) {
                println(launcherApps.getLauncherUserInfo(profile)?.userType)
                 if (launcherApps.getLauncherUserInfo(profile)?.userType == UserManager.USER_TYPE_PROFILE_PRIVATE) {
                     // Check if the private space is hidden
                     return !(Settings.Secure.getString(context.contentResolver, "hide_privatespace_entry_point") == "1" && userManager.isQuietModeEnabled(profile))
                 }
            }
        }
        return false
    }

    // Get hidden apps for the hidden apps settings
    suspend fun getHiddenApps(): List<Triple<LauncherActivityInfo, UserHandle, Int>> {
        val allApps = mutableListOf<Triple<LauncherActivityInfo, UserHandle, Int>>()
        var sortedApps = listOf<Triple<LauncherActivityInfo, UserHandle, Int>>()
        withContext(Dispatchers.Default) {
        for (i in launcherApps.profiles.indices) {
            launcherApps.getActivityList(null, launcherApps.profiles[i]).forEach { app ->
                if (sharedPreferenceManager.isAppHidden(app.componentName.flattenToString(), i)) {
                    allApps.add(Triple(app, launcherApps.profiles[i], i))
                }
            }
        }

        //Sort apps by name
        sortedApps = allApps.sortedBy {
        sharedPreferenceManager.getAppName(
            it.first.componentName.flattenToString(),
            it.third,
            it.first.label
        ).toString().lowercase()
        }
        }
        return sortedApps
    }

    fun getAppInfo(
        packageName: String,
        profile: Int
    ): ApplicationInfo? {
        return try {
            launcherApps.getApplicationInfo(packageName, 0, launcherApps.profiles[profile])
        } catch (_: Exception) {
            null
        }
    }

    private fun startApp(componentName: ComponentName, userHandle: UserHandle) {
        launcherApps.startMainActivity(componentName,  userHandle, null, null)
    }

    fun launchApp(componentName: ComponentName, userHandle: UserHandle) {
        if (sharedPreferenceManager.isConfirmationEnabled()) {
            showConfirmationDialog(componentName, userHandle)
        } else {
            startApp(componentName, userHandle)
        }
    }

    private fun showConfirmationDialog(componentName: ComponentName, userHandle: UserHandle) {
        AlertDialog.Builder(context).apply {
            setTitle(getString(context, R.string.confirm_title))
            setMessage(getString(context, R.string.launch_confirmation_text))

            setPositiveButton(getString(context, R.string.confirm_yes)) { _, _ ->
                startApp(componentName, userHandle)
            }

            setNegativeButton(getString(context, R.string.confirm_no)) { _, _ ->
            }

        }.create().show()
    }

}
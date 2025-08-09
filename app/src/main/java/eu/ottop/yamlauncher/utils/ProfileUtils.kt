package eu.ottop.yamlauncher.utils

import android.content.Context
import android.content.pm.LauncherApps
import android.os.Build
import android.os.UserHandle
import android.os.UserManager

class ProfileUtils(private val context: Context, private val launcherApps: LauncherApps) {
    private fun getPrivateProfileInt(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager

            for (i in userManager.userProfiles.indices) {
                if (launcherApps.getLauncherUserInfo(launcherApps.profiles[i])?.userType == UserManager.USER_TYPE_PROFILE_PRIVATE) {
                    // Check if the private space is hidden
                    return i
                }
            }
        }
        return -1
    }

    fun isWorkProfile(profile: Int): Boolean {
        return when (profile) {
            getPrivateProfileInt() -> false
            0 -> false
            else -> true

        }
    }

    fun isPrivateProfile(profile: Int): Boolean {
        return profile == getPrivateProfileInt()
    }

    fun isPrivateSpaceLocked(profile: UserHandle): Boolean {
        val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager
        return userManager.isQuietModeEnabled(profile)
    }

    fun togglePrivateSpace() {
        val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager
        userManager.requestQuietModeEnabled(!userManager.isQuietModeEnabled(launcherApps.profiles[getPrivateProfileInt()]), launcherApps.profiles[getPrivateProfileInt()])
    }
}
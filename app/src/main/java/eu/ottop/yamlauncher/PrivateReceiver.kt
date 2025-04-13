package eu.ottop.yamlauncher.tasks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_PROFILE_AVAILABLE
import android.content.Intent.ACTION_PROFILE_UNAVAILABLE
import android.content.IntentFilter
import android.os.Build
import eu.ottop.yamlauncher.MainActivity

class PrivateReceiver(private val activity: MainActivity) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            when (intent.action) {
                ACTION_PROFILE_AVAILABLE -> {
                    activity.unlockedPrivateSpace()
                }
                ACTION_PROFILE_UNAVAILABLE -> {
                    activity.lockedPrivateSpace()
                }
            }
        }
    }

    companion object {
        fun register(context: Context, activity: MainActivity): PrivateReceiver {
            val receiver = PrivateReceiver(activity)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                val filter = IntentFilter()
                filter.addAction(ACTION_PROFILE_AVAILABLE)
                filter.addAction(ACTION_PROFILE_UNAVAILABLE)
                context.registerReceiver(receiver, filter)
            }
            return receiver
        }
    }
}
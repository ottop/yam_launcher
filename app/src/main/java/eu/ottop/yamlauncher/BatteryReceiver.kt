package eu.ottop.yamlauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.widget.TextClock

class BatteryReceiver(private val dateText: TextClock) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = level * 100 / scale.toFloat()
            dateText.format12Hour = "dd MMM yyyy | ${batteryPct.toInt()}%"
            dateText.format24Hour = "dd MMM yyyy | ${batteryPct.toInt()}%"
        }
    }

    companion object {
        fun register(context: Context, textView: TextClock): BatteryReceiver {
            val receiver = BatteryReceiver(textView)
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            context.registerReceiver(receiver, filter)
            return receiver
        }
    }
}
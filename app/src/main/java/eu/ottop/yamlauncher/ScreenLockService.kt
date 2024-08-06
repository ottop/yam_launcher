package eu.ottop.yamlauncher

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class ScreenLockService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Handle accessibility events if needed
    }

    override fun onInterrupt() {
        // Handle interrupt
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.action == "LOCK_SCREEN") {
            performLockScreen()
        }
        stopSelf()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun performLockScreen() {
        performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
    }
}
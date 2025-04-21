package eu.ottop.yamlauncher.utils

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import eu.ottop.yamlauncher.R

class BiometricUtils(private val activity: FragmentActivity) {
    private lateinit var callbackSettings: CallbackSettings

    interface CallbackSettings {
        fun onAuthenticationSucceeded()
        fun onAuthenticationFailed()
        fun onAuthenticationError(errorCode: Int, errorMessage: CharSequence?)
    }

    fun startBiometricSettingsAuth(callbackApp: CallbackSettings) {
        this.callbackSettings = callbackApp

        val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                callbackSettings.onAuthenticationSucceeded()
            }

            override fun onAuthenticationFailed() {
                callbackSettings.onAuthenticationFailed()
            }

            override fun onAuthenticationError(errorCode: Int, errorMessage: CharSequence) {
                callbackSettings.onAuthenticationError(errorCode, errorMessage)
            }
        }

        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor, authenticationCallback)

        val authenticators =
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        val canAuthenticate =
            BiometricManager.from(activity).canAuthenticate(authenticators)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.getString(R.string.text_biometric_login))
            .setSubtitle(activity.getString(R.string.text_biometric_login_sub))
            .setAllowedAuthenticators(authenticators)
            .setConfirmationRequired(false)
            .build()

        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            biometricPrompt.authenticate(promptInfo)
        }
    }
}

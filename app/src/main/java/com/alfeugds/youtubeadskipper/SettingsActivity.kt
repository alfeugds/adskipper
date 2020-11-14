package com.alfeugds.youtubeadskipper

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ServiceInfo
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, SettingsFragment())
                    .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
        private val LOG_TAG = "SettingsActivity"

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            Log.v(LOG_TAG, "onCreatePreferences()")
            refreshUIWithServiceStatus()
        }

        private fun refreshUIWithServiceStatus() {
            val isEnabled = isYoutubeServiceEnabled()
            Log.i(LOG_TAG, "isYoutubeServiceEnabled: $isEnabled")

            if (!isEnabled) {
                setEnableServiceSetting(false)
            }
        }

        private fun setEnableServiceSetting(value: Boolean) {
            val enableService: SwitchPreferenceCompat? = findPreference(SETTINGS_ENABLE_SERVICE)
            enableService?.isChecked = value
        }

        private fun setMuteAudioSetting(value: Boolean) {
            val muteAudio: SwitchPreferenceCompat? = findPreference(SETTINGS_MUTE_AUDIO)
            muteAudio?.isChecked = value
        }

        private fun isAccessibilityServiceEnabled(context: Context?, service: Class<out AccessibilityService?>): Boolean {
            val am = context?.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
            val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            for (enabledService in enabledServices) {
                val enabledServiceInfo: ServiceInfo = enabledService.resolveInfo.serviceInfo
                if (enabledServiceInfo.packageName == context.packageName && enabledServiceInfo.name == service.name)
                    return true
            }
            return false
        }

        private fun enableAccessibilityService() {
            val builder = context?.let { AlertDialog.Builder(it) }
            if (builder == null) {
                Log.e(LOG_TAG, "Could not create alert dialog, builder is null.")
                return
            }
            builder.apply {
                setCancelable(false)
                setPositiveButton(R.string.dialog_ok
                ) { _, _ ->
                    // User clicked OK button
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    startActivityForResult(intent, 0)
                    setMuteAudioSetting(true)
                }
                setNegativeButton(R.string.dialog_cancel
                ) { _, _ ->
                    // User cancelled the dialog
                    Log.i(LOG_TAG, "User cancelled action to open accessibility settings.")
                    setEnableServiceSetting(false)
                }
                setTitle(R.string.dialog_open_accessibility_settings_title)
                setMessage(R.string.dialog_open_accessibility_settings_message)
            }
            builder.create()
            builder.show()
        }

        override fun onResume() {
            super.onResume()
            refreshUIWithServiceStatus()
            preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }

        private fun isYoutubeServiceEnabled(): Boolean {
            return isAccessibilityServiceEnabled(context, YoutubeAccessibilityService::class.java)
        }


        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            Log.v(LOG_TAG, "onSharedPreferenceChanged()...")

            val isServiceEnabled = isAccessibilityServiceEnabled(context, YoutubeAccessibilityService::class.java)
            Log.v(LOG_TAG, "isAccessibilityServiceEnabled(): $isServiceEnabled")

            val setting: Boolean? = sharedPreferences?.getBoolean(key, false) ?: return

            Log.v(LOG_TAG, "setting key: $key, enabled: $setting")

            when (key) {
                SETTINGS_ENABLE_SERVICE -> {
                    if (setting == true && !isServiceEnabled) {
                        enableAccessibilityService()
                    }
                }
            }
        }
    }
}
package com.alfeugds.adskipper

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.preference.PreferenceManager


class AdSkipperAccessibilityService : AccessibilityService() {

    private val TAG = "AdSkipperAccessibilityService"
    private val AD_LEARN_MORE_BUTTON_ID = "com.google.android.youtube:id/player_learn_more_button"
    private val SKIP_AD_BUTTON_ID = "com.google.android.youtube:id/skip_ad_button"

    private var isMuted = false

    private var isRunning = false

    override fun onInterrupt() {
        Log.v(TAG, "onInterrupt fired")
        isRunning = false
    }

    private fun isServiceEnabled(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        return prefs.getBoolean(SETTINGS_ENABLE_SERVICE, true)
    }

    private fun isMuteAdEnabled(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        return prefs.getBoolean(SETTINGS_MUTE_AUDIO, true)
    }

    private fun muteMedia() {
        if (isMuted) {
            return
        }

        if (!isMuteAdEnabled()) {
            return
        }

        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
        }else {
            @Suppress("DEPRECATION")
            am.setStreamMute(AudioManager.STREAM_MUSIC, true)
        }

        Log.i(TAG, "STREAM_MUSIC muted.")
        isMuted = true
    }

    private fun unmuteMedia() {
        if (!isMuted) {
            return
        }

        if (!isMuteAdEnabled()) {
            return
        }

        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        // am.setStreamMute(AudioManager.STREAM_MUSIC, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0)
        }else{
            @Suppress("DEPRECATION")
            am.setStreamMute(AudioManager.STREAM_MUSIC, false)
        }

        Log.i(TAG, "STREAM_MUSIC unmuted.")
        isMuted = false
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        //optional resource ids
        // com.google.android.youtube:id/ad_progress_text --> ad duration

        // required resource ids:
        // [visitar anunciante text]
        // com.google.android.youtube:id/player_learn_more_button --> class android.widget.TextView

        // [skip ad button]
        // com.google.android.youtube:id/skip_ad_button --> class android.widget.FrameLayout

        try {

            if (!isServiceEnabled()) {
                Log.i(TAG, "Service is not supposed to be enabled. Disabling it..")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Log.i(TAG, "Disabling it..")
                    // TODO: decide if it is a good idea do entirely disable service. It would require the user to always enable the accessibility service and might generate friction
                    //disableSelf()
                }
                return
            }

            val adLearnMoreElement = rootInActiveWindow.findAccessibilityNodeInfosByViewId(AD_LEARN_MORE_BUTTON_ID).getOrNull(0)
            val skipAdButton = rootInActiveWindow.findAccessibilityNodeInfosByViewId(SKIP_AD_BUTTON_ID)?.getOrNull(0)

            if (adLearnMoreElement == null && skipAdButton == null) {
                unmuteMedia()
                Log.v(TAG, "No ads yet...")
                return
            }
            Log.i(TAG, "player_learn_more_button or skipAdButton are visible. Trying to skip ad...")

            muteMedia()

            if (skipAdButton == null) {
                Log.v(TAG, "skipAdButton is null... returning...")
                return
            }

            if (skipAdButton.isClickable) {
                Log.v(TAG, "skipAdButton is clickable! Trying to click it...")
                skipAdButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Log.i(TAG, "Clicked skipAdButton!")
            }

        } catch (error: Exception) {
            Log.e(TAG, "Something went wrong...")
            Log.e(TAG, error.message.toString())
            error.printStackTrace()
        }
    }

    override fun onServiceConnected() {
        Log.v(TAG, "accessibility onServiceConnected(). Ad skipping service connected.")
        isRunning = true
    }

}
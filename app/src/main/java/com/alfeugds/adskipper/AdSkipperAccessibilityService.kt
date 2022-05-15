package com.alfeugds.adskipper

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.preference.PreferenceManager


class AdSkipperAccessibilityService : AccessibilityService() {

    private val TAG = "AdSkipperService"
    private val AD_LEARN_MORE_BUTTON_ID = "com.google.android.youtube:id/player_learn_more_button"
    private val SKIP_AD_BUTTON_ID = "com.google.android.youtube:id/skip_ad_button"
    private val AD_PROGRESS_TEXT = "com.google.android.youtube:id/ad_progress_text"
    private val APP_PROMO_AD_CTA_OVERLAY = "com.google.android.youtube:id/app_promo_ad_cta_overlay"
    private val AD_COUNTDOWN = "com.google.android.youtube:id/ad_countdown"

    private var isMuted = false
    var isRunning = false

    private lateinit var prefs: SharedPreferences
    private lateinit var audioManager: AudioManager
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate fired")
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.i(TAG, "onRebind fired")
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
    }

    companion object {
        private var instance: AdSkipperAccessibilityService? = null

        @JvmStatic
        fun getInstance (): AdSkipperAccessibilityService? = instance
    }

    override fun onServiceConnected() {
        Log.v(TAG, "accessibility onServiceConnected(). Ad skipping service connected.")
        isRunning = true
        instance = this
        super.onServiceConnected()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.i(TAG, "onTaskRemoved fired")
        disable()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        Log.v(TAG, "onDestroy fired")
        isRunning = false
        super.onDestroy()
    }

    override fun onInterrupt() {
        Log.v(TAG, "onInterrupt fired")
        isRunning = false
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "onUnbind called. " + intent?.dataString)
        instance = null
        return super.onUnbind(intent)
    }

    override fun onLowMemory() {
        Log.w(TAG, "onLowMemory")
        super.onLowMemory()
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
        }else {
            @Suppress("DEPRECATION")
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true)
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0)
        }else{
            @Suppress("DEPRECATION")
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false)
        }

        Log.i(TAG, "STREAM_MUSIC unmuted.")
        isMuted = false
    }

    fun disable(){
        Log.i(TAG, "Disabling service with stopSelf")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopSelf()
        }
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
                Log.i(TAG, "Service is not supposed to be enabled.")
                return
            }

            val adLearnMoreElement = rootInActiveWindow?.findAccessibilityNodeInfosByViewId(AD_LEARN_MORE_BUTTON_ID)?.getOrNull(0)
            val skipAdButton = rootInActiveWindow?.findAccessibilityNodeInfosByViewId(SKIP_AD_BUTTON_ID)?.getOrNull(0)
            val adProgressText = rootInActiveWindow?.findAccessibilityNodeInfosByViewId(AD_PROGRESS_TEXT)?.getOrNull(0)
            val appPromoAdCTAOverlay = rootInActiveWindow?.findAccessibilityNodeInfosByViewId(APP_PROMO_AD_CTA_OVERLAY)?.getOrNull(0)
            val adCountdown = rootInActiveWindow?.findAccessibilityNodeInfosByViewId(AD_COUNTDOWN)?.getOrNull(0)

            if (adLearnMoreElement == null && skipAdButton == null && adProgressText == null && appPromoAdCTAOverlay == null && adCountdown == null) {
                unmuteMedia()
                Log.v(TAG, "No ads yet...")
                return
            }
            Log.i(TAG, "player_learn_more_button or skipAdButton or adProgressText are visible. Trying to skip ad...")

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
            Log.e(TAG, "Something went wrong...", error)
        }
    }

}
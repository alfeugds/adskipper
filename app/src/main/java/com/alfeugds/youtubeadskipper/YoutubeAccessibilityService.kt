package com.alfeugds.youtubeadskipper

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.media.AudioManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo


class YoutubeAccessibilityService : AccessibilityService() {

    private val TAG = "YoutubeAdSkipperService"
    private val AD_LEARN_MORE_BUTTON_ID = "com.google.android.youtube:id/player_learn_more_button"
    private val SKIP_AD_BUTTON_ID = "com.google.android.youtube:id/skip_ad_button"

    private var isMuted = false

    override fun onInterrupt() {
        Log.v(TAG, "onInterrupt fired");
    }

    fun muteMedia() {
        if (isMuted) {
            return
        }

        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        // am.setStreamMute(AudioManager.STREAM_MUSIC, true)
        am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)

        Log.i(TAG, "STREAM_MUSIC muted.");
        isMuted = true
    }

    fun unmuteMedia() {
        if (!isMuted) {
            return
        }
        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        // am.setStreamMute(AudioManager.STREAM_MUSIC, false)
        am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0)

        Log.i(TAG, "STREAM_MUSIC unmuted.");
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

            var adLearnMoreElement = rootInActiveWindow.findAccessibilityNodeInfosByViewId(AD_LEARN_MORE_BUTTON_ID).getOrNull(0)
            if (adLearnMoreElement == null) {
                unmuteMedia()
                Log.v(TAG, "No ads yet...");
                return
            }
            Log.i(TAG, "player_learn_more_button is visible. Trying to skip ad...");

            muteMedia()

            var skipAdButton = rootInActiveWindow.findAccessibilityNodeInfosByViewId(SKIP_AD_BUTTON_ID)?.getOrNull(0)

            if (skipAdButton == null) {
                Log.v(TAG, "skipAdButton is null... returning...");
                return
            }

            if (skipAdButton.isClickable) {
                Log.v(TAG, "skipAdButton is clickable! Trying to click it...");
                skipAdButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Log.d(TAG, "Clicked skipAdButton!");
            }
        } catch (error: Exception) {
            Log.e(TAG, "Something went wrong...");
            Log.e(TAG, error.message.toString())
            error.printStackTrace()
        }
    }

    override fun onServiceConnected() {
        Log.v(TAG, "accessibility onServiceConnected(). Ad skipping service connected.")
    }

}
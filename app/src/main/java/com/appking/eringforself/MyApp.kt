package com.appking.eringforself

import android.app.Application
import android.content.SharedPreferences
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.UnityAds

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Retrieve saved game ID from SharedPreferences
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("UnityAdsPrefs", MODE_PRIVATE)
        val savedGameId = sharedPreferences.getString("game_id", "") // Default Game ID
        val isTestMode = sharedPreferences.getBoolean("is_test_mode", true) // Default test mode OFF


        // Initialize Unity Ads with saved Game ID
        UnityAds.initialize(this, savedGameId, isTestMode, object : IUnityAdsInitializationListener {
            override fun onInitializationComplete() {}
            override fun onInitializationFailed(
                error: UnityAds.UnityAdsInitializationError?,
                message: String?,
            ) {
            }
        })
    }
}

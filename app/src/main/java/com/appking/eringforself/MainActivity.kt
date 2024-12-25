package com.appking.eringforself

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.appking.eringforself.user.UserGameIdActivity
import com.unity3d.services.banners.UnityBannerSize

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Show Ads Info Popup on App Launch
        showAdsInfoPopup()

        setupBannerAds()
        setupProfileButton()
        setupInterstitialAds()
        setupRewardedAds()
        setupInfoButton()
        setupUpdateButton() // Added for update functionality
    }

    // Step 1: Setup banner ads with Unity Ads
    private fun setupBannerAds() {
        showBannerAds(
            this,
            findViewById(R.id.adBannerCon),
            "Banner_Android",
            UnityBannerSize.standard
        )
    }

    // Step 2: Profile button to generate/retrieve user Game ID
    private fun setupProfileButton() {
        val profileButton = findViewById<Button>(R.id.acount)
        profileButton.setOnClickListener {
            val intent = Intent(this, UserGameIdActivity::class.java)
            startActivity(intent)
        }
    }

    // Step 3: Setup interstitial ads
    private fun setupInterstitialAds() {
        val interstitialAdsButton = findViewById<Button>(R.id.showInterstitialAdsBtn)
        val interstitialAds = MyInterstitialAds(this)
        interstitialAds.loadInterstitialAds("Interstitial_Android")

        interstitialAdsButton.setOnClickListener {
            interstitialAds.showInterstitialAds("Interstitial_Android") {
                navigateToAfterAdActivity()
            }
        }
    }

    // Navigate to AfterInterstitialActivity
    private fun navigateToAfterAdActivity() {
        val intent = Intent(this, AfterInterstitialActivity::class.java)
        startActivity(intent)
    }

    // Step 4: Setup rewarded ads for user incentives
    private fun setupRewardedAds() {
        val sharedPreferenceManager = SharedPreferenceManger(this)
        updateRewardedCoinDisplay(sharedPreferenceManager.totalRewardedCoin)

        val rewardedAdsButton = findViewById<Button>(R.id.showRewardedAdsBtn)
        val rewardedAds = MyRewardedAds(this)
        rewardedAds.loadRewardedAds("Rewarded_Android")

        rewardedAdsButton.setOnClickListener {
            rewardedAds.showRewardedAds("Rewarded_Android") {
                addRewardedCoins(10, sharedPreferenceManager)
            }
        }
    }

    // Add rewarded coins and update display
    private fun addRewardedCoins(rewardedCoins: Int, sharedPreferenceManager: SharedPreferenceManger) {
        sharedPreferenceManager.totalRewardedCoin += rewardedCoins
        updateRewardedCoinDisplay(sharedPreferenceManager.totalRewardedCoin)
    }

    // Update rewarded coin display
    private fun updateRewardedCoinDisplay(totalRewardedCoins: Int) {
        val rewardedCoinTextView = findViewById<TextView>(R.id.rewardedCoinTxt)
        rewardedCoinTextView.text = "Total Rewarded Coins: $totalRewardedCoins Coins"
    }

    // Step 5: Setup information popup button
    private fun setupInfoButton() {
        val infoButton = findViewById<Button>(R.id.infoButton)
        infoButton.setOnClickListener {
            showAdsInfoPopup()
        }
    }

    // Step 6: Setup update button to download the APK
    private fun setupUpdateButton() {
        val updateButton = findViewById<Button>(R.id.updateButton)
        updateButton.setOnClickListener {
            checkAndDownloadUpdate()
        }
    }

    // Check and download update using DownloadManager
    private fun checkAndDownloadUpdate() {
        val apkUrl = "https://github.com/samyak2403/IPTVmine/raw/master/app/release/app-release.apk"
        val fileName = "app-release.apk"

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(apkUrl))
            .setTitle("Updating App")
            .setDescription("Downloading the latest version...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        downloadManager.enqueue(request)

        AlertDialog.Builder(this)
            .setTitle("Update in Progress")
            .setMessage("The app is downloading the latest version. Please check your Downloads folder to install the update manually.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    // Display a popup with detailed information about ads and Game ID generation
    private fun showAdsInfoPopup() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.popup_ads_info, null)

        val infoTextView = view.findViewById<TextView>(R.id.infoTextView)
        infoTextView.text = """
            1. Click the profile/user ID icon to activate Game ID.
            2. Ads are not limited, but VPN must be enabled for unlimited rewards.
            3. Confirm real ads are being shown, not fake ones.
            4. Steps to generate a Game ID:
               - Open the Game ID settings in the app.
               - Follow the instructions provided in Unity documentation:
                 a) Log in to Unity Dashboard (https://dashboard.unity3d.com/).
                 b) Create or select a Unity project.
                 c) Enable Unity Ads and copy the Game ID for your platform.
               - Paste the Game ID into the appropriate Unity Ads setup in your app.
            5. $100 redeem is required to cash out.
        """.trimIndent()

        builder.setView(view)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}

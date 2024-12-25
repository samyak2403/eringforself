package com.appking.eringforself.user

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.appking.eringforself.R
import com.samyak2403.tastytoast.TastyToast
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.UnityAds

class UserGameIdActivity : AppCompatActivity() {

    private lateinit var etGameId: EditText
    private lateinit var btnUpdateGameId: Button
    private lateinit var tvStatus: TextView
    private lateinit var switchTestMode: Switch
    private lateinit var progressBar: ProgressBar

    companion object {
        private const val SHARED_PREFS_NAME = "UnityAdsPrefs"
        private const val KEY_GAME_ID = "game_id"
        private const val KEY_IS_TEST_MODE = "is_test_mode"
        private const val TAG = "UnityAds"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_game_id)

        initializeUI()
        loadSavedPreferences()
        setupListeners()
    }

    private fun initializeUI() {
        etGameId = findViewById(R.id.et_game_id)
        btnUpdateGameId = findViewById(R.id.btn_update_game_id)
        tvStatus = findViewById(R.id.tv_status)
        switchTestMode = findViewById(R.id.switch_test_mode)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun loadSavedPreferences() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
        val savedGameId = sharedPreferences.getString(KEY_GAME_ID, "") ?: ""
        val isTestMode = sharedPreferences.getBoolean(KEY_IS_TEST_MODE, true)

        etGameId.setText(savedGameId)
        switchTestMode.isChecked = isTestMode
        updateStatusUI(savedGameId)
    }

    private fun setupListeners() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)

        switchTestMode.setOnCheckedChangeListener { _, isChecked ->
            handleTestModeToggle(sharedPreferences, isChecked)
        }

        btnUpdateGameId.setOnClickListener {
            handleUpdateButtonClick(sharedPreferences)
        }
    }

    private fun handleTestModeToggle(sharedPreferences: android.content.SharedPreferences, isChecked: Boolean) {
        val currentGameId = etGameId.text.toString().trim()

        if (currentGameId.isBlank()) {
            showToast(getString(R.string.enter_valid_game_id), TastyToast.WARNING)
            switchTestMode.isChecked = !isChecked
            return
        }

        sharedPreferences.edit().putBoolean(KEY_IS_TEST_MODE, isChecked).apply()
        updateStatusUI(currentGameId)
        showToast(getString(R.string.mode_saved, if (isChecked) "Test" else "Live"), TastyToast.SUCCESS)
    }

    private fun handleUpdateButtonClick(sharedPreferences: android.content.SharedPreferences) {
        val enteredGameId = etGameId.text.toString().trim()

        if (enteredGameId.isBlank()) {
            showToast(getString(R.string.enter_valid_game_id), TastyToast.WARNING)
            return
        }

        val isTestMode = switchTestMode.isChecked
        savePreferences(sharedPreferences, enteredGameId, isTestMode)
        initializeUnityAds(enteredGameId, isTestMode)
    }

    private fun savePreferences(
        sharedPreferences: android.content.SharedPreferences,
        gameId: String,
        isTestMode: Boolean
    ) {
        sharedPreferences.edit()
            .putString(KEY_GAME_ID, gameId)
            .putBoolean(KEY_IS_TEST_MODE, isTestMode)
            .apply()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initializeUnityAds(gameId: String, isTestMode: Boolean) {
        setLoadingState(true)

        UnityAds.initialize(applicationContext, gameId, isTestMode, object : IUnityAdsInitializationListener {
            override fun onInitializationComplete() {
                handleInitializationSuccess(gameId, isTestMode)
            }

            override fun onInitializationFailed(error: UnityAds.UnityAdsInitializationError?, message: String?) {
                handleInitializationFailure(message)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateStatusUI(gameId: String) {
        val isActive = gameId.isNotBlank()
        tvStatus.text = getString(if (isActive) R.string.status_active else R.string.status_inactive)
        tvStatus.setTextColor(resources.getColor(if (isActive) R.color.green else R.color.red, theme))
        btnUpdateGameId.text = getString(if (isActive) R.string.update else R.string.activate)
        etGameId.isEnabled = true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun handleInitializationSuccess(gameId: String, isTestMode: Boolean) {
        setLoadingState(false)
        updateStatusUI(gameId)
        showToast(getString(R.string.unity_ads_initialized, if (isTestMode) "test" else "live"), TastyToast.SUCCESS)
    }

    private fun handleInitializationFailure(message: String?) {
        setLoadingState(false)
        Log.e(TAG, "Initialization failed: $message")
        showToast(getString(R.string.initialization_failed, message ?: "Unknown error"), TastyToast.ERROR)
    }

    private fun setLoadingState(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnUpdateGameId.isEnabled = !isLoading
    }

    private fun showToast(message: String, type: Int) {
        TastyToast.makeText(this, message, TastyToast.LENGTH_SHORT, type)
    }
}

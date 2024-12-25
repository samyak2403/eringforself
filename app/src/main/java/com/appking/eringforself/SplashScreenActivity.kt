package com.appking.eringforself

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Using Coroutine for delay
        GlobalScope.launch {
            delay(3000) // 3 seconds delay
            withContext(Dispatchers.Main) {
                // Navigate to MainActivity
                val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                startActivity(intent)
                finish() // Close SplashScreenActivity
            }
        }
    }
}

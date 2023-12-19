package com.example.happyplaces.module.initialization

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.happyplaces.R
import com.example.happyplaces.module.modules.authentication.IntroductionActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler().postDelayed({
            // Start the Intro Activity
            startActivity(Intent(this@SplashActivity, IntroductionActivity::class.java))
            finish() // Call this when your activity is done and should be closed.
        }, 2500)
    }
}
package com.hrithikvish.cbsm

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.hrithikvish.cbsm.utils.Constants
import com.hrithikvish.cbsm.utils.SharedPrefManager

class SplashActivity : AppCompatActivity() {
    var sharedPrefManager: SharedPrefManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_splash)

        sharedPrefManager = SharedPrefManager(this@SplashActivity)

        val isLoggedIn = sharedPrefManager!!.getBoolean(Constants.LOGIN_SESSION_SHARED_PREF_KEY)

        if (isLoggedIn) {
            startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
        } else {
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        }

        finish()
    }
}
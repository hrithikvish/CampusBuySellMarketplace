package com.hrithikvish.cbsm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.hrithikvish.cbsm.utils.Constants;
import com.hrithikvish.cbsm.utils.SharedPrefManager;

public class SplashActivity extends AppCompatActivity {

    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_splash);

        sharedPrefManager = new SharedPrefManager(SplashActivity.this);

        boolean isLoggedIn = sharedPrefManager.getBoolean(Constants.LOGIN_SESSION_SHARED_PREF_KEY);

        if(isLoggedIn) {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }

        finish();

    }
}
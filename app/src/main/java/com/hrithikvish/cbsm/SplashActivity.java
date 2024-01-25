package com.hrithikvish.cbsm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                /*auth = FirebaseAuth.getInstance();
                if(auth.getCurrentUser()==null) {
                    startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                }*/
                boolean isLoggedIn = pref.getBoolean("isLoggedIn", false);
                System.out.println("Splash Activity- isLoggedIn: " + isLoggedIn);
                if(isLoggedIn) {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
                }
                finish();
            }
        }, 1000);

    }
}
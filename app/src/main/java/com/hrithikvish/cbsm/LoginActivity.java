package com.hrithikvish.cbsm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.hrithikvish.cbsm.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginBtn.setOnClickListener(view-> {
            binding.loginBtn.setText("");
            binding.progressBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    binding.loginBtn.setText("Login");
                    binding.progressBar.setVisibility(View.GONE);
                }
            }, 1000);
        });

        binding.goToSignUpPageText.setOnClickListener(view-> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        });

    }
}
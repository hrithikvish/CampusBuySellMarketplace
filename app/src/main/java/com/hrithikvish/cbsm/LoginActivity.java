package com.hrithikvish.cbsm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.hrithikvish.cbsm.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.goToSignUpPageText.setOnClickListener(view-> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        });

    }
}
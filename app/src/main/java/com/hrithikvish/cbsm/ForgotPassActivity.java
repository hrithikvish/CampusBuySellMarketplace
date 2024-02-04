package com.hrithikvish.cbsm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hrithikvish.cbsm.databinding.ActivityForgotPassBinding;

public class ForgotPassActivity extends AppCompatActivity {

    ActivityForgotPassBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(view-> {
            finish();
        });

    }
}
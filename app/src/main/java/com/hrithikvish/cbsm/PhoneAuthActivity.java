package com.hrithikvish.cbsm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hrithikvish.cbsm.databinding.ActivityPhoneAuthBinding;

public class PhoneAuthActivity extends AppCompatActivity {

    ActivityPhoneAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



    }
}
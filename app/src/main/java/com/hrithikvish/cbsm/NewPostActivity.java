package com.hrithikvish.cbsm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hrithikvish.cbsm.databinding.ActivityNewPostBinding;

public class NewPostActivity extends AppCompatActivity {

    ActivityNewPostBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



    }
}
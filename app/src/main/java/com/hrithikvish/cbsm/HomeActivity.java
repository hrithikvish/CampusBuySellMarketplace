package com.hrithikvish.cbsm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.hrithikvish.cbsm.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.logoutBtn.setOnClickListener(view->{
            super.finish();
        });

        Intent intent = getIntent();
        binding.homeText.setText(intent.getStringExtra("name"));
        String photoUrl = intent.getStringExtra("profileUrl");
        System.out.println("PHOTOURL: " + photoUrl);
        Glide.with(this).load(photoUrl).into(binding.profileImg);
    }
}
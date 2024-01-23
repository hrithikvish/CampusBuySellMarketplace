package com.hrithikvish.cbsm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.hrithikvish.cbsm.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        /*binding.logoutBtn.setOnClickListener(view->{
            super.finish();
        });*/

        Intent intent = getIntent();
        //sign up
        /*if(intent.getStringExtra("name") != null) {
            binding.homeText.setText(intent.getStringExtra("name"));
            String photoUrl = intent.getStringExtra("profileUrl");
            System.out.println("PHOTOURL: " + photoUrl);
            Glide.with(this).load(photoUrl).into(binding.profileImg);
        }
        else if(intent.getStringExtra("email") != null) {
            binding.homeText.setText(intent.getStringExtra("email"));
        }
        //login
        else if(intent.getStringExtra("Login Email") != null) {
            binding.homeText.setText(intent.getStringExtra("Login Email"));
        }
        else if(intent.getStringExtra("Login name") != null) {
            binding.homeText.setText(intent.getStringExtra("Login name"));
            String photoUrl = intent.getStringExtra("Login profileUrl");
            System.out.println("PHOTOURL: " + photoUrl);
            Glide.with(this).load(photoUrl).into(binding.profileImg);
        }*/

    }
}
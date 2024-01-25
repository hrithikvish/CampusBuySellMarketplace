package com.hrithikvish.cbsm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.hrithikvish.cbsm.databinding.ActivityHomeBinding;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        /*System.out.println(auth.getCurrentUser().getDisplayName());
        System.out.println(auth.getCurrentUser().getEmail());
        binding.userNameText.setText(auth.getCurrentUser().getDisplayName()==null ? auth.getCurrentUser().getEmail() : auth.getCurrentUser().getDisplayName());*/
        binding.userNameText.setText(Objects.requireNonNull(auth.getCurrentUser()).getEmail());

        binding.logoutBtn.setOnClickListener(view-> {
            auth.signOut();
            setSharedPref(false);
            startActivity(new Intent(HomeActivity.this, SignUpActivity.class));
        });

        Intent intent = getIntent();
        //sign up
        if(intent.getStringExtra("name") != null) {
            binding.userNameText.setText(intent.getStringExtra("name"));
            String photoUrl = intent.getStringExtra("profileUrl");
            System.out.println("PHOTOURL: " + photoUrl);
        }
        else if(intent.getStringExtra("email") != null) {
            binding.userNameText.setText(intent.getStringExtra("email"));
        }
        //login
        else if(intent.getStringExtra("Login Email") != null) {
            binding.userNameText.setText(intent.getStringExtra("Login Email"));
        }
        else if(intent.getStringExtra("Login name") != null) {
            binding.userNameText.setText(intent.getStringExtra("Login name"));
            String photoUrl = intent.getStringExtra("Login profileUrl");
            System.out.println("PHOTOURL: " + photoUrl);
        }

    }

    private void setSharedPref(Boolean isLoggedIn) {
        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        System.out.println("logout Activity- isLoggedIn: " + pref.getBoolean("isLoggedIn", false));
        editor.apply();
    }
}
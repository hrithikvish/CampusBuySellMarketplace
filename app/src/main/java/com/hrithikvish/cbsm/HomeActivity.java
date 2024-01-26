package com.hrithikvish.cbsm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

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

        binding.userNameText.setText(Objects.requireNonNull(auth.getCurrentUser()).getDisplayName().isEmpty() ? auth.getCurrentUser().getEmail() : auth.getCurrentUser().getDisplayName());

        binding.logoutBtn.setOnClickListener(view-> {
            auth.signOut();
            setSharedPref(false);
            startActivity(new Intent(HomeActivity.this, SignUpActivity.class));
        });

    }

    private void setSharedPref(Boolean isLoggedIn) {
        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        System.out.println("logout Activity- isLoggedIn: " + pref.getBoolean("isLoggedIn", false));
        editor.apply();
    }
}
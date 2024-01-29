package com.hrithikvish.cbsm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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
//
//        binding.userNameText.setText(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getDisplayName()).isEmpty() ? auth.getCurrentUser().getEmail() : auth.getCurrentUser().getDisplayName());

        binding.logoutBtn.setOnClickListener(view-> {
            auth.signOut();
            setSharedPref(false);
            startActivity(new Intent(HomeActivity.this, SignUpActivity.class));
        });

        binding.bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if(id == R.id.navHome) {
                    loadFragment(new HomeFragment());
                } else if (id == R.id.navExplore) {
                    loadFragment(new ExploreFragment());
                } else if (id == R.id.navPost) {
                    //loadFragment(new NewPostFragment());
                    startActivity(new Intent(HomeActivity.this, NewPostActivity.class));
                } else if (id == R.id.navChat) {
                    loadFragment(new ChatFragment());
                } else {
                    loadFragment(new ProfileFragment());
                }

                return true;
            }
        });

        binding.bottomNavView.setSelectedItemId(R.id.navProfile);

    }

    private void loadFragment(Fragment frag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout, frag);
        ft.commit();
    }

    private void setSharedPref(Boolean isLoggedIn) {
        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        System.out.println("logout Activity- isLoggedIn: " + pref.getBoolean("isLoggedIn", false));
        editor.apply();
    }
}
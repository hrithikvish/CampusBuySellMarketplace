package com.hrithikvish.cbsm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithikvish.cbsm.databinding.ActivityHomeBinding;
import com.hrithikvish.cbsm.model.UserProfile;
import com.hrithikvish.cbsm.utils.Constants;
import com.hrithikvish.cbsm.utils.SharedPrefManager;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    SharedPrefManager sharedPrefManager;
    FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        sharedPrefManager = new SharedPrefManager(HomeActivity.this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPrefManager.putInt(Constants.LAST_SELECTED_ITEM, R.id.navHome);

        binding.bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navHome) {
                    loadFragment(new HomeFragment());
                } else if (id == R.id.navExplore) {
                    loadFragment(new ExploreFragment());
                } else if (id == R.id.navPost) {
                    Intent intent = new Intent(HomeActivity.this, NewPostActivity.class);
                    saveCurrentFragment();
                    startActivity(intent);
                } else if (id == R.id.navChat) {
                    loadFragment(new ChatFragment());
                } else {
                    loadFragment(new ProfileFragment());
                }
                return true;
            }
        });

        binding.bottomNavView.setSelectedItemId(R.id.navHome);

        //user Profile
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String emailName = auth.getCurrentUser().getDisplayName();
                String name;
                if(snapshot.child("Users").child(auth.getUid()).child("name").getValue() != null) {
                    name = snapshot.child("Users").child(auth.getUid()).child("name").getValue().toString();
                } else {
                    name = "";
                }
                String email = auth.getCurrentUser().getEmail();
                String clgName = snapshot.child("Users").child(auth.getUid()).child("clg").getValue().toString();
                UserProfile userProfile = new UserProfile(emailName, email, clgName, name);
                sharedPrefManager.putObject(Constants.PROFILE_SHARED_PREF_KEY, userProfile);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    private void saveCurrentFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if (currentFragment instanceof HomeFragment) {
            sharedPrefManager.putInt(Constants.LAST_SELECTED_ITEM, R.id.navHome);
        } else if (currentFragment instanceof ExploreFragment) {
            sharedPrefManager.putInt(Constants.LAST_SELECTED_ITEM, R.id.navExplore);
        } else if (currentFragment instanceof ChatFragment) {
            sharedPrefManager.putInt(Constants.LAST_SELECTED_ITEM, R.id.navChat);
        } else {
            sharedPrefManager.putInt(Constants.LAST_SELECTED_ITEM, R.id.navProfile);
        }
    }

    private void loadFragment(Fragment frag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout, frag);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if(!(currentFragment instanceof HomeFragment)) {
            binding.bottomNavView.setSelectedItemId(R.id.navHome);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //getting last Active Fragment
        int lastSelectedItem = sharedPrefManager.getInt(Constants.LAST_SELECTED_ITEM);
        binding.bottomNavView.setSelectedItemId(lastSelectedItem);
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveCurrentFragment();
    }
}
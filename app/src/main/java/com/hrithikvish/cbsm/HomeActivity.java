package com.hrithikvish.cbsm;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithikvish.cbsm.databinding.ActivityHomeBinding;
import com.hrithikvish.cbsm.utils.Constants;
import com.hrithikvish.cbsm.utils.SharedPrefManager;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    SharedPrefManager sharedPrefManager;
    GoogleSignInClient googleSignInClient;
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

        binding.logoutBtn.setOnClickListener(view-> {
            //auth.signOut();
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(Constants.CLIENT_ID).requestEmail().build();
            googleSignInClient = GoogleSignIn.getClient(this, gso);
            googleSignInClient.signOut();

            sharedPrefManager.putBoolean(Constants.LOGIN_SESSION_SHARED_PREF_KEY, false);
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
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

        binding.bottomNavView.setSelectedItemId(R.id.navHome);

        //user Profile
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = auth.getCurrentUser().getDisplayName();
                String email = auth.getCurrentUser().getEmail();
                String clgName = snapshot.child("Users").child(auth.getUid()).child("clg").getValue().toString();
                UserProfile userProfile = new UserProfile(name, email, clgName);
                sharedPrefManager.putObject(Constants.PROFILE_SHARED_PREF_KEY, userProfile);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadFragment(Fragment frag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout, frag);
        ft.commit();
    }

}
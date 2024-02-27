package com.hrithikvish.cbsm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.hrithikvish.cbsm.databinding.FragmentProfileBinding;
import com.hrithikvish.cbsm.utils.Constants;
import com.hrithikvish.cbsm.utils.SharedPrefManager;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    SharedPrefManager sharedPrefManager;
    UserProfile userProfile;
    Gson gson;
    ViewPagerPostsAndSavedAdapter viewPagerPostsAndSavedAdapter;
    GoogleSignInClient googleSignInClient;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        if(user.getDisplayName() == null || user.getDisplayName().isEmpty()) {
            binding.nameText.setText(user.getEmail());
        } else {
            binding.nameText.setText(user.getDisplayName());
        }
        binding.clgText.setText(userProfile.getClg());

        binding.editProfileBtn.setOnClickListener(view-> Toast.makeText(requireContext(), "Yeah Mr White", Toast.LENGTH_SHORT).show());

        binding.postsAndSavedViewPager.setAdapter(viewPagerPostsAndSavedAdapter);
        binding.postsAndSavedTabLayout.setupWithViewPager(binding.postsAndSavedViewPager);

        binding.logoutBtn.setOnClickListener(view-> {
            //auth.signOut();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure, you want to logout?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(Constants.CLIENT_ID).requestEmail().build();
                    googleSignInClient = GoogleSignIn.getClient(getContext(), gso);
                    googleSignInClient.signOut();

                    sharedPrefManager.putBoolean(Constants.LOGIN_SESSION_SHARED_PREF_KEY, false);
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {}
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        binding.appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isCollapsed = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    // Collapsed
                    if (!isCollapsed) {
                        binding.mineToolbar.setBackgroundColor(getActivity().getResources().getColor(R.color.my_color_primary));
                        if(user.getDisplayName() == null || user.getDisplayName().isEmpty()) {
                            String string = user.getEmail();
                            String newString = string.substring(0, string.length() - 10);
                            binding.mineToolbar.setTitle(newString);
                        } else {
                            binding.mineToolbar.setTitle(user.getDisplayName());
                        }
                        isCollapsed = true;
                    }
                } else {
                    // Not collapsed
                    if (isCollapsed) {
                        binding.mineToolbar.setBackgroundColor(getActivity().getResources().getColor(android.R.color.transparent));
                        binding.collapsingToolbar.setTitle("");
                        isCollapsed = false;
                    }
                }
            }
        });



        //do not write code below this
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPrefManager = new SharedPrefManager(getContext());
        gson = new Gson();
        String userProfileJson = sharedPrefManager.getObject(Constants.PROFILE_SHARED_PREF_KEY);
        userProfile = gson.fromJson(userProfileJson, UserProfile.class);
            viewPagerPostsAndSavedAdapter = new ViewPagerPostsAndSavedAdapter(getActivity().getSupportFragmentManager());
    }
}

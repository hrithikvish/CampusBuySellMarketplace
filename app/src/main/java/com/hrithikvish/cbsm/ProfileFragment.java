package com.hrithikvish.cbsm;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

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
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        if(user.getDisplayName() == null || user.getDisplayName().isEmpty()) {
            binding.nameText.setText(user.getEmail());
        } else {
            binding.nameText.setText(user.getDisplayName());
        }
        binding.clgText.setText(userProfile.getClg());


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
    }
}

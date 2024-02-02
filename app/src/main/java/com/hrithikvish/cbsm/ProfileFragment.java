package com.hrithikvish.cbsm;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hrithikvish.cbsm.databinding.FragmentProfileBinding;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        binding.uidText.setText("UID: " + user.getUid());

        if(user.getDisplayName() == null || user.getDisplayName().isEmpty()) {
            binding.nameText.setText(user.getEmail());
            Log.d("Email", "Profile Fragment: " + user.getEmail());
        } else {
            binding.nameText.setText(user.getDisplayName());
            Log.d("Display Name", "Profile Fragment: " + user.getDisplayName());
        }


        //do not write code below this
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

    }
}
package com.hrithikvish.cbsm;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithikvish.cbsm.databinding.FragmentExploreBinding;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ExploreFragment extends Fragment {

    FragmentExploreBinding binding;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(getLayoutInflater(), container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        binding.button.setOnClickListener(view-> {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        ArrayList<String> list = (ArrayList<String>) snapshot.child("Users").child(auth.getUid()).child("dummySavedPosted").child("saved").getValue();
                        Log.d("SET DS", list.toString()+"");
                    } catch (Exception e) {
                        Log.d("ERROR", e.getLocalizedMessage());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });

        binding.button3.setOnClickListener(view-> {
            HashMap<String, Object> listMap = new HashMap<>();
            ArrayList<String> dummyList = new ArrayList<>();
            dummyList.add("Post 2");
            dummyList.add("Post 5");
            dummyList.add("Post 42");
            dummyList.add("Post 35");
            listMap.put("dummyList", dummyList);

            databaseReference.child("Dummy LIST").updateChildren(listMap);
        });

        binding.button2.setOnClickListener(view-> {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        ArrayList<String> list = (ArrayList<String>) snapshot.child("Dummy LIST").child("dummyList").getValue();
                        Log.d("LIST DS", list.toString() + "");
                        Log.d("LIST CHECK DATA", String.valueOf(list.contains("Post 41")));
                    } catch (Exception e) {
                        Log.d("ERROR", e.getLocalizedMessage());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        });




        //
        return binding.getRoot();
    }
}
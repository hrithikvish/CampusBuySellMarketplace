package com.hrithikvish.cbsm;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.hrithikvish.cbsm.adapter.SavedPostsRVAdapter;
import com.hrithikvish.cbsm.databinding.FragmentProfileSavedBinding;

import java.util.ArrayList;
import java.util.Collections;

import com.hrithikvish.cbsm.model.PostModelClassForRV;

public class ProfileSavedFragment extends Fragment {
    FragmentProfileSavedBinding binding;

    DatabaseReference databaseReference;
    FirebaseAuth auth;
    SavedPostsRVAdapter adapter;
    ArrayList<PostModelClassForRV> list;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileSavedBinding.inflate(getLayoutInflater(), container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        list = new ArrayList<>();

        binding.userPostsRecyclerView.setHasFixedSize(true);
        binding.userPostsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), GridLayoutManager.VERTICAL, false));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if(!snapshot.child("Users").child(auth.getUid()).child("savedPosts").exists()) {
                    return;
                }
                binding.noPostsYet.setVisibility(View.GONE);
//                binding.progressBar.setVisibility(View.VISIBLE);
                ArrayList<String> savedPostsList = (ArrayList<String>) snapshot.child("Users").child(auth.getUid()).child("savedPosts").getValue();
                for (String savedPost : savedPostsList) {
                    if(savedPost == null) continue;
                    try {
                        PostModelClassForRV post = snapshot.child("Posts").child(savedPost).getValue(PostModelClassForRV.class);
                        post.setPostId(snapshot.child("Posts").child(savedPost).getKey());
                        list.add(post);
                    } catch(Exception e) {
                        Log.d("ERROOROR", e.getLocalizedMessage()+"");
                    }
                }
                Collections.reverse(list);
//                binding.progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        adapter = new SavedPostsRVAdapter(getContext(), list);
        binding.userPostsRecyclerView.setAdapter(adapter);

        //forbidden
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
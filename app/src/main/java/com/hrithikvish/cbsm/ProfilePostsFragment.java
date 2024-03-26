package com.hrithikvish.cbsm;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithikvish.cbsm.databinding.FragmentProfilePostsBinding;

import java.util.ArrayList;
import java.util.Collections;

import com.hrithikvish.cbsm.adapter.UserPostsRVAdapter;
import com.hrithikvish.cbsm.model.PostModelClassForRV;

public class ProfilePostsFragment extends Fragment {

    FragmentProfilePostsBinding binding;

    DatabaseReference databaseReference;
    FirebaseAuth auth;
    UserPostsRVAdapter adapter;
    ArrayList<PostModelClassForRV> list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfilePostsBinding.inflate(inflater, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        list = new ArrayList<>();

        binding.userPostsRecyclerView.setHasFixedSize(true);
        binding.userPostsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if(!snapshot.child("Users").child(auth.getUid()).child("userPosts").exists()) {
                    return;
                }
                binding.noPostsYet.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);
                ArrayList<String> userPostsList = (ArrayList<String>) snapshot.child("Users").child(auth.getUid()).child("userPosts").getValue();
                for (String userPosts : userPostsList) {
                    PostModelClassForRV post = snapshot.child("Posts").child(userPosts).getValue(PostModelClassForRV.class);
                    post.setPostId(snapshot.child("Posts").child(userPosts).getKey());
                    list.add(post);
                }
                Collections.reverse(list);
                binding.progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        adapter = new UserPostsRVAdapter(getContext(), list);
        binding.userPostsRecyclerView.setAdapter(adapter);


        //don't go below this comment
        return binding.getRoot();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
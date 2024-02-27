package com.hrithikvish.cbsm;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithikvish.cbsm.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;

    DatabaseReference databaseReference;
    PostsRVAdapter adapter;
    ArrayList<PostModalClassForRV> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding =  FragmentHomeBinding.inflate(getLayoutInflater(), container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        list = new ArrayList<>();

        binding.userPostsRecyclerView.setHasFixedSize(true);
        //binding.userPostsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
        binding.userPostsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.child("Posts").getChildren()) {
                    PostModalClassForRV post = dataSnapshot.getValue(PostModalClassForRV.class);
                    post.setPostId(dataSnapshot.getKey());
                    list.add(post);
                }
                Collections.reverse(list);
                binding.progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        adapter = new PostsRVAdapter(getContext(), list);
        binding.userPostsRecyclerView.setAdapter(adapter);

        //search bar

        // forbidden
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
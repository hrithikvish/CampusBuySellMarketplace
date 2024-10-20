package com.hrithikvish.cbsm;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithikvish.cbsm.adapter.AllPostsRVAdapter;
import com.hrithikvish.cbsm.databinding.FragmentHomeBinding;
import com.hrithikvish.cbsm.model.PostModelClassForRV;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;

    DatabaseReference databaseReference;
    AllPostsRVAdapter adapter;
    ArrayList<PostModelClassForRV> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding =  FragmentHomeBinding.inflate(getLayoutInflater(), container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        list = new ArrayList<>();

        initRV(binding.userPostsRecyclerView);
        binding.searchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().isEmpty()) {
                    adapter.filterList(list);
                }
                ArrayList<PostModelClassForRV> filteredList = new ArrayList<>();
                for(PostModelClassForRV post : list) {
                    if(post.getTitle().toLowerCase().contains(editable.toString().toLowerCase()) || post.getBody().toLowerCase().contains(editable.toString().toLowerCase())) {
                        filteredList.add(post);
                    }
                }
                adapter.filterList(filteredList);
            }
        });

        // forbidden
        return binding.getRoot();
    }

    private void initRV(RecyclerView rv) {
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.child("Posts").getChildren()) {
                    PostModelClassForRV post = dataSnapshot.getValue(PostModelClassForRV.class);
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
        adapter = new AllPostsRVAdapter(getContext(), list);
        rv.setAdapter(adapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
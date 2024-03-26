package com.hrithikvish.cbsm;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.hrithikvish.cbsm.adapter.ExploreParentRVAdapter;
import com.hrithikvish.cbsm.databinding.FragmentExploreBinding;
import com.hrithikvish.cbsm.model.ParentItemModelClassForRV;
import com.hrithikvish.cbsm.model.PostModelClassForRV;

import java.util.ArrayList;
import java.util.Collections;

public class ExploreFragment extends Fragment {

    FragmentExploreBinding binding;
    DatabaseReference databaseReference;
    FirebaseAuth auth;

    ArrayList<ParentItemModelClassForRV> collegesListMain;
    ExploreParentRVAdapter parentRVAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(getLayoutInflater(), container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        collegesListMain = new ArrayList<>();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<String> collegesList = (ArrayList<String>) snapshot.child("Colleges").child("collegesList").getValue();
                System.out.println(collegesList);

                for(int i = 0; i < collegesList.size(); i++) {

                    ArrayList<String> tempPostList = (ArrayList<String>) snapshot.child("Colleges").child("collegesPost").child(String.valueOf(i)).getValue();
                    System.out.println(tempPostList);

                    if(tempPostList == null) {
                        continue;
                    }

                    ArrayList<PostModelClassForRV> postList = new ArrayList<>();
                    for(String post : tempPostList) {
                        PostModelClassForRV postClass = snapshot.child("Posts").child(post).getValue(PostModelClassForRV.class);
                        postList.add(postClass);
                    }
                    Collections.reverse(postList);
                    collegesListMain.add(new ParentItemModelClassForRV(collegesList.get(i), postList));

                }
                parentRVAdapter = new ExploreParentRVAdapter(getContext(), collegesListMain);
                binding.parentRV.setHasFixedSize(true);
                binding.parentRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                binding.parentRV.setAdapter(parentRVAdapter);
                parentRVAdapter.notifyDataSetChanged();
                binding.progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


        //
        return binding.getRoot();
    }
}
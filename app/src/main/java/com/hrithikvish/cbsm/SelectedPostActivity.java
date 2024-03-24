package com.hrithikvish.cbsm;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithikvish.cbsm.databinding.ActivitySelectedPostBinding;
import com.hrithikvish.cbsm.model.PostModelClassForRV;

public class SelectedPostActivity extends AppCompatActivity {

    ActivitySelectedPostBinding binding;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectedPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = FirebaseDatabase.getInstance().getReference();
        PostModelClassForRV selectedItem = (PostModelClassForRV) getIntent().getSerializableExtra("selectedPost");

        binding.backArrow.setOnClickListener(view-> finish());

        Glide.with(this)
                .load(selectedItem.getPostImageUri())
                .into(binding.image);

        if(selectedItem.getBody().isEmpty()) {
            binding.desc.setText("No Description Available");
        } else {
            binding.desc.setText(selectedItem.getBody());
        }

        if(selectedItem.getTitle().isEmpty()) {
            binding.desc.setText("No Title Available");
        } else {
            binding.title.setText(selectedItem.getTitle());
        }

        //college
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.college.setText(snapshot.child("Users").child(selectedItem.getUser()).child("clg").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        binding.chatBtn.setOnClickListener(view -> {
            Toast.makeText(this, "UID: " + selectedItem.getUser(), Toast.LENGTH_SHORT).show();
        });
        binding.saveBtn.setOnClickListener(view -> {
            Toast.makeText(this, "Save?", Toast.LENGTH_SHORT).show();
        });


    }
}
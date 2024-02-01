package com.hrithikvish.cbsm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hrithikvish.cbsm.databinding.ActivityNewPostBinding;
import com.hrithikvish.cbsm.utils.FirebaseDatabaseHelper;

public class NewPostActivity extends AppCompatActivity {

    ActivityNewPostBinding binding;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.backBtn.setOnClickListener(view->{
            finish();
        });

        binding.nextBtn.setOnClickListener(view->{
            String title = binding.titleET.getText().toString().trim();
            String body = binding.bodyTextET.getText().toString().trim();
            addPostInFbDb(title, body);
            finish();
        });

    }
    private void addPostInFbDb(String postTitle, String postBody) {
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseDatabaseHelper firebaseDatabaseHelper = new FirebaseDatabaseHelper(NewPostActivity.this, auth, databaseReference);
        firebaseDatabaseHelper.addPostIntoFbDb(postTitle, postBody);
    }
}
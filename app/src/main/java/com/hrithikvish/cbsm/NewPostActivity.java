package com.hrithikvish.cbsm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hrithikvish.cbsm.databinding.ActivityNewPostBinding;
import com.hrithikvish.cbsm.utils.ActivityFinisher;
import com.hrithikvish.cbsm.utils.FirebaseDatabaseHelper;

public class NewPostActivity extends AppCompatActivity implements ActivityFinisher {

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

        binding.postBtn.setOnClickListener(view->{
            String title = binding.titleET.getText().toString().trim();
            String body = binding.bodyTextET.getText().toString().trim();
            changePostBtnToProgBar();
            addPostInFbDb(title, body);
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        changeBackDefaultPostBtn();
    }

    private void addPostInFbDb(String postTitle, String postBody) {
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseDatabaseHelper firebaseDatabaseHelper = new FirebaseDatabaseHelper(NewPostActivity.this, auth, databaseReference, this::finishActivity);
        firebaseDatabaseHelper.addPostIntoFbDb(postTitle, postBody);
    }
    private void changePostBtnToProgBar() {
        binding.postBar.setVisibility(View.VISIBLE);
        binding.postBtn.setText("");
    }
    private void changeBackDefaultPostBtn() {
        binding.postBar.setVisibility(View.GONE);
        binding.postBtn.setText("Post");
    }

    @Override
    public void finishActivity() {
        finish();
    }
}
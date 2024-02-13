package com.hrithikvish.cbsm;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hrithikvish.cbsm.databinding.ActivityNewPostBinding;
import com.hrithikvish.cbsm.utils.ActivityFinisher;
import com.hrithikvish.cbsm.utils.FirebaseDatabaseHelper;

public class NewPostActivity extends AppCompatActivity implements ActivityFinisher {

    ActivityNewPostBinding binding;
    FirebaseAuth auth;
    FirebaseDatabaseHelper firebaseDatabaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(NewPostActivity.this, auth, this::finishActivity);

        binding.backBtn.setOnClickListener(view->{
            finish();
        });

        binding.postBtn.setOnClickListener(view->{
            String title = binding.titleET.getText().toString().trim();
            String body = binding.bodyTextET.getText().toString().trim();
            changePostBtnToProgBar(true);
            firebaseDatabaseHelper.addPostIntoFbDb(title, body);
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        changePostBtnToProgBar(false);
    }

    private void changePostBtnToProgBar(Boolean isLoading) {
        if(isLoading) {
            binding.postBar.setVisibility(View.VISIBLE);
            binding.postBtn.setText("");
        } else {
            binding.postBar.setVisibility(View.GONE);
            binding.postBtn.setText("Post");
        }
    }

    @Override
    public void finishActivity() {
        finish();
    }
}
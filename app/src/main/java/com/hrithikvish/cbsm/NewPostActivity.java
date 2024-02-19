package com.hrithikvish.cbsm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hrithikvish.cbsm.databinding.ActivityNewPostBinding;
import com.hrithikvish.cbsm.utils.ActivityFinisher;
import com.hrithikvish.cbsm.utils.FirebaseDatabaseHelper;

import java.net.URI;

public class NewPostActivity extends AppCompatActivity implements ActivityFinisher {

    ActivityNewPostBinding binding;
    FirebaseAuth auth;
    StorageReference storageReference;
    FirebaseDatabaseHelper firebaseDatabaseHelper;
    static final int PICK_IMAGE = 1;
    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(NewPostActivity.this, binding, auth, storageReference, this::finishActivity);

        binding.backBtn.setOnClickListener(view->{
            finish();
        });

        binding.postBtn.setOnClickListener(view->{
            String title = binding.titleET.getText().toString().trim();
            String body = binding.bodyTextET.getText().toString().trim();
            changePostBtnToProgBar(true);
            Toast.makeText(NewPostActivity.this, "Please Wait...", Toast.LENGTH_LONG).show();
            firebaseDatabaseHelper.addPostIntoFbDb(title, body, imageUri);
        });

        binding.addImgIcon.setOnClickListener(view-> {
            selectImage();
        });

    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK && data!=null) {
            imageUri = data.getData();
            Glide.with(NewPostActivity.this)
                            .load(imageUri)
                            .into(binding.imageThumb);
            //binding.imageThumb.setImageURI(imageUri);
        }
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
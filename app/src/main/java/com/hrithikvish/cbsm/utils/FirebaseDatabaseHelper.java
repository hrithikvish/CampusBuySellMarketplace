package com.hrithikvish.cbsm.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hrithikvish.cbsm.HomeActivity;

import java.util.HashMap;

public class FirebaseDatabaseHelper {

    FirebaseAuth auth;
    Context context;
    DatabaseReference databaseReference;
    ActivityFinisher activityFinisher;

    public FirebaseDatabaseHelper(Context context, FirebaseAuth auth, DatabaseReference databaseReference) {
        this.context = context;
        this.auth = auth;
        this.databaseReference = databaseReference;
    }

    public FirebaseDatabaseHelper(Context context, FirebaseAuth auth, DatabaseReference databaseReference, ActivityFinisher activityFinisher) {
        this.context = context;
        this.auth = auth;
        this.databaseReference = databaseReference;
        this.activityFinisher = activityFinisher;
    }

    public void addUserIntoFbDb(String email) {
        String uid = auth.getCurrentUser().getUid();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.child("Users").child(uid).exists()) {
                    HashMap<String, Object> userData = new HashMap<>();
                    userData.put("email", email);

                    databaseReference.child("Users").child(uid).updateChildren(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(context, "User added in DB", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, HomeActivity.class);
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, "Oops! Something went wrong. Try Again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addPostIntoFbDb(String postTitle, String postBody) {
        String uid = auth.getUid();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("PostIdGenerator").child("currentId").exists()) {
                    long postID = (long) snapshot.child("PostIdGenerator").child("currentId").getValue();

                    String postId = "Post: " + (postID + 1);

                    HashMap<String, Object> newPostData = new HashMap<>();
                    newPostData.put("user", uid);
                    newPostData.put("title", postTitle);
                    newPostData.put("body", postBody);

                    databaseReference.child("Posts").child(postId).updateChildren(newPostData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                databaseReference.child("PostIdGenerator").child("currentId").setValue(postID+1);
                                Toast.makeText(context, "Post Added into Fb Db", Toast.LENGTH_SHORT).show();
                                activityFinisher.finishActivity();
                            } else {
                                Toast.makeText(context, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    databaseReference.child("PostIdGenerator").child("currentId").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
package com.hrithikvish.cbsm.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class FirebaseDatabaseHelper {

    FirebaseAuth auth;
    Context context;
    DatabaseReference databaseReference;

    public FirebaseDatabaseHelper(Context context, FirebaseAuth auth, DatabaseReference databaseReference) {
        this.context = context;
        this.auth = auth;
        this.databaseReference = databaseReference;
    }

    public void addUserIntoFbDb(String email, String pass) {
        String uid = auth.getCurrentUser().getUid();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.child("Users").child(uid).exists()) {
                    HashMap<String, Object> userData = new HashMap<>();
                    userData.put("email", email);
                    userData.put("password", pass);

                    databaseReference.child("Users").child(uid).updateChildren(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(context, "User added in DB", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to add in DB", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(context, "Post Added into Fb Db", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    databaseReference.child("PostIdGenerator").child("currentId").setValue(postID+1);

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

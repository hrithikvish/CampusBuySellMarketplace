package com.hrithikvish.cbsm.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hrithikvish.cbsm.HomeActivity;
import com.hrithikvish.cbsm.databinding.ActivityNewPostBinding;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class FirebaseDatabaseHelper {

    FirebaseAuth auth;
    Context context;
    DatabaseReference databaseReference;
    ActivityFinisher activityFinisher;
    SharedPrefManager sharedPrefManager;
    StorageReference storageReference;
    ActivityNewPostBinding binding;

    //

    public FirebaseDatabaseHelper(Context context, FirebaseAuth auth) {
        this.context = context;
        this.auth = auth;
        this.sharedPrefManager = new SharedPrefManager(context);
    }

    public FirebaseDatabaseHelper(Context context, FirebaseAuth auth, ActivityFinisher activityFinisher) {
        this.context = context;
        this.auth = auth;
        this.activityFinisher = activityFinisher;
        this.sharedPrefManager = new SharedPrefManager(context);
    }

    public FirebaseDatabaseHelper(Context context, ActivityNewPostBinding binding, FirebaseAuth auth, StorageReference storageReference, ActivityFinisher activityFinisher) {
        this.context = context;
        this.binding = binding;
        this.auth = auth;
        this.storageReference = storageReference;
        this.activityFinisher = activityFinisher;
        this.sharedPrefManager = new SharedPrefManager(context);
    }

    public void addUserIntoFbDb(String email, String clg) {
        String uid = auth.getCurrentUser().getUid();
        FirebaseUser user = auth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

/////////////////////////////////////
                HashMap<String, Object> collegesListMap = new HashMap<>();
                HashMap<String, Object> collegeUsersMap = new HashMap<>();

                ArrayList<String> collegesList = (ArrayList<String>) snapshot.child("Colleges").child("collegesList").getValue();

                if(collegesList == null || !collegesList.contains(clg)) {
                    updateCollegesList(clg, collegesList, uid);
                }

                if(collegesList != null && collegesList.contains(clg)) {
                    updateUserList(clg, uid);
                }

                ///////////////////

                HashMap<String, Object> userData = new HashMap<>();
                userData.put("email", email);
                if(user.getDisplayName() == null) {
                    userData.put("name", auth.getCurrentUser().getEmail());
                } else {
                    userData.put("name", auth.getCurrentUser().getDisplayName());
                }
                userData.put("clg", clg);

                databaseReference.child("Users").child(uid).updateChildren(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            sharedPrefManager.putBoolean(Constants.LOGIN_SESSION_SHARED_PREF_KEY, true);
                            Toast.makeText(context, "User added in DB", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, HomeActivity.class);
                            context.startActivity(intent);
                            activityFinisher.finishActivity();
                        } else {
                            Toast.makeText(context, "Oops! Something went wrong. Try Again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    // if(collegesList == null || !collegesList.contains(clg))
    private void updateCollegesList(String clg, ArrayList<String> collegesList, String uid) {
        HashMap<String, Object> collegesListMap = new HashMap<>();
        ArrayList<String> tempCollegesList = (collegesList == null) ? new ArrayList<>() : new ArrayList<>(collegesList);
        tempCollegesList.add(clg);
        collegesListMap.put("collegesList", tempCollegesList);
        databaseReference.child("Colleges").updateChildren(collegesListMap).addOnCompleteListener(task -> updateUserList(clg, uid));
    }

    private void updateUserList(String clg, String uid) {
        HashMap<String, Object> collegeUsersMap = new HashMap<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> newClgList = (ArrayList<String>) snapshot.child("Colleges").child("collegesList").getValue();
                ArrayList<String> userList = (ArrayList<String>) snapshot.child("Colleges").child("collegeUsers").child(String.valueOf(newClgList.indexOf(clg))).getValue();
                if (userList == null) {
                    userList = new ArrayList<>();
                }
                userList.add(uid);
                collegeUsersMap.put(String.valueOf(newClgList.indexOf(clg)), userList);
                databaseReference.child("Colleges").child("collegeUsers").updateChildren(collegeUsersMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void addPostIntoFbDb(String postTitle, String postBody, Uri imageUri) {
        String uid = auth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        addPostWithImageIntoFbDb(postTitle, postBody, imageUri, uid);
    }

    private void addPostWithImageIntoFbDb(String postTitle, String postBody, Uri imageUri, String uid) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("PostIdGenerator").child("currentId").exists()) {
                    long postIdLong = (long) snapshot.child("PostIdGenerator").child("currentId").getValue();
                    long postImageIdLong = (long) snapshot.child("PostImageIdGenerator").child("currentPostImageId").getValue();

                    String postId = "Post " + (postIdLong + 1);
                    String postImageId = "Image " + (postImageIdLong + 1);
                    StorageReference postImagePath = storageReference.child("PostImages").child(postImageId+".jpg");

                    byte[] compressedImage = compressImage(imageUri);

                    UploadTask uploadTask = postImagePath.putBytes(compressedImage);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if(!task.isSuccessful()) {
                                        Log.d("UPLOAD TASK ERROR", task.getException().getLocalizedMessage()+"");
                                    }
                                    return postImagePath.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()) {

                                        HashMap<String, Object> newPostData = new HashMap<>();
                                        newPostData.put("user", uid);
                                        newPostData.put("title", postTitle);
                                        newPostData.put("body", postBody);
                                        newPostData.put("datePosted", getCurrentDateString());
                                        newPostData.put("timePosted", getCurrentTimeString());
                                        newPostData.put("postImageUri", task.getResult().toString());

                                        databaseReference.child("Posts").child(postId).updateChildren(newPostData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    // updating the ID GENERATORS
                                                    databaseReference.child("PostIdGenerator").child("currentId").setValue(postIdLong+1);
                                                    databaseReference.child("PostImageIdGenerator").child("currentPostImageId").setValue(postImageIdLong+1);

                                                    //adding user posts in his profile
                                                    addPostDataIntoUsersNode(snapshot, uid, postId);
                                                    addPostInCollegesPostNode(snapshot, uid, postId);

                                                    Toast.makeText(context, "Your post is live now.", Toast.LENGTH_SHORT).show();
                                                    activityFinisher.finishActivity();
                                                } else {
                                                    Toast.makeText(context, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    } else {
                                        binding.postBar.setVisibility(View.GONE);
                                        binding.postBtn.setText("Post");
                                        Log.d("ADD POST ERROR", task.getException().getLocalizedMessage()+"");
                                        Toast.makeText(context, "Something Went Wrong, Try Again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });

                    //long clgId = (long) snapshot.child("Users").child(auth.getUid()).child("clgId").getValue();

                    //databaseReference.child("Colleges").child(clgId+"").child(postId).updateChildren(newPostData);

                } else {
                    databaseReference.child("PostIdGenerator").child("currentId").setValue(0);
                    addPostIntoFbDb(postTitle, postBody, imageUri);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void addPostDataIntoUsersNode(@NonNull DataSnapshot snapshot, String uid, String postId) {
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<String> userPostList;
        try {
            userPostList = (ArrayList<String>) snapshot.child("Users").child(uid).child("userPosts").getValue();
            if(userPostList == null) {
                userPostList = new ArrayList<>();
                userPostList.add(postId);
                map.put("userPosts", userPostList);
            } else {
                userPostList.add(postId);
                map.put("userPosts", userPostList);
            }
            databaseReference.child("Users").child(uid).updateChildren(map);
        } catch (Exception e) {Log.d("NULL U-P ARRAY", e.getLocalizedMessage()+"");}
    }

    private void addPostInCollegesPostNode(DataSnapshot snapshot, String uid, String postId) {
        ArrayList<String> clgList = (ArrayList<String>) snapshot.child("Colleges").child("collegesList").getValue();

        String clg = (String) snapshot.child("Users").child(uid).child("clg").getValue();

        ArrayList<String> collegePostsList = (ArrayList<String>) snapshot.child("Colleges").child("collegesPost").child(String.valueOf(clgList.indexOf(clg))).getValue();
        Log.d("INDEX", String.valueOf(clgList.indexOf(clg)));
        String clgIndexOrID = String.valueOf(clgList.indexOf(clg));

        HashMap<String, Object> collegesPostsMap = new HashMap<>();

        if(collegePostsList == null) {
            collegePostsList = new ArrayList<>();
            collegePostsList.add(postId);
            collegesPostsMap.put(clgIndexOrID, collegePostsList);
        } else {
            collegePostsList.add(postId);
            collegesPostsMap.put(clgIndexOrID, collegePostsList);
        }

        databaseReference.child("Colleges").child("collegesPost").updateChildren(collegesPostsMap);

    }

    private byte[] compressImage(Uri imageUri) {
        byte[] bytes = new byte[0];
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            float aspectRatio = (float) bitmap.getWidth() / bitmap.getHeight();
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 1000, (int) (1000/aspectRatio), true);

            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
            bytes = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            Log.d("COMPRESS IMAGE", e.getLocalizedMessage()+"");
        }
        return bytes;
    }

    private String getCurrentDateString() {
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM yyyy");
        return currentDate.format(Calendar.getInstance().getTime());
    }
    private String getCurrentTimeString() {
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm:ss a");
        return currentTime.format(Calendar.getInstance().getTime());
    }
}
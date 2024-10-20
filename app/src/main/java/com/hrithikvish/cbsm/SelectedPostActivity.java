package com.hrithikvish.cbsm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithikvish.cbsm.databinding.ActivitySelectedPostBinding;
import com.hrithikvish.cbsm.model.PostModelClassForRV;
import com.hrithikvish.cbsm.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectedPostActivity extends AppCompatActivity {

    ActivitySelectedPostBinding binding;
    DatabaseReference databaseReference;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectedPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        PostModelClassForRV selectedItem = (PostModelClassForRV) getIntent().getSerializableExtra("selectedPost");

        binding.backArrow.setOnClickListener(view-> finish());
        if(selectedItem.getUser().equals(auth.getCurrentUser().getUid())) {
            binding.deleteBtn.setVisibility(View.VISIBLE);
        }
        binding.deleteBtn.setOnClickListener(view-> {
            Toast.makeText(this, "Delete CLicked", Toast.LENGTH_SHORT).show();
        });

        Glide.with(this)
                .load(selectedItem.getPostImageUri())
                .error(R.drawable.noimage)
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
        binding.timePosted.setText(selectedItem.getTimePosted());
        binding.datePosted.setText(selectedItem.getDatePosted());
        binding.deleteBtn.setOnClickListener(view-> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure, you want to Delete this Item?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                    databaseReference.child("Posts").child(selectedItem.getPostId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        List<String> list = (List<String>) snapshot.child("Users").child(auth.getUid()).child("userPosts").getValue();
                                        list.remove(selectedItem.getPostId());
                                        databaseReference.child("Users").child(auth.getUid()).child("userPosts").setValue(list);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) { }
                                });

                                Toast.makeText(getApplicationContext(), "Item Delisted", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {}
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        //college
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.college.setText(snapshot.child("Users").child(selectedItem.getUser()).child("clg").getValue().toString());
                Object name = snapshot.child("Users").child(selectedItem.getUser()).child("name").getValue();
                if(name != null) {
                    binding.posterName.setText(name.toString());
                } else {binding.posterName.setText(snapshot.child("Users").child(selectedItem.getUser()).child("email").getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        binding.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        binding.imgCard.setOnClickListener(view-> {
            if(binding.image.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
                binding.image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                binding.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            binding.image.invalidate();
        });

        binding.chatBtn.setOnClickListener(view -> {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String mail = (String) snapshot.child("Users").child(selectedItem.getUser()).child("email").getValue();
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"+mail));
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        });

        //setting save btn as unsave btn if alreasy saved
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> savedPostList = (ArrayList<String>) snapshot.child("Users").child(auth.getUid()).child("savedPosts").getValue();

                if(savedPostList != null) {
                    if(savedPostList.contains(selectedItem.getPostId())) {
                        binding.saveBtn.setText("Unsave");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        binding.saveBtn.setOnClickListener(view -> {
            HashMap<String, Object> savedPostsMap = new HashMap<>();
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<String> savedPostList = (ArrayList<String>) snapshot.child("Users").child(auth.getUid()).child("savedPosts").getValue();

                    boolean removedTemp = false;

                    if(savedPostList != null) {
                        if(!savedPostList.contains(selectedItem.getPostId())) {
                            savedPostList.add(selectedItem.getPostId());
                            savedPostsMap.put("savedPosts", savedPostList);
                        } else if(savedPostList.contains(selectedItem.getPostId())) {
                            savedPostList.remove(selectedItem.getPostId());
                            removedTemp = true;
                            savedPostsMap.put("savedPosts", savedPostList);
                        }
                    } else {
                        savedPostList = new ArrayList<>();
                        savedPostList.add(selectedItem.getPostId());
                        savedPostsMap.put("savedPosts", savedPostList);
                    }
                    boolean removed = removedTemp;
                    databaseReference.child("Users").child(auth.getUid()).updateChildren(savedPostsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                if(removed) {
                                    Toast.makeText(getApplicationContext(), "Post removed from Saved", Toast.LENGTH_SHORT).show();
                                    binding.saveBtn.setText("Save");
                                } else {
                                    Toast.makeText(getApplicationContext(), "Post Saved", Toast.LENGTH_SHORT).show();
                                    binding.saveBtn.setText("Unsave");
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        });

    }
}
package com.hrithikvish.cbsm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithikvish.cbsm.utils.FirebaseDatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class PostsRVAdapter extends RecyclerView.Adapter<PostsRVAdapter.postsRVViewHolder> {

    Context context;
    ArrayList<PostModalClassForRV> postList;

    public PostsRVAdapter(Context context, ArrayList<PostModalClassForRV> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public postsRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_posts_item_card_view, parent, false);
        return new postsRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull postsRVViewHolder holder, int position) {
        PostModalClassForRV post = postList.get(position);
        holder.postTitle.setText(post.getTitle());
        Glide.with(context)
                .load(post.getPostImageUri())
                .into(holder.postImage);
        holder.datePosted.setText(post.getDatePosted());
        //holder.clg.setText(post.get());

        //click listeners
        holder.savePost.setOnClickListener(view-> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            HashMap<String, Object> map = new HashMap<>();
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<String> savedPostList = (ArrayList<String>) snapshot.child("Users").child(auth.getUid()).child("savedPosts").getValue();
                    if(savedPostList == null) {
                        savedPostList = new ArrayList<>();
                        savedPostList.add(post.getPostId());
                        map.put("savedPosts", savedPostList);
                    } else {
                        savedPostList.add(post.getPostId());
                        map.put("savedPosts", savedPostList);
                    }
                    databaseReference.child("Users").child(auth.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(context, "Post Saved", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class postsRVViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle, clg, datePosted;
        ImageView postImage, savePost;
        public postsRVViewHolder(@NonNull View itemView) {
            super(itemView);

            postTitle = itemView.findViewById(R.id.postTitleTextView);
            clg = itemView.findViewById(R.id.postClgTextView);
            datePosted = itemView.findViewById(R.id.postDatePostedTextView);
            postImage = itemView.findViewById(R.id.postImageView);
            savePost = itemView.findViewById(R.id.savePostBtn);

        }
    }
}

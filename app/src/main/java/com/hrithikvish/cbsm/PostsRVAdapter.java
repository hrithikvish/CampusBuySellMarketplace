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

            HashMap<String, Object> savedPostsMap = new HashMap<>();
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<String> savedPostList = (ArrayList<String>) snapshot.child("Users").child(auth.getUid()).child("savedPosts").getValue();

                    boolean removedTemp = false;

                    if(savedPostList != null) {
                        if(!savedPostList.contains(post.getPostId())) {
                            savedPostList.add(post.getPostId());
                            savedPostsMap.put("savedPosts", savedPostList);
                        } else if(savedPostList.contains(post.getPostId())) {
                            savedPostList.remove(post.getPostId());
                            removedTemp = true;
                            savedPostsMap.put("savedPosts", savedPostList);
                        }
                    } else {
                        savedPostList = new ArrayList<>();
                        savedPostList.add(post.getPostId());
                        savedPostsMap.put("savedPosts", savedPostList);
                    }
                    boolean removed = removedTemp;
                    databaseReference.child("Users").child(auth.getUid()).updateChildren(savedPostsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                if(removed) {
                                    Toast.makeText(context, "Post removed from Saved", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Post Saved", Toast.LENGTH_SHORT).show();
                                }
                                //holder.savePost.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_bookmark_24));
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

        holder.itemView.setOnClickListener(view-> {
            /*PostModalClassForRV selectedPost = postList.get(position);
            Intent intent = new Intent(context, SelectedPost.class);
            intent.putExtra("selectedPost", selectedPost);
            context.startActivity(intent);*/
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class postsRVViewHolder extends RecyclerView.ViewHolder{
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

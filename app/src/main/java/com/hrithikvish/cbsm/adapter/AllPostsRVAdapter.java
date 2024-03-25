package com.hrithikvish.cbsm.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import com.hrithikvish.cbsm.SelectedPostActivity;
import com.hrithikvish.cbsm.model.PostModelClassForRV;
import com.hrithikvish.cbsm.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AllPostsRVAdapter extends RecyclerView.Adapter<AllPostsRVAdapter.postsRVViewHolder> {

    Context context;
    ArrayList<PostModelClassForRV> postList;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public AllPostsRVAdapter(Context context, ArrayList<PostModelClassForRV> postList) {
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
        PostModelClassForRV post = postList.get(position);
        if(post.getTitle().isEmpty()) {
            holder.postTitle.setText("No Title Available");
        } else {
            holder.postTitle.setText(post.getTitle());
        }

        Glide.with(context)
                .load(post.getPostImageUri())
                .error(R.drawable.noimage2)
                .into(holder.postImage);

        holder.datePosted.setText(post.getDatePosted());
        if(post.getBody().isEmpty()) {
            holder.postBody.setText("No Description Available");
        } else {
            holder.postBody.setText(post.getBody());
        }
        //setting college name
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.clg.setText(snapshot.child("Users").child(post.getUser()).child("clg").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        holder.threeDot.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(context, holder.threeDot);
            popup.inflate(R.menu.all_posts_item_three_dot_items);
            dynamicallyShowOrHideDeletePostItem(post, popup);
            dynamicallySetPopMenuSavePostItem(post, popup);
            handlePopupAndClicks(popup, context, post);
            popup.show();
        });

        holder.itemView.setOnLongClickListener(view -> {
            PopupMenu popup = new PopupMenu(context, holder.threeDot);
            popup.inflate(R.menu.all_posts_item_three_dot_items);
            dynamicallyShowOrHideDeletePostItem(post, popup);
            dynamicallySetPopMenuSavePostItem(post, popup);
            handlePopupAndClicks(popup, context, post);
            return true;
        });

        holder.itemView.setOnClickListener(view-> {
            PostModelClassForRV selectedPost = postList.get(position);
            Intent intent = new Intent(context, SelectedPostActivity.class);
            intent.putExtra("selectedPost", selectedPost);
            context.startActivity(intent);
        });

    }

    private void dynamicallyShowOrHideDeletePostItem(PostModelClassForRV post, PopupMenu popup) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> userPostList = (ArrayList<String>) snapshot.child("Users").child(auth.getUid()).child("userPosts").getValue();

                if (userPostList != null) {
                    if (!userPostList.contains(post.getPostId())) {
                        MenuItem deleteMenuItem = popup.getMenu().findItem(R.id.delete);
                        deleteMenuItem.setVisible(false);
                    }
                }
                popup.show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void dynamicallySetPopMenuSavePostItem(PostModelClassForRV post, PopupMenu popup) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> savedPostList = (ArrayList<String>) snapshot.child("Users").child(auth.getUid()).child("savedPosts").getValue();

                if(savedPostList != null) {
                    if(savedPostList.contains(post.getPostId())) {
                        MenuItem menuItem = popup.getMenu().findItem(R.id.save);
                        menuItem.setTitle("Unsave Item");
                    }
                }
                popup.show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void handlePopupAndClicks(PopupMenu popup, Context context, PostModelClassForRV post) {
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.save) {

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
                                    } else {
                                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                }
                else if (id == R.id.chat) {
                    Toast.makeText(context, "Chat Clicked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Delete CLicked", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class postsRVViewHolder extends RecyclerView.ViewHolder{
        TextView postTitle, postBody, clg, datePosted, threeDot;
        ImageView postImage;
        public postsRVViewHolder(@NonNull View itemView) {
            super(itemView);

            postTitle = itemView.findViewById(R.id.postTitle);
            postBody = itemView.findViewById(R.id.postBody);
            clg = itemView.findViewById(R.id.clgName);
            datePosted = itemView.findViewById(R.id.datePosted);
            postImage = itemView.findViewById(R.id.postImageView);
            threeDot = itemView.findViewById(R.id.threeDot);
        }
    }
}

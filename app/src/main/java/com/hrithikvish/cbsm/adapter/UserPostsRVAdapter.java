package com.hrithikvish.cbsm.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hrithikvish.cbsm.SelectedPostActivity;
import com.hrithikvish.cbsm.model.PostModelClassForRV;
import com.hrithikvish.cbsm.R;

import java.util.ArrayList;

public class UserPostsRVAdapter extends RecyclerView.Adapter<UserPostsRVAdapter.userPostsRVViewHolder> {

    Context context;
    ArrayList<PostModelClassForRV> list;

    public UserPostsRVAdapter(Context context, ArrayList<PostModelClassForRV> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public userPostsRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_posts_item_card_view, parent, false);
        return new userPostsRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull userPostsRVViewHolder holder, int position) {
        PostModelClassForRV post = list.get(position);
        holder.datePosted.setText(post.getDatePosted());

        Glide.with(context)
                .load(post.getPostImageUri())
                .into(holder.postImage);

        holder.itemView.setOnClickListener(view-> {
            PostModelClassForRV selectedPost = list.get(position);
            Intent intent = new Intent(context, SelectedPostActivity.class);
            intent.putExtra("selectedPost", selectedPost);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class userPostsRVViewHolder extends RecyclerView.ViewHolder {
        TextView datePosted;
        ImageView postImage;
        public userPostsRVViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.postImageView);
            datePosted = itemView.findViewById(R.id.datePostedText);
        }
    }
}
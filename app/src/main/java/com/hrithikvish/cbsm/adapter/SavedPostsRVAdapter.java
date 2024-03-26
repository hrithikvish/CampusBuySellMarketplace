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
import com.hrithikvish.cbsm.R;
import com.hrithikvish.cbsm.SelectedPostActivity;
import com.hrithikvish.cbsm.model.PostModelClassForRV;

import java.util.ArrayList;

public class SavedPostsRVAdapter extends RecyclerView.Adapter<SavedPostsRVAdapter.savedPostsRVViewHolder> {

    Context context;
    ArrayList<PostModelClassForRV> list;

    public SavedPostsRVAdapter(Context context, ArrayList<PostModelClassForRV> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public savedPostsRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.saved_posts_item_card_view, parent, false);
        return new savedPostsRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull savedPostsRVViewHolder holder, int position) {
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

    public static class savedPostsRVViewHolder extends RecyclerView.ViewHolder {
        TextView datePosted;
        ImageView postImage;
        public savedPostsRVViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.postImageView);
            datePosted = itemView.findViewById(R.id.datePostedText);
        }
    }
}
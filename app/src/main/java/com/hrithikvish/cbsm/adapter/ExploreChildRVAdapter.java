package com.hrithikvish.cbsm.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

public class ExploreChildRVAdapter extends RecyclerView.Adapter<ExploreChildRVAdapter.ChildViewHolder> {

    Context context;
    ArrayList<PostModelClassForRV> postList;

    public ExploreChildRVAdapter(Context context, ArrayList<PostModelClassForRV> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.explore_page_child_rv_item, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        PostModelClassForRV post = postList.get(position);

        if(post != null) {
            if(post.getTitle().isEmpty()) {
                holder.postTitle.setText("No Title Available");
            } else {
                holder.postTitle.setText(post.getTitle());
            }
            if(post.getBody().isEmpty()) {
                holder.postBody.setText("No Description Available");
            } else {
                holder.postBody.setText(post.getBody());
            }
            holder.datePosted.setText(post.getDatePosted());
            Glide.with(context)
                    .load(post.getPostImageUri())
                    .into(holder.postImage);

            holder.itemView.setOnClickListener(view-> {
                PostModelClassForRV selectedPost = postList.get(position);
                Intent intent = new Intent(context, SelectedPostActivity.class);
                intent.putExtra("selectedPost", selectedPost);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder{
        TextView postTitle, postBody, datePosted;
        ImageView postImage;
        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);

            postTitle = itemView.findViewById(R.id.postTitle);
            postBody = itemView.findViewById(R.id.postBody);
            datePosted = itemView.findViewById(R.id.datePosted);
            postImage = itemView.findViewById(R.id.postImage);

        }
    }
}

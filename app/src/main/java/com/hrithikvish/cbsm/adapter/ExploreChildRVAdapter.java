package com.hrithikvish.cbsm.adapter;

import android.content.Context;
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

        Log.d("ChildAdapter", "Post Type: " + post.getClass().getName());

        holder.postTitle.setText(post.getTitle());
        Glide.with(context)
                .load(post.getPostImageUri())
                .into(holder.postImage);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder{
        TextView postTitle;
        ImageView postImage;
        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);

            postTitle = itemView.findViewById(R.id.postTitle);
            postImage = itemView.findViewById(R.id.postImage);

        }
    }
}

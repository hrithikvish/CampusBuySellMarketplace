package com.hrithikvish.cbsm.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hrithikvish.cbsm.R
import com.hrithikvish.cbsm.SelectedPostActivity
import com.hrithikvish.cbsm.adapter.ExploreChildRVAdapter.ChildViewHolder
import com.hrithikvish.cbsm.model.PostModelClassForRV

class ExploreChildRVAdapter(var context: Context, var postList: ArrayList<PostModelClassForRV?>?) :
    RecyclerView.Adapter<ChildViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.explore_page_child_rv_item, parent, false)
        return ChildViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val post = postList!![position]

        if (post != null) {
            if (post.title!!.isEmpty()) {
                holder.postTitle.text = "No Title Available"
            } else {
                holder.postTitle.text = post.title
            }
            if (post.body!!.isEmpty()) {
                holder.postBody.text = "No Description Available"
            } else {
                holder.postBody.text = post.body
            }
            holder.datePosted.text = post.datePosted
            Glide.with(context)
                .load(post.postImageUri)
                .into(holder.postImage)

            holder.itemView.setOnClickListener { view: View? ->
                val selectedPost = postList!![position]
                val intent = Intent(context, SelectedPostActivity::class.java)
                intent.putExtra("selectedPost", selectedPost)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return postList!!.size
    }

    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var postTitle: TextView = itemView.findViewById(R.id.postTitle)
        var postBody: TextView = itemView.findViewById(R.id.postBody)
        var datePosted: TextView = itemView.findViewById(R.id.datePosted)
        var postImage: ImageView =
            itemView.findViewById(R.id.postImage)
    }
}

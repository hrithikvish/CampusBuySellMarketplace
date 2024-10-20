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
import com.hrithikvish.cbsm.adapter.SavedPostsRVAdapter.savedPostsRVViewHolder
import com.hrithikvish.cbsm.model.PostModelClassForRV

class SavedPostsRVAdapter(var context: Context?, var list: ArrayList<PostModelClassForRV>?) :
    RecyclerView.Adapter<savedPostsRVViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): savedPostsRVViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.saved_posts_item_card_view, parent, false)
        return savedPostsRVViewHolder(view)
    }

    override fun onBindViewHolder(holder: savedPostsRVViewHolder, position: Int) {
        val post = list!![position]
        holder.title.text = post.title
        holder.datePosted.text = post.datePosted

        Glide.with(context!!)
            .load(post.postImageUri)
            .into(holder.postImage)

        holder.itemView.setOnClickListener { view: View? ->
            val selectedPost = list!![position]
            val intent = Intent(context, SelectedPostActivity::class.java)
            intent.putExtra("selectedPost", selectedPost)
            context!!.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    class savedPostsRVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var datePosted: TextView = itemView.findViewById(R.id.datePostedText)
        var title: TextView = itemView.findViewById(R.id.title)
        var postImage: ImageView =
            itemView.findViewById(R.id.postImageView)
    }
}
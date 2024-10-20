package com.hrithikvish.cbsm.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hrithikvish.cbsm.R
import com.hrithikvish.cbsm.SelectedPostActivity
import com.hrithikvish.cbsm.adapter.AllPostsRVAdapter.postsRVViewHolder
import com.hrithikvish.cbsm.model.PostModelClassForRV

class AllPostsRVAdapter(var context: Context?, var postList: ArrayList<PostModelClassForRV>?) :
    RecyclerView.Adapter<postsRVViewHolder>() {
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): postsRVViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.all_posts_item_card_view, parent, false)
        return postsRVViewHolder(view)
    }

    override fun onBindViewHolder(holder: postsRVViewHolder, position: Int) {
        val post = postList!![position]
        if (post.title!!.isEmpty()) {
            holder.postTitle.text = "No Title Available"
        } else {
            holder.postTitle.text = post.title
        }

        Glide.with(context!!)
            .load(post.postImageUri)
            .error(R.drawable.noimage2)
            .into(holder.postImage)

        holder.datePosted.text = post.datePosted
        holder.timePosted.text = post.timePosted
        if (post.body!!.isEmpty()) {
            holder.postBody.text = "No Description Available"
        } else {
            holder.postBody.text = post.body
        }
        //setting college name
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                holder.clg.text =
                    snapshot.child("Users").child(post.user!!).child("clg").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        holder.threeDot.setOnClickListener { view: View? ->
            val popup = PopupMenu(
                context, holder.threeDot
            )
            popup.inflate(R.menu.all_posts_item_three_dot_items)
            //dynamicallyShowOrHideDeletePostItem(post, popup);
            dynamicallySetPopMenuSavePostItem(post, popup)
            handlePopupAndClicks(popup, context!!, post)
            popup.show()
        }

        holder.itemView.setOnLongClickListener { view: View? ->
            val popup = PopupMenu(
                context, holder.threeDot
            )
            popup.inflate(R.menu.all_posts_item_three_dot_items)
            //dynamicallyShowOrHideDeletePostItem(post, popup);
            dynamicallySetPopMenuSavePostItem(post, popup)
            handlePopupAndClicks(popup, context!!, post)
            true
        }

        holder.itemView.setOnClickListener { view: View? ->
            val selectedPost = postList!![position]
            val intent = Intent(context, SelectedPostActivity::class.java)
            intent.putExtra("selectedPost", selectedPost)
            context!!.startActivity(intent)
        }
    }

    /*private void dynamicallyShowOrHideDeletePostItem(PostModelClassForRV post, PopupMenu popup) {
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
    }*/
    fun filterList(filteredList: ArrayList<PostModelClassForRV>?) {
        postList = filteredList!!
        notifyDataSetChanged()
    }

    private fun dynamicallySetPopMenuSavePostItem(post: PostModelClassForRV, popup: PopupMenu) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val savedPostList = snapshot.child("Users").child(
                    auth.uid!!
                ).child("savedPosts").value as ArrayList<String?>?

                if (savedPostList != null) {
                    if (savedPostList.contains(post.postId)) {
                        val menuItem = popup.menu.findItem(R.id.save)
                        menuItem.setTitle("Unsave Item")
                    }
                }
                popup.show()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun handlePopupAndClicks(
        popup: PopupMenu,
        context: Context,
        post: PostModelClassForRV
    ) {
        popup.setOnMenuItemClickListener { item ->
            val id = item.itemId
            if (id == R.id.save) {
                val savedPostsMap = HashMap<String, Any>()
                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var savedPostList = snapshot.child("Users").child(
                            auth.uid!!
                        ).child("savedPosts").value as ArrayList<String?>?

                        var removedTemp = false

                        if (savedPostList != null) {
                            if (!savedPostList.contains(post.postId)) {
                                savedPostList.add(post.postId)
                                savedPostsMap["savedPosts"] = savedPostList
                            } else if (savedPostList.contains(post.postId)) {
                                savedPostList.remove(post.postId)
                                removedTemp = true
                                savedPostsMap["savedPosts"] = savedPostList
                            }
                        } else {
                            savedPostList = ArrayList()
                            savedPostList.add(post.postId)
                            savedPostsMap["savedPosts"] = savedPostList
                        }
                        val removed = removedTemp
                        databaseReference.child("Users").child(auth.uid!!)
                            .updateChildren(savedPostsMap).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    if (removed) {
                                        Toast.makeText(
                                            context,
                                            "Post removed from Saved",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Post Saved",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Something went wrong",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            } else {
                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val mail = snapshot.child("Users").child(post.user!!)
                            .child("email").value as String?
                        Log.d("MAIL", mail!!)
                        val intent = Intent(Intent.ACTION_SENDTO)
                        intent.setData(Uri.parse("mailto:$mail"))
                        context.startActivity(intent)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return postList!!.size
    }

    /*@Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterPattern = constraint.toString().toLowerCase().trim();
                List<PostModelClassForRV> filteredResults = new ArrayList<>();

                for (PostModelClassForRV data : postList) {
                    if (data.getTitle().toLowerCase().contains(filterPattern) || data.getBody().toLowerCase().contains(filterPattern)) {
                        filteredResults.add(data);
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList.clear();
                filteredList.addAll((List<PostModelClassForRV>) results.values);
                notifyDataSetChanged();
            }
        };
    }*/
    inner class postsRVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var postTitle: TextView = itemView.findViewById(R.id.postTitle)
        var postBody: TextView = itemView.findViewById(R.id.postBody)
        var clg: TextView = itemView.findViewById(R.id.clgName)
        var datePosted: TextView = itemView.findViewById(R.id.datePosted)
        var timePosted: TextView = itemView.findViewById(R.id.timePosted)
        var threeDot: TextView = itemView.findViewById(R.id.threeDot)
        var postImage: ImageView =
            itemView.findViewById(R.id.postImageView)
    }
}

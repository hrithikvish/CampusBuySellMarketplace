package com.hrithikvish.cbsm

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hrithikvish.cbsm.adapter.UserPostsRVAdapter
import com.hrithikvish.cbsm.databinding.FragmentProfilePostsBinding
import com.hrithikvish.cbsm.model.PostModelClassForRV
import java.util.Collections

class ProfilePostsFragment : Fragment() {
    var binding: FragmentProfilePostsBinding? = null

    var databaseReference: DatabaseReference? = null
    var auth: FirebaseAuth? = null
    var adapter: UserPostsRVAdapter? = null
    var list: ArrayList<PostModelClassForRV>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilePostsBinding.inflate(inflater, container, false)

        databaseReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        list = ArrayList()

        binding!!.userPostsRecyclerView.setHasFixedSize(true)
        binding!!.userPostsRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list!!.clear()
                if (!snapshot.child("Users").child(auth!!.uid!!).child("userPosts").exists()) {
                    return
                }
                binding!!.noPostsYet.visibility = View.GONE
                //binding.progressBar.setVisibility(View.VISIBLE);
                val userPostsList = snapshot.child("Users").child(
                    auth!!.uid!!
                ).child("userPosts").value as ArrayList<String>?
                for (userPosts in userPostsList!!) {
                    if (userPosts == null) continue
                    println(userPosts)
                    try {
                        val post = snapshot.child("Posts").child(userPosts).getValue(
                            PostModelClassForRV::class.java
                        )
                        post!!.postId = snapshot.child("Posts").child(userPosts).key
                        list!!.add(post)
                    } catch (e: Exception) {
                        Log.d("EROROROR", e.localizedMessage)
                    }
                }
                Collections.reverse(list)
                //binding.progressBar.setVisibility(View.GONE);
                adapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        adapter = UserPostsRVAdapter(context, list)
        binding!!.userPostsRecyclerView.adapter = adapter


        //don't go below this comment
        return binding!!.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
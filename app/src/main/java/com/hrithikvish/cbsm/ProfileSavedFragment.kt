package com.hrithikvish.cbsm

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hrithikvish.cbsm.adapter.SavedPostsRVAdapter
import com.hrithikvish.cbsm.databinding.FragmentProfileSavedBinding
import com.hrithikvish.cbsm.model.PostModelClassForRV
import java.util.Collections

class ProfileSavedFragment : Fragment() {
    var binding: FragmentProfileSavedBinding? = null

    var databaseReference: DatabaseReference? = null
    var auth: FirebaseAuth? = null
    var adapter: SavedPostsRVAdapter? = null
    var list: ArrayList<PostModelClassForRV>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileSavedBinding.inflate(
            layoutInflater, container, false
        )

        databaseReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        list = ArrayList()

        binding!!.userPostsRecyclerView.setHasFixedSize(true)
        binding!!.userPostsRecyclerView.layoutManager =
            LinearLayoutManager(context, GridLayoutManager.VERTICAL, false)
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list!!.clear()
                if (!snapshot.child("Users").child(auth!!.uid!!).child("savedPosts").exists()) {
                    return
                }
                binding!!.noPostsYet.visibility = View.GONE
                //                binding.progressBar.setVisibility(View.VISIBLE);
                val savedPostsList = snapshot.child("Users").child(
                    auth!!.uid!!
                ).child("savedPosts").value as ArrayList<String>?
                for (savedPost in savedPostsList!!) {
                    if (savedPost == null) continue
                    try {
                        val post = snapshot.child("Posts").child(savedPost).getValue(
                            PostModelClassForRV::class.java
                        )
                        post!!.postId = snapshot.child("Posts").child(savedPost).key
                        list!!.add(post)
                    } catch (e: Exception) {
                        Log.d("ERROOROR", e.localizedMessage + "")
                    }
                }
                Collections.reverse(list)
                //                binding.progressBar.setVisibility(View.GONE);
                adapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        adapter = SavedPostsRVAdapter(context, list)
        binding!!.userPostsRecyclerView.adapter = adapter

        //forbidden
        return binding!!.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
package com.hrithikvish.cbsm

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hrithikvish.cbsm.adapter.AllPostsRVAdapter
import com.hrithikvish.cbsm.databinding.FragmentHomeBinding
import com.hrithikvish.cbsm.model.PostModelClassForRV
import java.util.Collections
import java.util.Locale

class HomeFragment : Fragment() {
    var binding: FragmentHomeBinding? = null

    var databaseReference: DatabaseReference? = null
    var adapter: AllPostsRVAdapter? = null
    var list: ArrayList<PostModelClassForRV>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        databaseReference = FirebaseDatabase.getInstance().reference
        list = ArrayList()

        initRV(binding!!.userPostsRecyclerView)
        binding!!.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
                if (editable.toString().isEmpty()) {
                    adapter!!.filterList(list)
                }
                val filteredList = ArrayList<PostModelClassForRV>()
                for (post in list!!) {
                    if (post!!.title!!.lowercase(Locale.getDefault()).contains(
                            editable.toString().lowercase(
                                Locale.getDefault()
                            )
                        ) || post.body!!.lowercase(Locale.getDefault()).contains(
                            editable.toString().lowercase(
                                Locale.getDefault()
                            )
                        )
                    ) {
                        filteredList.add(post)
                    }
                }
                adapter!!.filterList(filteredList)
            }
        })

        // forbidden
        return binding!!.root
    }

    private fun initRV(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list!!.clear()
                for (dataSnapshot in snapshot.child("Posts").children) {
                    val post = dataSnapshot.getValue(
                        PostModelClassForRV::class.java
                    )
                    post!!.postId = dataSnapshot.key
                    list!!.add(post)
                }
                Collections.reverse(list)
                binding!!.progressBar.visibility = View.GONE
                adapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        adapter = AllPostsRVAdapter(context, list)
        rv.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
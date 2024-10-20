package com.hrithikvish.cbsm

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.hrithikvish.cbsm.adapter.ExploreParentRVAdapter
import com.hrithikvish.cbsm.databinding.FragmentExploreBinding
import com.hrithikvish.cbsm.model.ParentItemModelClassForRV
import com.hrithikvish.cbsm.model.PostModelClassForRV
import java.util.Collections
import java.util.Locale

class ExploreFragment : Fragment() {
    var binding: FragmentExploreBinding? = null
    var databaseReference: DatabaseReference? = null
    var auth: FirebaseAuth? = null

    var collegesListMain: ArrayList<ParentItemModelClassForRV>? = null
    var parentRVAdapter: ExploreParentRVAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExploreBinding.inflate(layoutInflater, container, false)

        databaseReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        collegesListMain = ArrayList()

        databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val collegesList =
                    snapshot.child("Colleges").child("collegesList").value as ArrayList<String>?
                println(collegesList)

                for (i in collegesList!!.indices) {
                    val tempPostList = snapshot.child("Colleges").child("collegesPost")
                        .child(i.toString()).value as ArrayList<String>?
                    println(tempPostList)

                    if (tempPostList == null) {
                        continue
                    }

                    val postList = ArrayList<PostModelClassForRV?>()
                    for (post in tempPostList) {
                        val postClass = snapshot.child("Posts").child(post).getValue(
                            PostModelClassForRV::class.java
                        )
                        postList.add(postClass)
                    }
                    Collections.reverse(postList)
                    collegesListMain!!.add(ParentItemModelClassForRV(collegesList[i], postList))
                }
                parentRVAdapter = ExploreParentRVAdapter(context, collegesListMain)
                binding!!.parentRV.setHasFixedSize(true)
                binding!!.parentRV.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding!!.parentRV.adapter = parentRVAdapter
                parentRVAdapter!!.notifyDataSetChanged()
                binding!!.progressBar.visibility = View.INVISIBLE
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        binding!!.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) { }

            override fun afterTextChanged(editable: Editable) {
                if (editable.toString().isEmpty()) {
                    parentRVAdapter!!.filterList(collegesListMain!!)
                }
                val filteredList = ArrayList<ParentItemModelClassForRV>()
                for (post in collegesListMain!!) {
                    if (post.collegeName!!.lowercase(Locale.getDefault()).contains(
                            editable.toString().lowercase(
                                Locale.getDefault()
                            )
                        )
                    ) {
                        filteredList.add(post)
                    }
                }
                parentRVAdapter!!.filterList(filteredList)
            }
        })

        //
        return binding!!.root
    }
}
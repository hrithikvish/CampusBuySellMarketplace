package com.hrithikvish.cbsm

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hrithikvish.cbsm.databinding.ActivitySelectedPostBinding
import com.hrithikvish.cbsm.model.PostModelClassForRV

class SelectedPostActivity : AppCompatActivity() {
    var binding: ActivitySelectedPostBinding? = null
    var databaseReference: DatabaseReference? = null
    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectedPostBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)

        databaseReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        val selectedItem = intent.getSerializableExtra("selectedPost") as PostModelClassForRV?

        binding!!.backArrow.setOnClickListener { view: View? -> finish() }
        if (selectedItem!!.user == auth!!.currentUser!!.uid) {
            binding!!.deleteBtn.visibility = View.VISIBLE
        }
        binding!!.deleteBtn.setOnClickListener { view: View? ->
            Toast.makeText(this, "Delete CLicked", Toast.LENGTH_SHORT).show()
        }

        Glide.with(this)
            .load(selectedItem.postImageUri)
            .error(R.drawable.noimage)
            .into(binding!!.image)

        if (selectedItem.body!!.isEmpty()) {
            binding!!.desc.text = "No Description Available"
        } else {
            binding!!.desc.text = selectedItem.body
        }

        if (selectedItem.title!!.isEmpty()) {
            binding!!.desc.text = "No Title Available"
        } else {
            binding!!.title.text = selectedItem.title
        }
        binding!!.timePosted.text = selectedItem.timePosted
        binding!!.datePosted.text = selectedItem.datePosted
        binding!!.deleteBtn.setOnClickListener { view: View? ->
            val builder =
                AlertDialog.Builder(this)
            builder.setMessage("Are you sure, you want to Delete this Item?")
            builder.setPositiveButton(
                "Yes"
            ) { dialogInterface, i ->
                finish()
                databaseReference!!.child("Posts").child(selectedItem.postId!!).removeValue()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            databaseReference!!.addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val list =
                                        snapshot.child("Users").child(
                                            auth!!.uid!!
                                        )
                                            .child("userPosts").value as MutableList<String>?
                                    list!!.remove(selectedItem.postId)
                                    databaseReference!!.child("Users").child(auth!!.uid!!)
                                        .child("userPosts").setValue(list)
                                }

                                override fun onCancelled(error: DatabaseError) {}
                            })

                            Toast.makeText(
                                applicationContext,
                                "Item Delisted",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
            builder.setNegativeButton(
                "Cancel"
            ) { dialogInterface, i -> }

            val dialog = builder.create()
            dialog.show()
        }

        //college
        databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding!!.college.text =
                    snapshot.child("Users").child(selectedItem.user!!).child("clg").value.toString()
                val name = snapshot.child("Users").child(selectedItem.user!!).child("name").value
                if (name != null) {
                    binding!!.posterName.text = name.toString()
                } else {
                    binding!!.posterName.text =
                        snapshot.child("Users").child(selectedItem.user!!)
                            .child("email").value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        binding!!.image.scaleType = ImageView.ScaleType.CENTER_CROP
        binding!!.imgCard.setOnClickListener { view: View? ->
            if (binding!!.image.scaleType == ImageView.ScaleType.CENTER_CROP) {
                binding!!.image.scaleType = ImageView.ScaleType.FIT_CENTER
            } else {
                binding!!.image.scaleType = ImageView.ScaleType.CENTER_CROP
            }
            binding!!.image.invalidate()
        }

        binding!!.chatBtn.setOnClickListener { view: View? ->
            databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mail = snapshot.child("Users").child(selectedItem.user!!)
                        .child("email").value as String?
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.setData(Uri.parse("mailto:$mail"))
                    startActivity(intent)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        //setting save btn as unsave btn if alreasy saved
        databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val savedPostList = snapshot.child("Users").child(
                    auth!!.uid!!
                ).child("savedPosts").value as ArrayList<String>?

                if (savedPostList != null) {
                    if (savedPostList.contains(selectedItem.postId)) {
                        binding!!.saveBtn.text = "Unsave"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        binding!!.saveBtn.setOnClickListener { view: View? ->
            val savedPostsMap =
                HashMap<String, Any>()
            databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var savedPostList =
                        snapshot.child("Users").child(
                            auth!!.uid!!
                        ).child("savedPosts").value as ArrayList<String?>?

                    var removedTemp = false

                    if (savedPostList != null) {
                        if (!savedPostList.contains(selectedItem.postId)) {
                            savedPostList.add(selectedItem.postId)
                            savedPostsMap["savedPosts"] = savedPostList
                        } else if (savedPostList.contains(selectedItem.postId)) {
                            savedPostList.remove(selectedItem.postId)
                            removedTemp = true
                            savedPostsMap["savedPosts"] = savedPostList
                        }
                    } else {
                        savedPostList = ArrayList()
                        savedPostList.add(selectedItem.postId)
                        savedPostsMap["savedPosts"] = savedPostList
                    }
                    val removed = removedTemp
                    databaseReference!!.child("Users").child(auth!!.uid!!)
                        .updateChildren(savedPostsMap)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                if (removed) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Post removed from Saved",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    binding!!.saveBtn.text = "Save"
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "Post Saved",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    binding!!.saveBtn.text = "Unsave"
                                }
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}
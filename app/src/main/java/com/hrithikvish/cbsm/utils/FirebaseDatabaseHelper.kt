package com.hrithikvish.cbsm.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.hrithikvish.cbsm.HomeActivity
import com.hrithikvish.cbsm.databinding.ActivityNewPostBinding
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar

class FirebaseDatabaseHelper {
    var auth: FirebaseAuth
    var context: Context
    var databaseReference: DatabaseReference? = null
    var activityFinisher: ActivityFinisher? = null
    var sharedPrefManager: SharedPrefManager
    var storageReference: StorageReference? = null
    var binding: ActivityNewPostBinding? = null

    //
    constructor(context: Context, auth: FirebaseAuth) {
        this.context = context
        this.auth = auth
        this.sharedPrefManager = SharedPrefManager(context)
    }

    constructor(context: Context, auth: FirebaseAuth, activityFinisher: () -> Unit) {
        this.context = context
        this.auth = auth
        //this.activityFinisher = activityFinisher
        this.sharedPrefManager = SharedPrefManager(context)
    }

    constructor(
        context: Context,
        binding: ActivityNewPostBinding?,
        auth: FirebaseAuth,
        storageReference: StorageReference?,
        activityFinisher: () -> Unit
    ) {
        this.context = context
        this.binding = binding
        this.auth = auth
        this.storageReference = storageReference
        //this.activityFinisher = activityFinisher
        this.sharedPrefManager = SharedPrefManager(context)
    }

    fun addUserIntoFbDb(email: String?, clg: String) {
        val uid = auth.currentUser!!.uid
        val user = auth.currentUser

        databaseReference = FirebaseDatabase.getInstance().reference
        databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val collegesListMap = HashMap<String, Any>()
                val collegeUsersMap = HashMap<String, Any>()

                val collegesList =
                    snapshot.child("Colleges").child("collegesList").value as ArrayList<String>?

                if (collegesList == null || !collegesList.contains(clg)) {
                    updateCollegesList(clg, collegesList, uid)
                }

                if (collegesList != null && collegesList.contains(clg)) {
                    updateUserList(clg, uid)
                }

                val userData = HashMap<String, Any?>()
                userData["email"] = email
                if (user!!.displayName == null) {
                    userData["name"] = auth.currentUser!!.email
                } else {
                    userData["name"] = auth.currentUser!!.displayName
                }
                userData["clg"] = clg

                databaseReference!!.child("Users").child(uid).updateChildren(userData)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            sharedPrefManager.putBoolean(
                                Constants.LOGIN_SESSION_SHARED_PREF_KEY,
                                true
                            )
                            Toast.makeText(context, "User added in DB", Toast.LENGTH_SHORT).show()
                            val intent = Intent(context, HomeActivity::class.java)
                            context.startActivity(intent)
                            activityFinisher!!.finishActivity()
                        } else {
                            Toast.makeText(
                                context,
                                "Oops! Something went wrong. Try Again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    // if(collegesList == null || !collegesList.contains(clg))
    private fun updateCollegesList(clg: String, collegesList: ArrayList<String>?, uid: String) {
        val collegesListMap = HashMap<String, Any>()
        val tempCollegesList = if ((collegesList == null)) ArrayList() else ArrayList(collegesList)
        tempCollegesList.add(clg)
        collegesListMap["collegesList"] = tempCollegesList
        databaseReference!!.child("Colleges").updateChildren(collegesListMap)
            .addOnCompleteListener { task: Task<Void?>? ->
                updateUserList(
                    clg,
                    uid
                )
            }
    }

    private fun updateUserList(clg: String, uid: String) {
        val collegeUsersMap = HashMap<String, Any>()
        databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newClgList =
                    snapshot.child("Colleges").child("collegesList").value as ArrayList<String>?
                var userList = snapshot.child("Colleges").child("collegeUsers").child(
                    newClgList!!.indexOf(clg).toString()
                ).value as ArrayList<String?>?
                if (userList == null) {
                    userList = ArrayList()
                }
                userList.add(uid)
                collegeUsersMap[newClgList.indexOf(clg).toString()] = userList
                databaseReference!!.child("Colleges").child("collegeUsers")
                    .updateChildren(collegeUsersMap)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun addPostIntoFbDb(postTitle: String, postBody: String, imageUri: Uri) {
        val uid = auth.uid
        databaseReference = FirebaseDatabase.getInstance().reference

        addPostWithImageIntoFbDb(postTitle, postBody, imageUri, uid!!)
    }

    private fun addPostWithImageIntoFbDb(
        postTitle: String,
        postBody: String,
        imageUri: Uri,
        uid: String
    ) {
        databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("PostIdGenerator").child("currentId").exists()) {
                    val postIdLong =
                        snapshot.child("PostIdGenerator").child("currentId").value as Long
                    val postImageIdLong = snapshot.child("PostImageIdGenerator")
                        .child("currentPostImageId").value as Long

                    val postId = "Post " + (postIdLong + 1)
                    val postImageId = "Image " + (postImageIdLong + 1)
                    val postImagePath = storageReference!!.child("PostImages").child(
                        "$postImageId.jpg"
                    )

                    val compressedImage = compressImage(imageUri)

                    val uploadTask = postImagePath.putBytes(compressedImage)
                    uploadTask.addOnSuccessListener {
                        val urlTask = uploadTask.continueWithTask<Uri> { task ->
                            if (!task.isSuccessful) {
                                Log.d(
                                    "UPLOAD TASK ERROR", task.exception!!
                                        .localizedMessage + ""
                                )
                            }
                            postImagePath.downloadUrl
                        }.addOnCompleteListener(object : OnCompleteListener<Uri> {
                            override fun onComplete(task: Task<Uri>) {
                                if (task.isSuccessful) {
                                    val newPostData = HashMap<String, Any>()
                                    newPostData["user"] = uid
                                    newPostData["title"] = postTitle
                                    newPostData["body"] = postBody
                                    newPostData["datePosted"] = currentDateString
                                    newPostData["timePosted"] = currentTimeString
                                    newPostData["postImageUri"] = task.result.toString()

                                    databaseReference!!.child("Posts").child(postId)
                                        .updateChildren(newPostData).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                // updating the ID GENERATORS
                                                databaseReference!!.child("PostIdGenerator")
                                                    .child("currentId").setValue(postIdLong + 1)
                                                databaseReference!!.child("PostImageIdGenerator")
                                                    .child("currentPostImageId")
                                                    .setValue(postImageIdLong + 1)

                                                //adding user posts in his profile
                                                addPostDataIntoUsersNode(snapshot, uid, postId)
                                                addPostInCollegesPostNode(snapshot, uid, postId)

                                                Toast.makeText(
                                                    context,
                                                    "Your post is live now.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                activityFinisher!!.finishActivity()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    task.exception!!.localizedMessage,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                } else {
                                    binding!!.postBar.visibility = View.GONE
                                    binding!!.postBtn.text = "Post"
                                    Log.d(
                                        "ADD POST ERROR",
                                        task.exception!!.localizedMessage + ""
                                    )
                                    Toast.makeText(
                                        context,
                                        "Something Went Wrong, Try Again.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        })
                    }

                    //long clgId = (long) snapshot.child("Users").child(auth.getUid()).child("clgId").getValue();

                    //databaseReference.child("Colleges").child(clgId+"").child(postId).updateChildren(newPostData);
                } else {
                    databaseReference!!.child("PostIdGenerator").child("currentId").setValue(0)
                    addPostIntoFbDb(postTitle, postBody, imageUri)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun addPostDataIntoUsersNode(snapshot: DataSnapshot, uid: String, postId: String) {
        val map = HashMap<String, Any>()
        var userPostList: ArrayList<String?>?
        try {
            userPostList =
                snapshot.child("Users").child(uid).child("userPosts").value as ArrayList<String?>?
            if (userPostList == null) {
                userPostList = ArrayList()
                userPostList.add(postId)
                map["userPosts"] = userPostList
            } else {
                userPostList.add(postId)
                map["userPosts"] = userPostList
            }
            databaseReference!!.child("Users").child(uid).updateChildren(map)
        } catch (e: Exception) {
            Log.d("NULL U-P ARRAY", e.localizedMessage + "")
        }
    }

    private fun addPostInCollegesPostNode(snapshot: DataSnapshot, uid: String, postId: String) {
        val clgList = snapshot.child("Colleges").child("collegesList").value as ArrayList<String?>?

        val clg = snapshot.child("Users").child(uid).child("clg").value as String?

        var collegePostsList = snapshot.child("Colleges").child("collegesPost").child(
            clgList!!.indexOf(clg).toString()
        ).value as ArrayList<String?>?
        Log.d("INDEX", clgList.indexOf(clg).toString())
        val clgIndexOrID = clgList.indexOf(clg).toString()

        val collegesPostsMap = HashMap<String, Any>()

        if (collegePostsList == null) {
            collegePostsList = ArrayList()
            collegePostsList.add(postId)
            collegesPostsMap[clgIndexOrID] = collegePostsList
        } else {
            collegePostsList.add(postId)
            collegesPostsMap[clgIndexOrID] = collegePostsList
        }

        databaseReference!!.child("Colleges").child("collegesPost").updateChildren(collegesPostsMap)
    }

    private fun compressImage(imageUri: Uri): ByteArray {
        var bytes = ByteArray(0)
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            val byteArrayOutputStream = ByteArrayOutputStream()

            val aspectRatio = bitmap.width.toFloat() / bitmap.height
            val scaledBitmap =
                Bitmap.createScaledBitmap(bitmap, 1000, (1000 / aspectRatio).toInt(), true)

            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream)
            bytes = byteArrayOutputStream.toByteArray()
        } catch (e: Exception) {
            Log.d("COMPRESS IMAGE", e.localizedMessage + "")
        }
        return bytes
    }

    private val currentDateString: String
        get() {
            val currentDate = SimpleDateFormat("dd MMM yyyy")
            return currentDate.format(Calendar.getInstance().time)
        }
    private val currentTimeString: String
        get() {
            val currentTime = SimpleDateFormat("hh:mm:ss a")
            return currentTime.format(Calendar.getInstance().time)
        }
}
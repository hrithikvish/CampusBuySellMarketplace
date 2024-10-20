package com.hrithikvish.cbsm

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hrithikvish.cbsm.databinding.ActivityNewPostBinding
import com.hrithikvish.cbsm.utils.ActivityFinisher
import com.hrithikvish.cbsm.utils.FirebaseDatabaseHelper

class NewPostActivity : AppCompatActivity(), ActivityFinisher {
    var binding: ActivityNewPostBinding? = null
    var auth: FirebaseAuth? = null
    var storageReference: StorageReference? = null
    var firebaseDatabaseHelper: FirebaseDatabaseHelper? = null
    var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        firebaseDatabaseHelper = FirebaseDatabaseHelper(
            this@NewPostActivity, binding, auth!!, storageReference
        ) { finishActivity() }

        binding!!.postBtn.isEnabled = false
        binding!!.titleET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                // Check if the EditText has text
                val isEditTextEmpty = binding!!.titleET.text.toString().trim { it <= ' ' }.isEmpty()
                // Enable the button if EditText is not empty, otherwise disable it
                binding!!.postBtn.isEnabled = !isEditTextEmpty
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        binding!!.backBtn.setOnClickListener { view: View? ->
            finish()
        }

        binding!!.addImgIcon.setOnClickListener { view: View? ->
            selectImage()
        }

        binding!!.postBtn.setOnClickListener { view: View? ->
            val title = binding!!.titleET.text.toString().trim { it <= ' ' }
            val body = binding!!.bodyTextET.text.toString().trim { it <= ' ' }
            changePostBtnToProgBar(true)
            Toast.makeText(this@NewPostActivity, "Please Wait...", Toast.LENGTH_LONG)
                .show()
            firebaseDatabaseHelper!!.addPostIntoFbDb(title, body, imageUri!!)
            Log.d("IMAGE URI LOG", imageUri.toString() + "")
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.setAction(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            Glide.with(this@NewPostActivity)
                .load(imageUri)
                .into(binding!!.imageThumb)
            //binding.imageThumb.setImageURI(imageUri);
        }
    }

    override fun onStop() {
        super.onStop()
        changePostBtnToProgBar(false)
    }

    private fun changePostBtnToProgBar(isLoading: Boolean) {
        if (isLoading) {
            binding!!.postBar.visibility = View.VISIBLE
            binding!!.postBtn.text = ""
        } else {
            binding!!.postBar.visibility = View.GONE
            binding!!.postBtn.text = "Post"
        }
    }

    override fun finishActivity() {
        finish()
    }

    companion object {
        const val PICK_IMAGE: Int = 1
    }
}
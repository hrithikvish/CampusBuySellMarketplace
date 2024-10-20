package com.hrithikvish.cbsm

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.util.IOUtils
import com.google.firebase.auth.FirebaseAuth
import com.hrithikvish.cbsm.databinding.ActivitySelectCollegeBinding
import com.hrithikvish.cbsm.utils.ActivityFinisher
import com.hrithikvish.cbsm.utils.FirebaseDatabaseHelper
import java.nio.charset.StandardCharsets
import java.util.Arrays
import java.util.zip.GZIPInputStream

class SelectCollegeActivity : AppCompatActivity(), ActivityFinisher {
    var binding: ActivitySelectCollegeBinding? = null
    var auth: FirebaseAuth? = null
    var firebaseDatabaseHelper: FirebaseDatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCollegeBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)

        auth = FirebaseAuth.getInstance()
        firebaseDatabaseHelper = FirebaseDatabaseHelper(
            this@SelectCollegeActivity, auth!!
        ) { this.finishActivity() }

        setClgAdapter()

        binding!!.continueBtn.setOnClickListener { view: View? ->
            changeRegBtnToLoading(true)
            firebaseDatabaseHelper!!.addUserIntoFbDb(auth!!.currentUser!!.email,
                binding!!.clgET.text.toString().trim { it <= ' ' })
        }
    }

    private fun setClgAdapter() {
        //decompressing Colleges_lIST GZip
        val colleges: MutableList<String> = ArrayList()
        try {
            val gzipInputStream = decompressCollegeGz()

            val data = IOUtils.toByteArray(gzipInputStream)

            val allClgesString = String(data, StandardCharsets.UTF_8)
            val individualColleges =
                allClgesString.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            colleges.addAll(Arrays.asList(*individualColleges))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        //College
        var adapter: ArrayAdapter<String>? = null
        try {
            adapter = ArrayAdapter(this, R.layout.college_dropdown_layout, colleges)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        binding!!.clgET.setAdapter(adapter)
    }

    override fun onStop() {
        super.onStop()
        changeRegBtnToLoading(false)
    }

    @Throws(Exception::class)
    private fun decompressCollegeGz(): GZIPInputStream {
        val inputStream = resources.openRawResource(R.raw.colleges_list_txt)
        return GZIPInputStream(inputStream)
    }

    private fun changeRegBtnToLoading(isLoading: Boolean) {
        if (isLoading) {
            binding!!.progressBar.visibility = View.VISIBLE
            binding!!.continueBtn.text = ""
        } else {
            binding!!.progressBar.visibility = View.GONE
            binding!!.continueBtn.text = "Register"
        }
    }

    override fun finishActivity() {
        finish()
    }
}
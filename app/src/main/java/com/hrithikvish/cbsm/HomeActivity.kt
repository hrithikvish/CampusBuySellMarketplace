package com.hrithikvish.cbsm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hrithikvish.cbsm.databinding.ActivityHomeBinding
import com.hrithikvish.cbsm.model.UserProfile
import com.hrithikvish.cbsm.utils.Constants
import com.hrithikvish.cbsm.utils.SharedPrefManager

class HomeActivity : AppCompatActivity() {
    var binding: ActivityHomeBinding? = null
    var sharedPrefManager: SharedPrefManager? = null
    var auth: FirebaseAuth? = null
    var databaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        auth = FirebaseAuth.getInstance()
        sharedPrefManager = SharedPrefManager(this@HomeActivity)
        databaseReference = FirebaseDatabase.getInstance().reference
        sharedPrefManager!!.putInt(Constants.LAST_SELECTED_ITEM, R.id.navHome)

        binding!!.bottomNavView.setOnItemSelectedListener { item ->
            val id = item.itemId
            if (id == R.id.navHome) {
                loadFragment(HomeFragment())
            } else if (id == R.id.navExplore) {
                loadFragment(ExploreFragment())
            } else if (id == R.id.navPost) {
                val intent = Intent(this@HomeActivity, NewPostActivity::class.java)
                saveCurrentFragment()
                startActivity(intent)
            } else {
                loadFragment(ProfileFragment())
            }
            true
        }

        binding!!.bottomNavView.selectedItemId = R.id.navHome

        //user Profile
        databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val emailName = auth!!.currentUser!!.displayName
                val name =
                    if (snapshot.child("Users").child(auth!!.uid!!).child("name").value != null) {
                        snapshot.child("Users").child(auth!!.uid!!).child("name").value.toString()
                    } else {
                        ""
                    }
                val email = auth!!.currentUser!!.email
                val clgName =
                    snapshot.child("Users").child(auth!!.uid!!).child("clg").value.toString()
                val userProfile = UserProfile(emailName, email, clgName, name)
                sharedPrefManager!!.putObject(Constants.PROFILE_SHARED_PREF_KEY, userProfile)
                Log.d("DONE", userProfile.toString())
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun saveCurrentFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
        if (currentFragment is HomeFragment) {
            sharedPrefManager!!.putInt(Constants.LAST_SELECTED_ITEM, R.id.navHome)
        } else if (currentFragment is ExploreFragment) {
            sharedPrefManager!!.putInt(Constants.LAST_SELECTED_ITEM, R.id.navExplore)
        } else {
            sharedPrefManager!!.putInt(Constants.LAST_SELECTED_ITEM, R.id.navProfile)
        }
    }

    private fun loadFragment(frag: Fragment) {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(R.id.frameLayout, frag)
        ft.commit()
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
        if (currentFragment !is HomeFragment) {
            binding!!.bottomNavView.selectedItemId = R.id.navHome
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        //getting last Active Fragment
        val lastSelectedItem = sharedPrefManager!!.getInt(Constants.LAST_SELECTED_ITEM)
        binding!!.bottomNavView.selectedItemId = lastSelectedItem
    }

    override fun onStop() {
        super.onStop()
        saveCurrentFragment()
    }
}
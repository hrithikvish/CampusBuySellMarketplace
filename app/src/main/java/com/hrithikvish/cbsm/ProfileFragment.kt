package com.hrithikvish.cbsm

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.hrithikvish.cbsm.adapter.ViewPagerPostsAndSavedAdapter
import com.hrithikvish.cbsm.databinding.FragmentProfileBinding
import com.hrithikvish.cbsm.model.UserProfile
import com.hrithikvish.cbsm.utils.Constants
import com.hrithikvish.cbsm.utils.SharedPrefManager

class ProfileFragment : Fragment() {
    var binding: FragmentProfileBinding? = null
    var auth: FirebaseAuth? = null
    var user: FirebaseUser? = null
    var databaseReference: DatabaseReference? = null
    var sharedPrefManager: SharedPrefManager? = null
    var userProfile: UserProfile? = null
    var gson: Gson? = null
    var viewPagerPostsAndSavedAdapter: ViewPagerPostsAndSavedAdapter? = null
    var googleSignInClient: GoogleSignInClient? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        gson = Gson()
        val userProfileJson = sharedPrefManager!!.getObject(Constants.PROFILE_SHARED_PREF_KEY)
        userProfile = gson!!.fromJson(userProfileJson, UserProfile::class.java)

        if (user!!.displayName == null || user!!.displayName!!.isEmpty()) {
            binding!!.nameText.text = user!!.email
        } else {
            binding!!.nameText.text = user!!.displayName
            //binding.editProfileBtn.setVisibility(View.GONE);
        }

        //setting name
        /*if(userProfile.getEmailName() != null) {
            binding.nameText.setText(userProfile.getEmailName());
            Log.d("EMAIL NAME", userProfile.getEmailName());
        } else {
            if(!userProfile.getName().isEmpty()) {
                binding.nameText.setText(userProfile.getName());
            } else {
                binding.nameText.setText(userProfile.getEmail());
            }
        }*/
        binding!!.clgText.text = userProfile!!.getClg()

        /*binding.editProfileBtn.setOnClickListener(view-> {
            binding.nameText.setVisibility(View.GONE);
            binding.nameETLayout.setVisibility(View.VISIBLE);
        });

        binding.confirmNameBtn.setOnClickListener(view-> {
            if(binding.nameET.getText().toString().isEmpty() || binding.nameET.getText().toString()==null) {
                binding.nameET.setError("First enter your name");
            } else {
                databaseReference.child("Users").child(auth.getUid()).child("name").setValue(binding.nameET.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            binding.nameText.setText(binding.nameET.getText().toString());

                            sharedPrefManager.putObject(Constants.PROFILE_SHARED_PREF_KEY, new UserProfile(userProfile.getEmailName(), userProfile.getEmail(), userProfile.getClg(), binding.nameET.getText().toString()));
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                });
                binding.nameText.setVisibility(View.VISIBLE);
                binding.nameETLayout.setVisibility(View.GONE);
            }
        });*/
        binding!!.closeEditNameLayout.setOnClickListener { view: View ->
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            binding!!.nameText.visibility = View.VISIBLE
            binding!!.nameETLayout.visibility = View.GONE
        }

        binding!!.postsAndSavedViewPager.adapter = viewPagerPostsAndSavedAdapter
        binding!!.postsAndSavedTabLayout.setupWithViewPager(binding!!.postsAndSavedViewPager)

        binding!!.logoutBtn.setOnClickListener { view: View? ->
            //auth.signOut();
            val builder = AlertDialog.Builder(activity)
            builder.setMessage("Are you sure, you want to logout?")
            builder.setPositiveButton(
                "Yes"
            ) { dialogInterface, i ->
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(Constants.CLIENT_ID)
                        .requestEmail().build()
                googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
                googleSignInClient!!.signOut()

                sharedPrefManager!!.putBoolean(
                    Constants.LOGIN_SESSION_SHARED_PREF_KEY,
                    false
                )
                startActivity(Intent(activity, LoginActivity::class.java))
                requireActivity().finish()
            }
            builder.setNegativeButton("Cancel") { dialogInterface, i -> }

            val dialog = builder.create()
            dialog.show()
        }

        binding!!.appbar.addOnOffsetChangedListener(object : OnOffsetChangedListener {
            var isCollapsed: Boolean = false
            var scrollRange: Int = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    // Collapsed
                    if (!isCollapsed) {
                        binding!!.mineToolbar.visibility = View.VISIBLE
                        binding!!.mineToolbar.setBackgroundColor(activity!!.resources.getColor(R.color.my_color_primary))
                        if (user!!.displayName == null || user!!.displayName!!.isEmpty()) {
                            val string = user!!.email
                            val newString = string!!.substring(0, string.length - 10)
                            binding!!.mineToolbar.title = newString
                        } else {
                            binding!!.mineToolbar.title = user!!.displayName
                        }
                        isCollapsed = true
                    }
                } else {
                    // Not collapsed
                    if (isCollapsed) {
                        binding!!.mineToolbar.visibility = View.INVISIBLE
                        binding!!.mineToolbar.setBackgroundColor(
                            activity!!.resources.getColor(
                                android.R.color.transparent
                            )
                        )
                        binding!!.collapsingToolbar.title = ""
                        isCollapsed = false
                    }
                }
            }
        })


        //do not write code below this
        return binding!!.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        databaseReference = FirebaseDatabase.getInstance().reference
        sharedPrefManager = SharedPrefManager(requireContext())
        viewPagerPostsAndSavedAdapter =
            ViewPagerPostsAndSavedAdapter(requireActivity().supportFragmentManager)
    }
}

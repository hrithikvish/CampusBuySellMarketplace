package com.hrithikvish.cbsm

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hrithikvish.cbsm.databinding.ActivitySignUpBinding
import com.hrithikvish.cbsm.utils.ActivityFinisher
import com.hrithikvish.cbsm.utils.Constants
import com.hrithikvish.cbsm.utils.FirebaseDatabaseHelper
import com.hrithikvish.cbsm.utils.SharedPrefManager

class SignUpActivity : AppCompatActivity(), ActivityFinisher {
    var binding: ActivitySignUpBinding? = null
    var sharedPrefManager: SharedPrefManager? = null
    var auth: FirebaseAuth? = null
    var googleSignInClient: GoogleSignInClient? = null
    var googleSignUnBtn: MaterialButton? = null
    var firebaseDatabaseHelper: FirebaseDatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        auth = FirebaseAuth.getInstance()
        firebaseDatabaseHelper = FirebaseDatabaseHelper(
            this@SignUpActivity, auth!!
        ) { this.finishActivity() }
        sharedPrefManager = SharedPrefManager(this@SignUpActivity)
        googleSignUnBtn = binding!!.googleSignUp as MaterialButton
        FirebaseApp.initializeApp(this)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(
            Constants.CLIENT_ID
        ).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding!!.goToLoginPageText.setOnClickListener { view: View? ->
            startActivity(
                Intent(
                    this@SignUpActivity,
                    LoginActivity::class.java
                )
            )
            finish()
        }

        binding!!.signUpBtn.setOnClickListener { view: View? ->
            val email = binding!!.emailET.text.toString().trim { it <= ' ' }
            val pass = binding!!.passET.text.toString().trim { it <= ' ' }
            val conPass = binding!!.conPassET.text.toString().trim { it <= ' ' }
            if (!email.isEmpty() && !pass.isEmpty() && !conPass.isEmpty()) {
                changeRegBtnToLoading(true)
                signUpUsingEmailPass(email, pass, conPass)
            } else {
                if (email.isEmpty()) {
                    binding!!.emailET.error = "Enter Email"
                }
                if (pass.isEmpty()) {
                    binding!!.passET.error = "Enter Password"
                }
                if (conPass.isEmpty()) {
                    binding!!.conPassET.error = "Re Enter Password"
                }
            }
        }

        binding!!.googleSignUp.setOnClickListener { view: View? ->
            changeGoogleRegBtnToLoading(true)
            val signInIntent = googleSignInClient!!.signInIntent
            activityResultLauncher.launch(signInIntent)
        }
    }

    override fun onStop() {
        super.onStop()
        changeRegBtnToLoading(false)
        changeGoogleRegBtnToLoading(false)
    }

    private fun signUpUsingEmailPass(email: String, pass: String, conPass: String) {
        if (validateData(email, pass, conPass)) {
            //String clg = binding.clgET.getText().toString().trim();
            auth!!.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(
                        this@SignUpActivity,
                        SelectCollegeActivity::class.java
                    )
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@SignUpActivity,
                        task.exception!!.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                    changeRegBtnToLoading(false)
                }
            }
        } else {
            changeRegBtnToLoading(false)
        }
    }

    private val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(activityResult.data)
            try {
                val googleSignInAccount = accountTask.getResult(
                    ApiException::class.java
                )
                val authCredential =
                    GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
                auth!!.signInWithCredential(authCredential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //sharedPrefManager.putBoolean(Constants.LOGIN_SESSION_SHARED_PREF_KEY, true);
                        auth = FirebaseAuth.getInstance()
                        val user = auth!!.currentUser
                        val email = user!!.email

                        val databaseReference = FirebaseDatabase.getInstance().reference
                        databaseReference.addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.child("Users").child(auth!!.uid!!).exists()) {
                                    startActivity(
                                        Intent(
                                            this@SignUpActivity,
                                            HomeActivity::class.java
                                        )
                                    )
                                } else {
                                    val intent = Intent(
                                        this@SignUpActivity,
                                        SelectCollegeActivity::class.java
                                    )
                                    startActivity(intent)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Something went wrong, try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                        changeGoogleRegBtnToLoading(false)
                    }
                }
            } catch (e: ApiException) {
                throw RuntimeException(e)
            }
        }
    }

    private fun validateData(email: String, pass: String, conPass: String): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding!!.emailET.error = "Invalid Email"
            return false
        }
        if (pass.length < 6) {
            binding!!.passET.error = "Password Length must be 6 or more"
            return false
        }
        if (conPass != pass) {
            binding!!.conPassET.error = "Password doesn't match"
            return false
        }
        /*if(binding.clgET.getText().toString().isEmpty()) {
            binding.clgET.setError("Select Your College");
            return false;
        }*/
        return true
    }

    private fun changeRegBtnToLoading(isLoading: Boolean) {
        if (isLoading) {
            binding!!.signUpBar.visibility = View.VISIBLE
            binding!!.signUpBtn.text = ""
        } else {
            binding!!.signUpBar.visibility = View.GONE
            binding!!.signUpBtn.text = "Register"
        }
    }

    private fun changeGoogleRegBtnToLoading(isLoading: Boolean) {
        if (isLoading) {
            binding!!.googleSignUp.text = ""
            googleSignUnBtn!!.icon = null
            binding!!.googleBar.visibility = View.VISIBLE
        } else {
            binding!!.googleBar.visibility = View.GONE
            googleSignUnBtn!!.setIconResource(R.drawable.icon_google)
            binding!!.googleSignUp.text = "Continue to Google"
        }
    }

    override fun finishActivity() {
        finish()
    }
}
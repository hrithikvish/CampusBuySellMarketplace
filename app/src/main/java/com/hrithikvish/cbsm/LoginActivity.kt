package com.hrithikvish.cbsm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
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
import com.hrithikvish.cbsm.databinding.ActivityLoginBinding
import com.hrithikvish.cbsm.utils.Constants
import com.hrithikvish.cbsm.utils.SharedPrefManager

class LoginActivity : AppCompatActivity() {
    var binding: ActivityLoginBinding? = null
    var sharedPrefManager: SharedPrefManager? = null
    var auth: FirebaseAuth? = null
    var googleSignInClient: GoogleSignInClient? = null
    var googleSignInBtn: MaterialButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        auth = FirebaseAuth.getInstance()
        FirebaseApp.initializeApp(this)
        sharedPrefManager = SharedPrefManager(this@LoginActivity)

        googleSignInBtn = binding!!.googleSignIn as MaterialButton

        val options =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(
                Constants.CLIENT_ID
            ).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, options)

        binding!!.loginBtn.setOnClickListener { view: View? ->
            changeLoginBtnToProgBar(true)
            val email = binding!!.emailET.text.toString()
            val pass = binding!!.passET.text.toString()
            if (!email.isEmpty() && !pass.isEmpty()) {
                signInUsingEmailPass(email, pass)
            } else {
                if (email.isEmpty()) {
                    binding!!.emailET.error = "Enter Email"
                }
                if (pass.isEmpty()) {
                    binding!!.passET.error = "Enter Password"
                }
                changeLoginBtnToProgBar(false)
            }
        }

        binding!!.googleSignIn.setOnClickListener { view: View? ->
            changeGoogleRegBtnToLoading(true)
            val signInIntent = googleSignInClient!!.signInIntent
            activityResultLauncher.launch(signInIntent)
        }

        binding!!.goToSignUpPageText.setOnClickListener { view: View? ->
            startActivity(
                Intent(
                    this@LoginActivity,
                    SignUpActivity::class.java
                )
            )
            finish()
        }

        binding!!.forgotPassText.setOnClickListener { view: View? ->
            startActivity(
                Intent(
                    this@LoginActivity,
                    ForgotPassActivity::class.java
                )
            )
        }
    }

    override fun onStop() {
        super.onStop()
        changeLoginBtnToProgBar(false)
        changeGoogleRegBtnToLoading(false)
    }

    private fun signInUsingEmailPass(email: String, pass: String) {
        auth = FirebaseAuth.getInstance()
        auth!!.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                sharedPrefManager!!.putBoolean(
                    Constants.LOGIN_SESSION_SHARED_PREF_KEY,
                    true
                )
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    task.exception!!.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
                changeLoginBtnToProgBar(false)
            }
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
                        val uid = auth!!.uid
                        val databaseReference = FirebaseDatabase.getInstance().reference
                        databaseReference.addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (!snapshot.child("Users").child(uid!!).child("clg")
                                        .exists()
                                ) {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "User Not Found, Sign Up",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    val gso =
                                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                            .requestIdToken(Constants.CLIENT_ID)
                                            .requestEmail().build()
                                    googleSignInClient = GoogleSignIn.getClient(
                                        this@LoginActivity,
                                        gso
                                    )
                                    googleSignInClient!!.signOut()
                                    changeGoogleRegBtnToLoading(false)
                                } else {
                                    sharedPrefManager!!.putBoolean(
                                        Constants.LOGIN_SESSION_SHARED_PREF_KEY,
                                        true
                                    )
                                    val intent = Intent(
                                        this@LoginActivity,
                                        HomeActivity::class.java
                                    )
                                    startActivity(intent)
                                    finish()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login Failed, Try Again!",
                            Toast.LENGTH_SHORT
                        ).show()
                        changeGoogleRegBtnToLoading(false)
                    }
                }.addOnFailureListener { e ->
                    Log.d(
                        "FAILURE",
                        e.localizedMessage
                    )
                }
            } catch (e: ApiException) {
                throw RuntimeException(e)
            }
        }
    }

    private fun changeLoginBtnToProgBar(isLoading: Boolean) {
        if (isLoading) {
            binding!!.loginBtn.text = ""
            binding!!.progressBar.visibility = View.VISIBLE
        } else {
            binding!!.loginBtn.text = "Login"
            binding!!.progressBar.visibility = View.GONE
        }
    }

    private fun changeGoogleRegBtnToLoading(isLoading: Boolean) {
        if (isLoading) {
            binding!!.googleSignIn.text = ""
            googleSignInBtn!!.icon = null
            binding!!.googleBar.visibility = View.VISIBLE
        } else {
            binding!!.googleBar.visibility = View.GONE
            googleSignInBtn!!.setIconResource(R.drawable.icon_google)
            binding!!.googleSignIn.text = "Continue to Google"
        }
    }
}
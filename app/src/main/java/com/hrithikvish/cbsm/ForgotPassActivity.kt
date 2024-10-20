package com.hrithikvish.cbsm

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.hrithikvish.cbsm.databinding.ActivityForgotPassBinding

class ForgotPassActivity : AppCompatActivity() {
    var binding: ActivityForgotPassBinding? = null
    var auth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPassBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        auth = FirebaseAuth.getInstance()

        binding!!.backBtn.setOnClickListener { view: View? ->
            finish()
        }

        binding!!.resetPassBtn.setOnClickListener { view: View? ->
            val resetEmail = binding!!.emailET.text.toString().trim { it <= ' ' }
            if (!resetEmail.isEmpty()) {
                changeResetPassBtnToProgBar(true)
                auth!!.sendPasswordResetEmail(resetEmail)
                    .addOnCompleteListener { task: Task<Void?> ->
                        if (task.isSuccessful) {
                            changeResetPassBtnToProgBar(false)
                            Toast.makeText(
                                this@ForgotPassActivity,
                                "Email sent to $resetEmail", Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this@ForgotPassActivity,
                                task.exception!!.localizedMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                            changeResetPassBtnToProgBar(false)
                        }
                    }
            } else {
                binding!!.emailET.error = "Enter Email Address"
                changeResetPassBtnToProgBar(false)
            }
        }
    }

    private fun changeResetPassBtnToProgBar(isLoading: Boolean) {
        if (isLoading) {
            binding!!.resetPassBtn.text = ""
            binding!!.progressBar.visibility = View.VISIBLE
        } else {
            binding!!.resetPassBtn.text = "Reset Password"
            binding!!.progressBar.visibility = View.GONE
        }
    }
}
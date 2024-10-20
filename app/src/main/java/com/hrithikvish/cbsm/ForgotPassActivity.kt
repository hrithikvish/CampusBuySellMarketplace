package com.hrithikvish.cbsm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.hrithikvish.cbsm.databinding.ActivityForgotPassBinding;

public class ForgotPassActivity extends AppCompatActivity {

    ActivityForgotPassBinding binding;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.backBtn.setOnClickListener(view-> {
            finish();
        });

        binding.resetPassBtn.setOnClickListener(view-> {
            String resetEmail = binding.emailET.getText().toString().trim();
            if(!resetEmail.isEmpty()) {
                changeResetPassBtnToProgBar(true);
                auth.sendPasswordResetEmail(resetEmail).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        changeResetPassBtnToProgBar(false);
                        Toast.makeText(ForgotPassActivity.this, "Email sent to " + resetEmail, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ForgotPassActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        changeResetPassBtnToProgBar(false);
                    }
                });
            } else {
                binding.emailET.setError("Enter Email Address");
                changeResetPassBtnToProgBar(false);
            }
        });
    }

    private void changeResetPassBtnToProgBar(Boolean isLoading) {
        if(isLoading) {
            binding.resetPassBtn.setText("");
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.resetPassBtn.setText("Reset Password");
            binding.progressBar.setVisibility(View.GONE);
        }
    }

}
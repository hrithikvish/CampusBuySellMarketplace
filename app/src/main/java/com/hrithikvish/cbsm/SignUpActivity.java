package com.hrithikvish.cbsm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.hrithikvish.cbsm.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.goToLoginPageText.setOnClickListener(view-> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });


        //change the method bitvh
        binding.signUpBtn.setOnClickListener(view->{
            binding.signUpBtn.setText("");
            binding.progressBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                    binding.signUpBtn.setText("Register");
                    binding.progressBar.setVisibility(View.GONE);
                }
            }, 2000);
        });

    }
}
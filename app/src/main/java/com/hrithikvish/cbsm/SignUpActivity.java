package com.hrithikvish.cbsm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

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

        //change the login bitvh
        binding.signUpBtn.setOnClickListener(view->{
            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
        });

    }
}
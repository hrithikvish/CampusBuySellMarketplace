package com.hrithikvish.cbsm;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.hrithikvish.cbsm.databinding.ActivitySignUpBinding;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    String clientId = "751206525517-rbhuic3hspp5k5hpohgd9ghp324mbj05.apps.googleusercontent.com";

    MaterialButton googleSignUnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        googleSignUnBtn = (MaterialButton) binding.googleSignUp;
        FirebaseApp.initializeApp(this);

        binding.goToLoginPageText.setOnClickListener(view-> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(clientId).requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(this, options);

        binding.signUpBtn.setOnClickListener(view-> {
            String email = binding.emailET.getText().toString();
            String pass = binding.passET.getText().toString();
            if(!email.isEmpty() && !pass.isEmpty()) {
                changeRegBtnToProgBar();
                signUpUsingEmailPass(binding.emailET.getText().toString().trim(), binding.passET.getText().toString().trim());
            } else {
                displayEmptyErrorMsg();
            }
        });

        binding.googleSignUp.setOnClickListener(view -> {
            changeGoogleBtnToProgBar();

            Intent signInIntent = googleSignInClient.getSignInIntent();
            activityResultLauncher.launch(signInIntent);
        });

        binding.phoneSignUp.setOnClickListener(view-> {
            startActivity(new Intent(SignUpActivity.this, PhoneAuthActivity.class));
        });

        binding.skipToHome.setOnClickListener(view-> {
            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
        });

    }

    private void displayEmptyErrorMsg() {
        binding.emailET.setError("Enter Email");
        binding.passET.setError("Enter Password");
    }

    private void changeGoogleBtnToProgBar() {
        binding.googleBar.setVisibility(View.VISIBLE);
        binding.googleSignUp.setText("");
        googleSignUnBtn.setIcon(null);
    }
    private void changeRegBtnToProgBar() {
        binding.signUpBar.setVisibility(View.VISIBLE);
        binding.signUpBtn.setText("");
    }

    @Override
    protected void onStop() {
        super.onStop();
        changeBackDefaultGoogleBtn();
        changeBackDefaultRegBtn();
    }

    private void changeBackDefaultGoogleBtn() {
        binding.googleBar.setVisibility(View.GONE);
        binding.googleSignUp.setText("Continue with Google");
        googleSignUnBtn.setIconResource(R.drawable.icon_google);
    }
    private void changeBackDefaultRegBtn() {
        binding.signUpBar.setVisibility(View.GONE);
        binding.signUpBtn.setText("Register");
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if(o.getResultCode() == RESULT_OK) {
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(o.getData());
                try {
                    GoogleSignInAccount googleSignInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                    auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                auth = FirebaseAuth.getInstance();
                                Toast.makeText(SignUpActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                intent.putExtra("name", Objects.requireNonNull(auth.getCurrentUser()).getDisplayName());
                                intent.putExtra("profileUrl", Objects.requireNonNull(auth.getCurrentUser().getPhotoUrl()).toString());
                                startActivity(intent);
                            } else {
                                Toast.makeText(SignUpActivity.this, "Registration Failed, Try Again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    });

    private void signUpUsingEmailPass(String email, String pass) {
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                    intent.putExtra("email", Objects.requireNonNull(auth.getCurrentUser()).getEmail());
                    startActivity(intent);
                } else {
                    Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    changeBackDefaultRegBtn();
                }
            }
        });
    }

}
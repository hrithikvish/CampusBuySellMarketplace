package com.hrithikvish.cbsm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithikvish.cbsm.databinding.ActivityLoginBinding;
import com.hrithikvish.cbsm.utils.Constants;
import com.hrithikvish.cbsm.utils.SharedPrefManager;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    SharedPrefManager sharedPrefManager;
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    MaterialButton googleSignInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        sharedPrefManager = new SharedPrefManager(LoginActivity.this);

        googleSignInBtn = (MaterialButton) binding.googleSignIn;

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(Constants.CLIENT_ID).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, options);

        binding.loginBtn.setOnClickListener(view-> {
            changeLoginBtnToProgBar(true);
            String email = binding.emailET.getText().toString();
            String pass = binding.passET.getText().toString();
            if(!email.isEmpty() && !pass.isEmpty()) {
                signInUsingEmailPass(email, pass);
            } else {
                if (email.isEmpty()) {
                    binding.emailET.setError("Enter Email");
                }
                if (pass.isEmpty()) {
                    binding.passET.setError("Enter Password");
                }
                changeLoginBtnToProgBar(false);
            }
        });

        binding.googleSignIn.setOnClickListener(view -> {
            changeGoogleRegBtnToLoading(true);
            Intent signInIntent = googleSignInClient.getSignInIntent();
            activityResultLauncher.launch(signInIntent);
        });

        binding.goToSignUpPageText.setOnClickListener(view-> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        });

        binding.forgotPassText.setOnClickListener(view-> {
            startActivity(new Intent(LoginActivity.this, ForgotPassActivity.class));
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        changeLoginBtnToProgBar(false);
        changeGoogleRegBtnToLoading(false);
    }

    private void signInUsingEmailPass(String email, String pass) {
        auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    sharedPrefManager.putBoolean(Constants.LOGIN_SESSION_SHARED_PREF_KEY, true);
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    changeLoginBtnToProgBar(false);
                }
            }
        });
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
                                String uid = auth.getUid();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(!snapshot.child("Users").child(uid).child("clg").exists()) {
                                            Toast.makeText(LoginActivity.this, "User Not Found, Sign Up", Toast.LENGTH_LONG).show();
                                            changeGoogleRegBtnToLoading(false);
                                        } else {
                                            sharedPrefManager.putBoolean(Constants.LOGIN_SESSION_SHARED_PREF_KEY, true);
                                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } else {
                                Toast.makeText(LoginActivity.this, "Login Failed, Try Again!", Toast.LENGTH_SHORT).show();
                                changeGoogleRegBtnToLoading(false);
                            }
                        }
                    });
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    });

    private void changeLoginBtnToProgBar(Boolean isLoading) {
        if(isLoading) {
            binding.loginBtn.setText("");
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.loginBtn.setText("Login");
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void changeGoogleRegBtnToLoading(Boolean isLoading) {
        if(isLoading) {
            binding.googleSignIn.setText("");
            googleSignInBtn.setIcon(null);
            binding.googleBar.setVisibility(View.VISIBLE);
        } else {
            binding.googleBar.setVisibility(View.GONE);
            googleSignInBtn.setIconResource(R.drawable.icon_google);
            binding.googleSignIn.setText("Continue to Google");
        }
    }
}
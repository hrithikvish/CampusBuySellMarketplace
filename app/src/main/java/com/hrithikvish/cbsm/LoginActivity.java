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
import com.hrithikvish.cbsm.databinding.ActivityLoginBinding;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    MaterialButton googleSignInBtn;
    String clientId = "751206525517-rbhuic3hspp5k5hpohgd9ghp324mbj05.apps.googleusercontent.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        googleSignInBtn = (MaterialButton) binding.googleSignIn;
        FirebaseApp.initializeApp(this);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(clientId).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, options);

        binding.loginBtn.setOnClickListener(view-> {
            changeLoginBtnToProgBar();
            String email = binding.emailET.getText().toString();
            String pass = binding.passET.getText().toString();
            if(!email.isEmpty() && !pass.isEmpty()) {
                signInUsingEmailPass(email, pass);
            } else {
                displayEmptyErrorMsg();
                changeBackDefaultLoginBtn();
            }
        });

        binding.googleSignIn.setOnClickListener(view -> {
            changeGoogleBtnToProgBar();
            Intent signInIntent = googleSignInClient.getSignInIntent();
            activityResultLauncher.launch(signInIntent);
        });

        binding.goToSignUpPageText.setOnClickListener(view-> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
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
                                auth = FirebaseAuth.getInstance();
                                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.putExtra("Login name", Objects.requireNonNull(auth.getCurrentUser()).getDisplayName());
                                intent.putExtra("Login profileUrl", Objects.requireNonNull(auth.getCurrentUser().getPhotoUrl()).toString());
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, "Login Failed, Try Again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    });

    private void displayEmptyErrorMsg() {
        binding.emailET.setError("Enter Email");
        binding.passET.setError("Enter Password");
    }

    private void changeLoginBtnToProgBar() {
        binding.loginBtn.setText("");
        binding.progressBar.setVisibility(View.VISIBLE);
    }
    private void changeGoogleBtnToProgBar() {
        binding.googleSignIn.setText("");
        binding.googleBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        changeBackDefaultLoginBtn();
        changeBackDefaultGoogleBtn();
    }

    private void changeBackDefaultLoginBtn() {
        binding.loginBtn.setText("Login");
        binding.progressBar.setVisibility(View.GONE);
    }

    private void changeBackDefaultGoogleBtn() {
        binding.googleBar.setVisibility(View.GONE);
        binding.googleSignIn.setText("Continue with Google");
        googleSignInBtn.setIconResource(R.drawable.icon_google);
    }

    private void signInUsingEmailPass(String email, String pass) {
        auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("Login Email", auth.getCurrentUser().getEmail());
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
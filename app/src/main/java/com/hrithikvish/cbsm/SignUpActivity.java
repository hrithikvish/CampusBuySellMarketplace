package com.hrithikvish.cbsm;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hrithikvish.cbsm.databinding.ActivitySignUpBinding;
import com.hrithikvish.cbsm.utils.FirebaseDatabaseHelper;

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
            String email = binding.emailET.getText().toString().trim();
            String pass = binding.passET.getText().toString().trim();
            String conPass = binding.conPassET.getText().toString().trim();
            if(!email.isEmpty() && !pass.isEmpty() && !conPass.isEmpty()) {
                changeRegBtnToProgBar();
                signUpUsingEmailPass(email, pass, conPass);
            } else {
                if(email.isEmpty()) {
                    binding.emailET.setError("Enter Email");
                }
                if(pass.isEmpty()) {
                    binding.passET.setError("Enter Password");
                }
                if(conPass.isEmpty()) {
                    binding.conPassET.setError("Re Enter Password");
                }
            }
        });

        binding.googleSignUp.setOnClickListener(view -> {
            changeGoogleBtnToProgBar();

            Intent signInIntent = googleSignInClient.getSignInIntent();
            activityResultLauncher.launch(signInIntent);
        });

        binding.phoneSignUp.setOnClickListener(view-> {
            Toast.makeText(SignUpActivity.this, "Not Implemented Yet", Toast.LENGTH_SHORT).show();
        });

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


    private void signUpUsingEmailPass(String email, String pass, String conPass) {
        if(validateData(email, pass, conPass)) {
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        setSharedPref(true);
                        //addUserIntoFbDb(email, pass);
                        addUserDataInFirebaseDatabase(email, pass);

                        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                        startActivity(intent.putExtra("user", auth.getCurrentUser()));
                    } else {
                        Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        changeBackDefaultRegBtn();
                    }
                }
            });
        } else {
            changeBackDefaultRegBtn();
        }
    }

    private void addUserDataInFirebaseDatabase(String email, String pass) {
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseDatabaseHelper firebaseDatabaseHelper = new FirebaseDatabaseHelper(SignUpActivity.this, auth, databaseReference);
        firebaseDatabaseHelper.addUserIntoFbDb(email, pass);
    }

    /*private void addUserIntoFbDb(String email, String pass) {
        final DatabaseReference databaseReference;
        String uid = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.child("Users").child(uid).exists()) {
                    HashMap<String, Object> userData = new HashMap<>();
                    userData.put("email", email);
                    userData.put("password", pass);

                    databaseReference.child("Users").child(uid).updateChildren(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "User added in DB", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Failed to add in DB", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    private boolean validateData(String email, String pass, String conPass) {
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailET.setError("Invalid Email");
            return false;
        }
        if(pass.length() < 8) {
            binding.passET.setError("Password Length must be 8 or more");
            return false;
        }
        if(!conPass.equals(pass)) {
            binding.conPassET.setError("Password doesn't match");
            return false;
        }
        return true;
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
                                setSharedPref(true);
                                auth = FirebaseAuth.getInstance();
                                FirebaseUser user = auth.getCurrentUser();
                                String email = user.getEmail();
                                addUserDataInFirebaseDatabase(email, "***No Password***");

                                Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                intent.putExtra("user", auth.getCurrentUser());
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

    private void setSharedPref(boolean isLoggedIn) {
        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }

}
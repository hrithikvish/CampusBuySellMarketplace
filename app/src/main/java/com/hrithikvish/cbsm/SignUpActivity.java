package com.hrithikvish.cbsm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithikvish.cbsm.databinding.ActivitySignUpBinding;
import com.hrithikvish.cbsm.utils.ActivityFinisher;
import com.hrithikvish.cbsm.utils.Constants;
import com.hrithikvish.cbsm.utils.FirebaseDatabaseHelper;
import com.hrithikvish.cbsm.utils.SharedPrefManager;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class SignUpActivity extends AppCompatActivity implements ActivityFinisher {

    ActivitySignUpBinding binding;
    SharedPrefManager sharedPrefManager;
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    MaterialButton googleSignUnBtn;
    FirebaseDatabaseHelper firebaseDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(SignUpActivity.this, auth, this::finishActivity);
        sharedPrefManager = new SharedPrefManager(SignUpActivity.this);
        googleSignUnBtn = (MaterialButton) binding.googleSignUp;
        FirebaseApp.initializeApp(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(Constants.CLIENT_ID).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.goToLoginPageText.setOnClickListener(view -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });

        binding.signUpBtn.setOnClickListener(view -> {
            String email = binding.emailET.getText().toString().trim();
            String pass = binding.passET.getText().toString().trim();
            String conPass = binding.conPassET.getText().toString().trim();
            if (!email.isEmpty() && !pass.isEmpty() && !conPass.isEmpty()) {
                changeRegBtnToLoading(true);
                signUpUsingEmailPass(email, pass, conPass);
            } else {
                if (email.isEmpty()) {
                    binding.emailET.setError("Enter Email");
                }
                if (pass.isEmpty()) {
                    binding.passET.setError("Enter Password");
                }
                if (conPass.isEmpty()) {
                    binding.conPassET.setError("Re Enter Password");
                }
            }
        });

        binding.googleSignUp.setOnClickListener(view -> {
            changeGoogleRegBtnToLoading(true);
            Intent signInIntent = googleSignInClient.getSignInIntent();
            activityResultLauncher.launch(signInIntent);
        });

        setClgAdapter();
    }

    private void setClgAdapter() {
        //decompressing Colleges_lIST GZip
        List<String> colleges = new ArrayList<>();
        try {
            GZIPInputStream gzipInputStream = decompressCollegeGz();

            byte[] data = IOUtils.toByteArray(gzipInputStream);

            String allClgesString = new String(data, StandardCharsets.UTF_8);
            String[] individualColleges = allClgesString.split("\n");
            colleges.addAll(Arrays.asList(individualColleges));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //College
        ArrayAdapter<String> adapter = null;
        try {
            adapter = new ArrayAdapter<>(this, R.layout.college_dropdown_layout, colleges);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        binding.clgET.setAdapter(adapter);
    }

    private GZIPInputStream decompressCollegeGz() throws Exception{
        InputStream inputStream = getResources().openRawResource(R.raw.colleges_list_txt);
        return new GZIPInputStream(inputStream);
    }

    @Override
    protected void onStop() {
        super.onStop();
        changeRegBtnToLoading(false);
        changeGoogleRegBtnToLoading(false);
    }

    private void signUpUsingEmailPass(String email, String pass, String conPass) {
        if (validateData(email, pass, conPass)) {
            String clg = binding.clgET.getText().toString().trim();
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        sharedPrefManager.putBoolean(Constants.LOGIN_SESSION_SHARED_PREF_KEY, true);
                        firebaseDatabaseHelper.addUserIntoFbDb(email, clg);
                    } else {
                        Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        changeRegBtnToLoading(false);
                    }
                }
            });
        } else {
            changeRegBtnToLoading(false);
        }
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == RESULT_OK) {
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(o.getData());
                try {
                    GoogleSignInAccount googleSignInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                    auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //sharedPrefManager.putBoolean(Constants.LOGIN_SESSION_SHARED_PREF_KEY, true);
                                auth = FirebaseAuth.getInstance();
                                FirebaseUser user = auth.getCurrentUser();
                                String email = user.getEmail();

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.child("Users").child(auth.getUid()).exists()) {
                                            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                                        } else {
                                            Intent intent = new Intent(SignUpActivity.this, SelectCollegeActivity.class);
                                            intent.putExtra("googleEmail", email);
                                            startActivity(intent);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {}
                                });
                            } else {
                                Toast.makeText(SignUpActivity.this, "Something went wrong, try again.", Toast.LENGTH_SHORT).show();
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

    private boolean validateData(String email, String pass, String conPass) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailET.setError("Invalid Email");
            return false;
        }
        if (pass.length() < 6) {
            binding.passET.setError("Password Length must be 6 or more");
            return false;
        }
        if (!conPass.equals(pass)) {
            binding.conPassET.setError("Password doesn't match");
            return false;
        }
        if(binding.clgET.getText().toString().isEmpty()) {
            binding.clgET.setError("Select Your College");
            return false;
        }
        return true;
    }

    private void changeRegBtnToLoading(Boolean isLoading) {
        if(isLoading) {
            binding.signUpBar.setVisibility(View.VISIBLE);
            binding.signUpBtn.setText("");
        } else {
            binding.signUpBar.setVisibility(View.GONE);
            binding.signUpBtn.setText("Register");
        }
    }

    private void changeGoogleRegBtnToLoading(Boolean isLoading) {
        if(isLoading) {
            binding.googleSignUp.setText("");
            googleSignUnBtn.setIcon(null);
            binding.googleBar.setVisibility(View.VISIBLE);
        } else {
            binding.googleBar.setVisibility(View.GONE);
            googleSignUnBtn.setIconResource(R.drawable.icon_google);
            binding.googleSignUp.setText("Continue to Google");
        }
    }

    @Override
    public void finishActivity() {
        finish();
    }
}
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.hrithikvish.cbsm.utils.ActivityFinisher;
import com.hrithikvish.cbsm.utils.FirebaseDatabaseHelper;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

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

        binding.goToLoginPageText.setOnClickListener(view -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(clientId).requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(this, options);

        binding.signUpBtn.setOnClickListener(view -> {
            String email = binding.emailET.getText().toString().trim();
            String pass = binding.passET.getText().toString().trim();
            String conPass = binding.conPassET.getText().toString().trim();
            if (!email.isEmpty() && !pass.isEmpty() && !conPass.isEmpty()) {
                changeRegBtnToProgBar();
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
            Intent signInIntent = googleSignInClient.getSignInIntent();
            activityResultLauncher.launch(signInIntent);
        });

        //decompressing Colleges_lIST GZip
        List<String> colleges = new ArrayList<>();
        try {
            GZIPInputStream gzipInputStream = decompressCollegeGz();
            // Read decompressed data into a byte array
            byte[] data = IOUtils.toByteArray(gzipInputStream);
            // Convert byte array to string
            String allClgesString = new String(data, StandardCharsets.UTF_8);
            String[] individualColleges = allClgesString.split("\n");
            colleges.addAll(Arrays.asList(individualColleges));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //College
        ArrayAdapter<String> adapter = null;
        try {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, colleges);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        binding.clgET.setAdapter(adapter);
    }

    private GZIPInputStream decompressCollegeGz() throws Exception{
        InputStream inputStream = getResources().openRawResource(R.raw.colleges_list_txt);
        return new GZIPInputStream(inputStream);
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
        if (validateData(email, pass, conPass)) {
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        setSharedPref(true);
                        addUserDataInFirebaseDatabase(email);
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

    private void addUserDataInFirebaseDatabase(String email) {
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseDatabaseHelper firebaseDatabaseHelper = new FirebaseDatabaseHelper(SignUpActivity.this, auth, databaseReference);
        firebaseDatabaseHelper.addUserIntoFbDb(email);
    }

    private boolean validateData(String email, String pass, String conPass) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailET.setError("Invalid Email");
            return false;
        }
        if (pass.length() < 8) {
            binding.passET.setError("Password Length must be 8 or more");
            return false;
        }
        if (!conPass.equals(pass)) {
            binding.conPassET.setError("Password doesn't match");
            return false;
        }
        return true;
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
                                setSharedPref(true);
                                auth = FirebaseAuth.getInstance();
                                FirebaseUser user = auth.getCurrentUser();
                                String email = user.getEmail();
                                addUserDataInFirebaseDatabase(email);
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
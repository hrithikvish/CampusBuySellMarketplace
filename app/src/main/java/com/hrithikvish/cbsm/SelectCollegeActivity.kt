package com.hrithikvish.cbsm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.util.IOUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.hrithikvish.cbsm.databinding.ActivitySelectCollegeBinding;
import com.hrithikvish.cbsm.utils.ActivityFinisher;
import com.hrithikvish.cbsm.utils.FirebaseDatabaseHelper;
import com.hrithikvish.cbsm.utils.SharedPrefManager;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class SelectCollegeActivity extends AppCompatActivity implements ActivityFinisher {
    ActivitySelectCollegeBinding binding;
    FirebaseAuth auth;
    FirebaseDatabaseHelper firebaseDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectCollegeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(SelectCollegeActivity.this, auth, this::finishActivity);

        setClgAdapter();

        binding.continueBtn.setOnClickListener(view -> {
            changeRegBtnToLoading(true);
            firebaseDatabaseHelper.addUserIntoFbDb(auth.getCurrentUser().getEmail(), binding.clgET.getText().toString().trim());
        });

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

    @Override
    protected void onStop() {
        super.onStop();
        changeRegBtnToLoading(false);
    }

    private GZIPInputStream decompressCollegeGz() throws Exception{
        InputStream inputStream = getResources().openRawResource(R.raw.colleges_list_txt);
        return new GZIPInputStream(inputStream);
    }

    private void changeRegBtnToLoading(Boolean isLoading) {
        if(isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.continueBtn.setText("");
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.continueBtn.setText("Register");
        }
    }

    @Override
    public void finishActivity() {
        finish();
    }
}
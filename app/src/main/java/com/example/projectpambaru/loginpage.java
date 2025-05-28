package com.example.projectpambaru;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginpage extends AppCompatActivity implements View.OnClickListener{

    EditText username, password;
    private FirebaseAuth mAuth;
    private static final String TAG = "loginpage";
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.loginpage_layout);

        mAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        Button login = findViewById(R.id.login_button);
        Button daftar = findViewById(R.id.daftar);

        login.setOnClickListener(this);
        daftar.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.login_button) {
            login(username.getText().toString(), password.getText().toString());
        } else if (id == R.id.daftar) {
            signUp(username.getText().toString(), password.getText().toString());
        }
    }

    public void signUp(String email, String password) {
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Toast.makeText(loginpage.this, user.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(loginpage.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    public void login(String email, String password) {
            if (!validateForm()) return;
        FirebaseApp app = FirebaseApp.getInstance();
        Log.d("FirebaseInit", "Firebase initialized with name: " + app.getName());
        Log.d("FirebaseInit", "Firebase options: " + app.getOptions().getProjectId());

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(loginpage.this, "Login sebagai: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            Toast.makeText(loginpage.this, "Login gagal.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean result = true;

        if (TextUtils.isEmpty(username.getText().toString())) {
            username.setError("Required");
            result = false;
        } else {
            username.setError(null);
        }

        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Required");
            result = false;
        } else {
            password.setError(null);
        }

        return result;
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(loginpage.this, dashboard.class);
            startActivity(intent);
        } else {
            Toast.makeText(loginpage.this, "Log In First", Toast.LENGTH_SHORT).show();
        }
    }
}
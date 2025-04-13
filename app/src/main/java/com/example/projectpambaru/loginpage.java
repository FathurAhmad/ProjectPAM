package com.example.projectpambaru;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class loginpage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.loginpage_layout);

        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);

        Button login = findViewById(R.id.login);
        login.setOnClickListener(v -> {
            String user = username.getText().toString();
            String pass = password.getText().toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(loginpage.this, "Username & Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else {
                if (user.equals("admin") && pass.equals("admin")) {
                    Intent intent = new Intent(this, dashboard.class);
                    intent.putExtra("username", user);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Username & Password tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
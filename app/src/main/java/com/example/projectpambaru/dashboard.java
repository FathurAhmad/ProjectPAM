package com.example.projectpambaru;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.dashboard_layout);

        Button home = findViewById(R.id.home_button);
        Button income = findViewById(R.id.uang_masuk_button);
        Button outcome = findViewById(R.id.uang_keluar_button);
        Button target = findViewById(R.id.target_button);

        home.setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, dashboard.class);
            startActivity(intent);
            finish();
        });

        income.setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, income.class);
            startActivity(intent);
            finish();
        });

        outcome.setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, outcome.class);
            startActivity(intent);
            finish();
        });

        target.setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, target.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
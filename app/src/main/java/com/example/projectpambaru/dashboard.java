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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class dashboard extends AppCompatActivity {

    RecyclerView recyclerView;
    TransaksiAdapter adapter;
    List<TransaksiItem> transaksiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.dashboard_layout);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        transaksiList = new ArrayList<>();
        transaksiList.add(new TransaksiItem("Pengeluaran", "Transaksi ke-1", "Membeli kopi", "Rp 20.000"));
        transaksiList.add(new TransaksiItem("Pengeluaran", "Transaksi ke-2", "Bayar parkir", "Rp 5.000"));
        transaksiList.add(new TransaksiItem("Pengeluaran", "Transaksi ke-3", "Makan siang", "Rp 30.000"));

        adapter = new TransaksiAdapter(transaksiList);
        recyclerView.setAdapter(adapter);

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
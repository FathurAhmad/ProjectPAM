package com.example.projectpambaru;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class income extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    AdapterTransaksi adapter; // Ubah tipe variabel adapter
    List<Transaksi> listTransaksi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.income_layout);

        recyclerView = findViewById(R.id.income_list);
        listTransaksi = new ArrayList<>();

        // Menambahkan objek Transaksi ke dalam listTransaksi

        listTransaksi.add(new Transaksi("Pemasukan", "Transaksi ke-1", "Beli sembako" ,10000));
        listTransaksi.add(new Transaksi("Pengeluaran", "Transaksi ke-2", "Beli bensin",10000));

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Membuat instance AdapterTransaksi
        adapter = new AdapterTransaksi(this, listTransaksi);
        recyclerView.setAdapter(adapter);

        Button home = findViewById(R.id.home_button);
        Button income = findViewById(R.id.uang_masuk_button);
        Button outcome = findViewById(R.id.uang_keluar_button);
        Button target = findViewById(R.id.target_button);

        home.setOnClickListener(v -> {
            Intent intent = new Intent(income.this, dashboard.class);
            startActivity(intent);
            finish();
        });

        income.setOnClickListener(v -> {
            Intent intent = new Intent(income.this, income.class);
            startActivity(intent);
            finish();
        });

        outcome.setOnClickListener(v -> {
            Intent intent = new Intent(income.this, outcome.class);
            startActivity(intent);
            finish();
        });

        target.setOnClickListener(v -> {
            Intent intent = new Intent(income.this, target.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

}
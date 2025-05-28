package com.example.projectpambaru;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class outcome extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    AdapterTransaksi adapter; // Ubah tipe variabel adapter
    List<Transaksi> listTransaksi;

    Button btnTambah;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.outcome_layout);

        recyclerView = findViewById(R.id.outcome_list);
        listTransaksi = new ArrayList<>();

        // Menambahkan objek Transaksi ke dalam listTransaksi

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Membuat instance AdapterTransaksi
        adapter = new AdapterTransaksi(this, DatabaseTransaksi.getTransaksiPengeluaran());
        recyclerView.setAdapter(adapter);

        btnTambah = findViewById(R.id.tambah_button);

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });

        Button home = findViewById(R.id.home_button);
        Button income = findViewById(R.id.uang_masuk_button);
        Button outcome = findViewById(R.id.uang_keluar_button);
        Button target = findViewById(R.id.target_button);

        home.setOnClickListener(v -> {
            Intent intent = new Intent(outcome.this, dashboard.class);
            startActivity(intent);
            finish();
        });

        income.setOnClickListener(v -> {
            Intent intent = new Intent(outcome.this, income.class);
            startActivity(intent);
            finish();
        });

        outcome.setOnClickListener(v -> {
            Intent intent = new Intent(outcome.this, outcome.class);
            startActivity(intent);
            finish();
        });

        target.setOnClickListener(v -> {
            Intent intent = new Intent(outcome.this, TargetActivity.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showPopup() {
        // Inflate layout popup
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.tambah_transaksi, null);

        // Buat AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(outcome.this);
        builder.setView(popupView);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Tombol close di dalam popup
        Button btnClose = popupView.findViewById(R.id.tambah_button);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView namaTransaksi = popupView.findViewById(R.id.nama_transaksi);
                TextView deskripsiTransaksi = popupView.findViewById(R.id.deskripsi_transaksi);
                TextView nominalTransaksi = popupView.findViewById(R.id.nominal_transaksi);

                String nama = namaTransaksi.getText().toString();
                String deskripsi = deskripsiTransaksi.getText().toString();
                double nominal = Double.parseDouble(nominalTransaksi.getText().toString());
                DatabaseTransaksi.tambahTransaksi(new Transaksi("Pengeluaran", nama, deskripsi, nominal));
                dialog.dismiss();
            }
        });
    }
}
package com.example.projectpambaru;

import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.icu.text.ListFormatter;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    TextView nominal;
    RecyclerView recyclerView;
    TransaksiAdapter adapter;
    List<Transaksi> transaksiList;
    DatabaseReference databaseReference;
    TextView tvUsername;
    Button btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        transaksiList = new ArrayList<>();
        for (Transaksi item : DatabaseTransaksi.getTransaksiList()) {
            transaksiList.add(new Transaksi(item.getJenisTransaksi(), item.getNama(), item.getDeskripsi(), item.getNominal()));
        }

        nominal = findViewById(R.id.balance);
        int total = DatabaseTransaksi.getNominal();
        String format = formatAngka(total);
        nominal.setText(String.valueOf("Rp" + format));

        adapter = new TransaksiAdapter(this, transaksiList);
        recyclerView.setAdapter(adapter);

        Button home = findViewById(R.id.home_button);
        Button income = findViewById(R.id.uang_masuk_button);
        Button outcome = findViewById(R.id.uang_keluar_button);
        Button target = findViewById(R.id.target_button);
        ImageView iconProfile = findViewById(R.id.profile_image);

        iconProfile.setOnClickListener(v -> {
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_profile, null);
            btn_logout = dialogView.findViewById(R.id.button_logout);
            tvUsername = dialogView.findViewById(R.id.tvUsername);

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            String username = firebaseAuth.getCurrentUser().getEmail();
            tvUsername.setText(username);

            ImageView imageViewProfile = dialogView.findViewById(R.id.imageViewProfile);
            TextView textViewUbahProfile = dialogView.findViewById(R.id.textViewUbahFoto);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            dialog.show();



            textViewUbahProfile.setOnClickListener(v2 -> {
                Intent profileIntent = new Intent(Intent.ACTION_PICK);
                profileIntent.setType("image/*");

                startActivityForResult(profileIntent, 100);
                dialog.dismiss();
            });

            btn_logout.setOnClickListener(v3 -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            });
        });

        home.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        income.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, IncomeActivity.class);
            startActivity(intent);
            finish();
        });

        outcome.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, OutcomeActivity.class);
            startActivity(intent);
            finish();
        });

        target.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, TargetActivity.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();

            // Ganti imageView dengan ID dari profil utama kalau kamu punya
            ImageView imageViewUtama = findViewById(R.id.profile_image);
            imageViewUtama.setImageURI(selectedImage);
        }
    }

    public static String formatAngka (int angka) {
        DecimalFormat format = new DecimalFormat("#,###");
        return format.format(angka).replace(",", ".");
    }
}
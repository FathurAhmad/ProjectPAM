package com.example.projectpambaru;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TargetActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TargetAdapter adapter;
    private List<Target> targetList;
    private DatabaseReference databaseReference;
    private Button btn_tambah_target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_target);

        recyclerView = findViewById(R.id.target_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        targetList = new ArrayList<>();
        adapter = new TargetAdapter(targetList, id -> {databaseReference.child(id).removeValue();});
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("targets");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                targetList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Target target = data.getValue(Target.class);
                    if (target != null) {
                        targetList.add(target);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TargetActivity.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });

        btn_tambah_target = findViewById(R.id.btn_tambah_target);
        btn_tambah_target.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(TargetActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.tambah_target, null);
            builder.setView(dialogView);

            AlertDialog dialog = builder.create(); // supaya bisa dismiss nanti
            dialog.show();

            EditText editNama = dialogView.findViewById(R.id.etTarget);
            EditText editNominal = dialogView.findViewById(R.id.etNominal);
            Button btnSubmit = dialogView.findViewById(R.id.btn_submit);

            btnSubmit.setOnClickListener(v -> {
                String nama = editNama.getText().toString().trim();
                String nominalStr = editNominal.getText().toString().trim();

                if (nama.isEmpty() || nominalStr.isEmpty()) {
                    Toast.makeText(TargetActivity.this, "Isi semua field!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int nominal = Integer.parseInt(nominalStr);
                String id = databaseReference.push().getKey();
                Target target = new Target(id, nama, nominal);

                databaseReference.child(id).setValue(target)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(TargetActivity.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                            dialog.dismiss(); // Tutup popup
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(TargetActivity.this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            });
        });

        Button home = findViewById(R.id.home_button);
        Button income = findViewById(R.id.uang_masuk_button);
        Button outcome = findViewById(R.id.uang_keluar_button);
        Button target = findViewById(R.id.target_button);

        home.setOnClickListener(v -> {
            Intent intent = new Intent(TargetActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        income.setOnClickListener(v -> {
            Intent intent = new Intent(TargetActivity.this, IncomeActivity.class);
            startActivity(intent);
            finish();
        });

        outcome.setOnClickListener(v -> {
            Intent intent = new Intent(TargetActivity.this, OutcomeActivity.class);
            startActivity(intent);
            finish();
        });

        target.setOnClickListener(v -> {
            Intent intent = new Intent(TargetActivity.this, TargetActivity.class);
            startActivity(intent);
            finish();
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void targetDelete(String id) {
        databaseReference.child(id).removeValue()
                .addOnSuccessListener(unused -> Toast.makeText(TargetActivity.this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(TargetActivity.this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
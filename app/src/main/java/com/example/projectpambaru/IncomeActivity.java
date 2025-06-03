package com.example.projectpambaru;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IncomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TransaksiAdapter adapter;
    List<Transaksi> transaksiList;
    DatabaseReference databaseReference;
    private Uri selectedFileUri;
    private TextView tvFileNameSelected;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 101;
    private static final int PICK_FILE_REQUEST_CODE = 202;

    private static final String SUPABASE_URL = "https://bisvlneeendtwzxtygpj.supabase.co";
    private static final String SUPABASE_BUCKET = "income";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJpc3ZsbmVlZW5kdHd6eHR5Z3BqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg3OTM5NTksImV4cCI6MjA2NDM2OTk1OX0.CvM3dQKKrdkpB6Sh3346QgtzJq3hSCOjxjdiS3KQmlM"; // ganti dengan API key asli kamu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_income);

        transaksiList = new ArrayList<>();
        recyclerView = findViewById(R.id.income_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransaksiAdapter(this, transaksiList);
        recyclerView.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference(userId).child("transaksi");
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transaksiList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Transaksi transaksi = dataSnapshot.getValue(Transaksi.class);
                    if (transaksi != null && "Pemasukan".equals(transaksi.getJenisTransaksi())) {
                        transaksiList.add(transaksi);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Gagal ambil data", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.tambah_button).setOnClickListener(v -> showPopup());

        Button home = findViewById(R.id.home_button);
        Button income = findViewById(R.id.uang_masuk_button);
        Button outcome = findViewById(R.id.uang_keluar_button);
        Button target = findViewById(R.id.target_button);

        home.setOnClickListener(v -> {
            Intent intent = new Intent(IncomeActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        income.setOnClickListener(v -> {
            Intent intent = new Intent(IncomeActivity.this, IncomeActivity.class);
            startActivity(intent);
            finish();
        });

        outcome.setOnClickListener(v -> {
            Intent intent = new Intent(IncomeActivity.this, OutcomeActivity.class);
            startActivity(intent);
            finish();
        });

        target.setOnClickListener(v -> {
            Intent intent = new Intent(IncomeActivity.this, TargetActivity.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showPopup() {
        selectedFileUri = null;

        View popupView = LayoutInflater.from(this).inflate(R.layout.transaksi_tambah, null);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(popupView).create();
        dialog.show();

        TextView namaTransaksi = popupView.findViewById(R.id.nama_transaksi);
        TextView deskripsiTransaksi = popupView.findViewById(R.id.deskripsi_transaksi);
        TextView nominalTransaksi = popupView.findViewById(R.id.nominal_transaksi);
//        tvFileNameSelected = popupView.findViewById(R.id.file_name_textview); // Pastikan ID ini ada di XML

        Button btnPilihFile = popupView.findViewById(R.id.upload_file_button);
        Button btnSimpan = popupView.findViewById(R.id.tambah_button);

        btnPilihFile.setOnClickListener(v -> checkAndRequestStoragePermission());

        btnSimpan.setOnClickListener(v -> {
            String nama = namaTransaksi.getText().toString().trim();
            String deskripsi = deskripsiTransaksi.getText().toString().trim();
            String nominalStr = nominalTransaksi.getText().toString().trim();

            if (nama.isEmpty()) {
                namaTransaksi.setError("Nama transaksi tidak boleh kosong");
                return;
            }

            if (nominalStr.isEmpty()) {
                nominalTransaksi.setError("Nominal tidak boleh kosong");
                return;
            }

            double nominal;
            try {
                nominal = Double.parseDouble(nominalStr);
                if (nominal <= 0) {
                    nominalTransaksi.setError("Nominal harus lebih dari 0");
                    return;
                }
            } catch (NumberFormatException e) {
                nominalTransaksi.setError("Nominal tidak valid");
                return;
            }

            String transaksiId = databaseReference.push().getKey();
            Transaksi transaksi = new Transaksi(transaksiId, "Pemasukan", nama, deskripsi, nominal, null);

            if (transaksiId != null) {
                databaseReference.child(transaksiId).setValue(transaksi).addOnSuccessListener(aVoid -> {
                    if (selectedFileUri != null) {
                        uploadFileToSupabaseStorage(selectedFileUri, transaksiId);
                    } else {
                        Toast.makeText(this, "Transaksi berhasil disimpan", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(this, "Gagal menyimpan transaksi", Toast.LENGTH_SHORT).show());
            }

            dialog.dismiss();
        });
    }

    private void checkAndRequestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                openFilePicker();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                openFilePicker();
            }
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Pilih File"), PICK_FILE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) openFilePicker();
            else Toast.makeText(this, "Izin ditolak. Tidak bisa pilih file.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            String fileName = getFileName(selectedFileUri);
            if (tvFileNameSelected != null) {
                tvFileNameSelected.setText("File terpilih: " + fileName);
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;

        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }

        // Fallback jika result masih null
        if (result == null) {
            result = uri.getLastPathSegment();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1 && cut < result.length() - 1) {
                    result = result.substring(cut + 1);
                }
            }
        }

        return result;
    }


    private void uploadFileToSupabaseStorage(Uri fileUri, String transaksiId) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            byte[] fileBytes = new byte[inputStream.available()];
            inputStream.read(fileBytes);
            inputStream.close();

            String fileName = UUID.randomUUID().toString() + "_" + getFileName(fileUri);
            String uploadUrl = SUPABASE_URL + "/storage/v1/object/" + SUPABASE_BUCKET + "/" + fileName;

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(fileBytes);
            Request request = new Request.Builder()
                    .url(uploadUrl)
                    .header("apikey", SUPABASE_API_KEY)
                    .header("Authorization", "Bearer " + SUPABASE_API_KEY)
                    .put(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(IncomeActivity.this, "Upload gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String fileUrl = SUPABASE_URL + "/storage/v1/object/public/" + SUPABASE_BUCKET + "/" + fileName;
                        databaseReference.child(transaksiId).child("gambarUrl").setValue(fileUrl);
                        runOnUiThread(() -> Toast.makeText(IncomeActivity.this, "File berhasil diupload", Toast.LENGTH_SHORT).show());
                    } else {
                        runOnUiThread(() -> Toast.makeText(IncomeActivity.this, "Upload gagal: " + response.message(), Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Gagal membaca file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

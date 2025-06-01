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

import com.google.firebase.database.DatabaseReference;

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

public class OutcomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    TransaksiAdapter adapter; // Ubah tipe variabel adapter
    List<Transaksi> listTransaksi;
    Button btnTambah;
    private Uri selectedFileUri;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 101;
    private static final int PICK_FILE_REQUEST_CODE = 202;
    private TextView tvFileNameSelected;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_outcome);

        recyclerView = findViewById(R.id.outcome_list);

        // Menambahkan objek Transaksi ke dalam listTransaksi

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Membuat instance AdapterTransaksi
        adapter = new TransaksiAdapter(this, DatabaseTransaksi.getTransaksiPengeluaran());
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
            Intent intent = new Intent(OutcomeActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        income.setOnClickListener(v -> {
            Intent intent = new Intent(OutcomeActivity.this, IncomeActivity.class);
            startActivity(intent);
            finish();
        });

        outcome.setOnClickListener(v -> {
            Intent intent = new Intent(OutcomeActivity.this, OutcomeActivity.class);
            startActivity(intent);
            finish();
        });

        target.setOnClickListener(v -> {
            Intent intent = new Intent(OutcomeActivity.this, TargetActivity.class);
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
        selectedFileUri = null; // Reset selectedFileUri setiap kali popup dibuka
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.tambah_transaksi, null);

        // Buat AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(OutcomeActivity.this);
        builder.setView(popupView);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Inisialisasi komponen UI dari popup
        TextView namaTransaksi = popupView.findViewById(R.id.nama_transaksi);
        TextView deskripsiTransaksi = popupView.findViewById(R.id.deskripsi_transaksi);
        TextView nominalTransaksi = popupView.findViewById(R.id.nominal_transaksi);
        Button btnPilihFile = popupView.findViewById(R.id.upload_file_button); // Inisialisasi tombol pilih file
        Button btnSimpan = popupView.findViewById(R.id.tambah_button); // Inisialisasi tombol simpan transaksi

        // Listener untuk tombol "Pilih Bukti File" di dalam popup
        btnPilihFile.setOnClickListener(v -> {
            checkAndRequestStoragePermission(); // Meminta izin dan membuka picker
        });

        // Listener untuk tombol "Simpan Transaksi" di dalam popup
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = namaTransaksi.getText().toString().trim();
                String deskripsi = deskripsiTransaksi.getText().toString().trim();
                String nominalStr = nominalTransaksi.getText().toString().trim();

                // --- Validasi Input ---
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
                // --- Akhir Validasi Input ---

                // Tambahkan transaksi ke DatabaseTransaksi (lokal)
                DatabaseTransaksi.tambahTransaksi(new Transaksi("Pengeluaran", nama, deskripsi, nominal));

                // Perbarui RecyclerView
                adapter.notifyDataSetChanged(); // Memberi tahu adapter bahwa data telah berubah

                // --- Logika Upload File ke Firebase Storage ---
                if (selectedFileUri != null) {
                    uploadFileToSupabaseStorage(selectedFileUri);
                } else {
                    Toast.makeText(OutcomeActivity.this, "Transaksi Pengeluaran berhasil ditambahkan (tanpa bukti file).", Toast.LENGTH_SHORT).show();
                }
                // --- Akhir Logika Upload File ---

                dialog.dismiss(); // Tutup pop-up
            }
        });
    }
    // --- Metode untuk Izin dan Pemilihan File ---
    private void checkAndRequestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 (API 33) ke atas
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                openFilePicker();
            }
        } else { // Android 12 (API 32) ke bawah
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                openFilePicker();
            }
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Untuk memilih semua jenis file
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Pilih Bukti Transaksi"), PICK_FILE_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Tidak ada aplikasi file manager terinstal.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                openFilePicker();
            } else {
                Toast.makeText(this, "Izin penyimpanan ditolak. Tidak dapat memilih file.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedFileUri = data.getData();
            String fileName = getFileName(selectedFileUri);
            if (tvFileNameSelected != null) {
                tvFileNameSelected.setText("File terpilih: " + fileName);
            }
            Toast.makeText(this, "File terpilih: " + fileName, Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method untuk mendapatkan nama file dari URI
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
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void uploadFileToSupabaseStorage(Uri fileUri) {
        if (fileUri == null) return;

        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            byte[] fileBytes = new byte[inputStream.available()];
            inputStream.read(fileBytes);
            inputStream.close();

            String fileName = UUID.randomUUID().toString() + "_" + getFileName(fileUri);
            String supabaseUrl = "https://bisvlneeendtwzxtygpj.supabase.co";
            String bucketName = "outcome";
            String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fileName;

            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = RequestBody.create(fileBytes);

            Request request = new Request.Builder()
                    .url(uploadUrl)
                    .header("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJpc3ZsbmVlZW5kdHd6eHR5Z3BqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg3OTM5NTksImV4cCI6MjA2NDM2OTk1OX0.CvM3dQKKrdkpB6Sh3346QgtzJq3hSCOjxjdiS3KQmlM")
                    .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJpc3ZsbmVlZW5kdHd6eHR5Z3BqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg3OTM5NTksImV4cCI6MjA2NDM2OTk1OX0.CvM3dQKKrdkpB6Sh3346QgtzJq3hSCOjxjdiS3KQmlM")
                    .put(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(OutcomeActivity.this, "Upload gagal: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            String fileUrl = supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + fileName;
                            Toast.makeText(OutcomeActivity.this, "Upload ke Supabase berhasil!\n" + fileUrl, Toast.LENGTH_LONG).show();
                            // Simpan URL ke database lokal atau remote
                        } else {
                            Toast.makeText(OutcomeActivity.this, "Gagal upload Supabase: " + response.message(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error saat membaca file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
package com.example.projectpambaru;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.text.DecimalFormat;
import android.icu.text.ListFormatter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DashboardActivity extends AppCompatActivity {

    TextView nominal;
    RecyclerView recyclerView;
    TransaksiAdapter adapter;
    List<Transaksi> transaksiList;
    DatabaseReference databaseReference;
    int totalPemasukan;
    int totalPengeluaran;
    ShapeableImageView iconProfile;
    ImageView imageViewProfile;
    TextView tvUsername;
    Button btn_logout;
    private Uri selectedFileUri;
    private TextView tvFileNameSelected;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 101;
    private static final int PICK_FILE_REQUEST_CODE = 202;

    private static final String SUPABASE_URL = "https://bisvlneeendtwzxtygpj.supabase.co";
    private static final String SUPABASE_BUCKET = "profile-pic";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJpc3ZsbmVlZW5kdHd6eHR5Z3BqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg3OTM5NTksImV4cCI6MjA2NDM2OTk1OX0.CvM3dQKKrdkpB6Sh3346QgtzJq3hSCOjxjdiS3KQmlM"; // ganti dengan API key asli kamu


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        recyclerView = findViewById(R.id.recyclerView);
        transaksiList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        iconProfile = findViewById(R.id.profile_image);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference(userId).child("transaksi");
            ambilSemuaTransaksi();
            databaseReference.child("profileImageUrl").get().addOnSuccessListener(dataSnapshot -> {
                String profileUrl = dataSnapshot.getValue(String.class);
                if (profileUrl != null && !profileUrl.isEmpty()){
                    Glide.with(this).load(profileUrl).circleCrop().into(iconProfile);
                }else{
                    iconProfile.setImageResource(R.drawable.profile_placeholder);
                }
            });
        }

        nominal = findViewById(R.id.balance);

        adapter = new TransaksiAdapter(this, transaksiList);
        recyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transaksiList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Transaksi transaksi = data.getValue(Transaksi.class);
                    if (transaksi != null) {
                        transaksiList.add(transaksi);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });

        Button home = findViewById(R.id.home_button);
        Button income = findViewById(R.id.uang_masuk_button);
        Button outcome = findViewById(R.id.uang_keluar_button);
        Button target = findViewById(R.id.target_button);

        iconProfile.setOnClickListener(v -> {
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_profile, null);
            btn_logout = dialogView.findViewById(R.id.button_logout);
            tvUsername = dialogView.findViewById(R.id.tvUsername);

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            String username = firebaseAuth.getCurrentUser().getEmail();
            tvUsername.setText(username);

            imageViewProfile = dialogView.findViewById(R.id.imageViewProfile);
            TextView textViewUbahProfile = dialogView.findViewById(R.id.textViewUbahFoto);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            dialog.show();

            textViewUbahProfile.setOnClickListener(v1 -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");

                startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
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

    public static String formatAngka (int angka) {
        DecimalFormat format = new DecimalFormat("#,###");
        return format.format(angka).replace(",", ".");
    }

    private void ambilSemuaTransaksi() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalPemasukan = 0;
                totalPengeluaran = 0;

                for (DataSnapshot data : snapshot.getChildren()) {
                    Transaksi transaksi = data.getValue(Transaksi.class);
                    if (transaksi != null) {
                        if ("pemasukan".equalsIgnoreCase(transaksi.getJenisTransaksi())) {
                            totalPemasukan += transaksi.getNominal();
                        } else if ("pengeluaran".equalsIgnoreCase(transaksi.getJenisTransaksi())) {
                            totalPengeluaran += transaksi.getNominal();
                        }
                    }
                }

                int saldoAkhir = totalPemasukan - totalPengeluaran;

                nominal.setText("Rp" + formatAngka(saldoAkhir));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Gagal mengambil data transaksi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickProfileImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Pilih Foto Profil"), PICK_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                uploadProfileImageToSupabase(selectedFileUri);
            }
        }
    }


    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private void uploadProfileImageToSupabase(Uri fileUri) {
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
                    runOnUiThread(() -> Toast.makeText(DashboardActivity.this, "Upload gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String fileUrl = SUPABASE_URL + "/storage/v1/object/public/" + SUPABASE_BUCKET + "/" + fileName;

                        // Simpan ke Firebase Database user saat ini
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            FirebaseDatabase.getInstance().getReference(userId).child("profileImageUrl").setValue(fileUrl);
                        }

                        // Update imageViewProfile
                        runOnUiThread(() -> {
                            Toast.makeText(DashboardActivity.this, "Foto profil berhasil diupload", Toast.LENGTH_SHORT).show();
                            Glide.with(DashboardActivity.this).load(fileUrl).into(imageViewProfile);
                            Glide.with(DashboardActivity.this).load(fileUrl).into(iconProfile);
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(DashboardActivity.this, "Upload gagal: " + response.message(), Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Gagal membaca file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



}
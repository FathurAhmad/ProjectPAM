package com.example.projectpambaru;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class TargetActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TargetAdapter adapter;
    private List<Target> targetList;
    private DatabaseReference databaseReference;
    private Button btn_tambah_target;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 101;
    private static final int PICK_FILE_REQUEST_CODE = 202;
    private Uri selectedFileUri;
    private TextView tvFileNameSelected;
    private static final String SUPABASE_URL = "https://bisvlneeendtwzxtygpj.supabase.co";
    private static final String SUPABASE_BUCKET = "target";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJpc3ZsbmVlZW5kdHd6eHR5Z3BqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDg3OTM5NTksImV4cCI6MjA2NDM2OTk1OX0.CvM3dQKKrdkpB6Sh3346QgtzJq3hSCOjxjdiS3KQmlM"; // ganti dengan API key asli kamu


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_target);

        recyclerView = findViewById(R.id.target_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        targetList = new ArrayList<>();
        adapter = new TargetAdapter(targetList, this);
        recyclerView.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            String userId = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference(userId).child("target");
        }

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
            View dialogView = getLayoutInflater().inflate(R.layout.target_tambah, null);
            builder.setView(dialogView);

            AlertDialog dialog = builder.create(); // supaya bisa dismiss nanti
            dialog.show();

            EditText editNama = dialogView.findViewById(R.id.etTarget);
            EditText editNominal = dialogView.findViewById(R.id.etNominal);
            Button btnSubmit = dialogView.findViewById(R.id.btn_submit);
            Button btnAddImage = dialogView.findViewById(R.id.btn_addGambar);

            btnAddImage.setOnClickListener(v -> checkAndRequestStoragePermission());

            btnSubmit.setOnClickListener(v -> {
                String nama = editNama.getText().toString().trim();
                String nominalStr = editNominal.getText().toString().trim();

                if (nama.isEmpty() || nominalStr.isEmpty()) {
                    Toast.makeText(TargetActivity.this, "Isi semua field!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int nominal = Integer.parseInt(nominalStr);
                String id = databaseReference.push().getKey();
                Target target = new Target(id, nama, nominal, null);

                if (id != null ){
                    databaseReference.child(id).setValue(target).addOnSuccessListener(aVoid -> {
                        if (selectedFileUri != null){
                            uploadToSupabaseStorage(selectedFileUri, id);
                        }else{
                            Toast.makeText(this, "Target berhasil disimpan", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Gagal menyimpan transaksi", Toast.LENGTH_SHORT).show();
                    });
                }
                dialog.dismiss();
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

    private void checkAndRequestStoragePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_STORAGE_PERMISSION);
            }else {
                openFilePicker();
            }
        }else{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                openFilePicker();
            }
        }
    }

    private void openFilePicker(){
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

    private String getFileName(Uri uri){
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

    private void uploadToSupabaseStorage(Uri fileUri, String targetId){
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
                    runOnUiThread(() -> Toast.makeText(TargetActivity.this, "Upload gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String fileUrl = SUPABASE_URL + "/storage/v1/object/public/" + SUPABASE_BUCKET + "/" + fileName;
                        databaseReference.child(targetId).child("gambarUrl").setValue(fileUrl);
                        runOnUiThread(() -> Toast.makeText(TargetActivity.this, "File berhasil diupload", Toast.LENGTH_SHORT).show());
                    } else {
                        runOnUiThread(() -> Toast.makeText(TargetActivity.this, "Upload gagal: " + response.message(), Toast.LENGTH_SHORT).show());
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(this, "Gagal membaca file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void targetDelete(String id) {
        databaseReference.child(id).removeValue()
                .addOnSuccessListener(unused -> Toast.makeText(TargetActivity.this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(TargetActivity.this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
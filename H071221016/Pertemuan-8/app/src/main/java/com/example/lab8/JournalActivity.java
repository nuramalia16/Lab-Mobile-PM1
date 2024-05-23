package com.example.lab8;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

public class JournalActivity extends AppCompatActivity {
    private EditText etTitle;
    private EditText etDescription;
    private Button btnSubmit;
    private ImageButton btnDelete;
    private DBConfig dbConfig;
    private int recordId;
    private boolean isUpdateMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_journal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbConfig = new DBConfig(this);

        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_desc);
        btnSubmit = findViewById(R.id.btn_submit);
        btnDelete = findViewById(R.id.btn_delete);

        TextView tvTitle = findViewById(R.id.tv_title);
        ImageButton btnBack = findViewById(R.id.btn_back);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("record_id")) {
            isUpdateMode = true;
            recordId = intent.getIntExtra("record_id", -1);
            loadRecordData(recordId);
            tvTitle.setText("Ubah");
            btnSubmit.setText("UPDATE");
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setText("Tambah");
            btnSubmit.setText("SUBMIT");
            btnDelete.setVisibility(View.GONE);
        }

        btnBack.setOnClickListener(v -> showCancelConfirmationDialog());

        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());

        btnSubmit.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (!title.isEmpty() && !description.isEmpty()) {
                if (isUpdateMode) {
                    dbConfig.updateRecord(recordId, title, description);
                } else {
                    dbConfig.insertData(title, description);
                }
                finish();
            } else {
                if (title.isEmpty()) {
                    etTitle.setError("Field tidak boleh kosong");
                }
                if (description.isEmpty()) {
                    etDescription.setError("Silakan masukkan deskripsi");
                }
            }
        });
    }

    private void loadRecordData(int id) {
        Cursor cursor = dbConfig.getReadableDatabase().rawQuery("SELECT * FROM " + dbConfig.getTableName() + " WHERE " + dbConfig.getColumnId() + " = ?", new String[]{String.valueOf(id)});
        if (cursor != null && cursor.moveToFirst()) {
            etTitle.setText(cursor.getString(cursor.getColumnIndexOrThrow(dbConfig.getColumnJudul())));
            etDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(dbConfig.getColumnDeskripsi())));
            cursor.close();
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Note")
                .setMessage("Apakah anda yakin ingin menghapus item ini?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    dbConfig.deleteRecords(recordId);
                    finish();
                })
                .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Batal")
                .setMessage("Apakah anda ingin membatlkan perubahan pada form ini?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    super.onBackPressed();
                    finish();
                })
                .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    public void onBackPressed() {
        showCancelConfirmationDialog();
    }
}

package com.example.cinemas;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cinemas.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTen, editEmail, editPass, editNhapPass, editSinh;
    private RadioGroup radioGroup;
    private CheckBox checkBox;
    private Button btDangKy;
    private Calendar calendar;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_register);

        editTen = findViewById(R.id.edit_ten);
        editEmail = findViewById(R.id.edit_email);
        editPass = findViewById(R.id.edit_pass);
        editNhapPass = findViewById(R.id.edit_nhap_pass);
        editSinh = findViewById(R.id.edit_sinh);
        radioGroup = findViewById(R.id.radioGroup);
        checkBox = findViewById(R.id.checkbox);
        btDangKy = findViewById(R.id.bt_dang_ky);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        calendar = Calendar.getInstance();
        editSinh.setOnClickListener(v -> chonNgaySinh());

        btDangKy.setOnClickListener(v -> {
            if (kiemTraDuLieu()) {
                dangKyVoiFirebaseAuth();
            }
        });
    }

    private void chonNgaySinh() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> editSinh.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                year, month, day
        );
        datePickerDialog.show();
    }

    private boolean kiemTraDuLieu() {
        String ten = editTen.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String pass = editPass.getText().toString().trim();
        String nhapPass = editNhapPass.getText().toString().trim();
        String sinh = editSinh.getText().toString().trim();

        if (ten.isEmpty() || email.isEmpty() || pass.isEmpty() || nhapPass.isEmpty() || sinh.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!pass.equals(nhapPass)) {
            Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (pass.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Kiểm tra ngày sinh hợp lệ
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setLenient(false);
        try {
            Date ngaySinh = sdf.parse(sinh);
            if (ngaySinh.after(new Date())) {
                Toast.makeText(this, "Ngày sinh không thể lớn hơn ngày hiện tại!", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Ngày sinh không hợp lệ!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!checkBox.isChecked()) {
            Toast.makeText(this, "Vui lòng đồng ý với điều khoản sử dụng!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void dangKyVoiFirebaseAuth() {
        String email = editEmail.getText().toString().trim();
        String pass = editPass.getText().toString().trim();

        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    luuDuLieu(user.getUid());
                }
            } else {
                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Đăng ký thất bại!";
                Toast.makeText(RegisterActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void luuDuLieu(String userId) {
        String ten = editTen.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String sinh = editSinh.getText().toString().trim();
        String gioiTinh = radioGroup.getCheckedRadioButtonId() == R.id.radio_nam ? "Nam" : "Nữ";

        // Tạo đối tượng dữ liệu
        Map<String, Object> userData = new HashMap<>();
        userData.put("hoTen", ten);
        userData.put("email", email);
        userData.put("ngaySinh", sinh);
        userData.put("gioiTinh", gioiTinh);

        // Lưu dữ liệu lên Firebase
        databaseReference.child(userId).setValue(userData).addOnSuccessListener(aVoid -> {
            Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
        });
    }
}

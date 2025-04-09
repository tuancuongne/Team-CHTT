package com.example.cinemas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;

import com.example.cinemas.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private EditText editEmail, editPass;
    private Button btnLogin;
    private TextView tvRegister, txt_quen_mk;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.edit_email);
        editPass = findViewById(R.id.edit_pass);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        txt_quen_mk = findViewById(R.id.txt_quen_mk);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        btnLogin.setOnClickListener(v -> {
            if(kiemTraDuLieu()){
                dangNhap();
            }
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        //xử lý quên mật khẩu
        txt_quen_mk.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            if(email.isEmpty()){
                Toast.makeText(LoginActivity.this, "Vui lòng nhập email để đặt lại mật khẩu!", Toast.LENGTH_SHORT).show();
            }else if(!email.contains("@") || !email.contains(".")){
                Toast.makeText(LoginActivity.this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
            }else{
                auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Email đặt lại mật khẩu đã được gửi!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(LoginActivity.this, "Gửi email thất bại, kiểm tra lại email!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean kiemTraDuLieu(){
        String email = editEmail.getText().toString().trim();
        String pass = editPass.getText().toString().trim();

        if(email.isEmpty() || pass.isEmpty()){
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!email.contains("@") || !email.contains(".")){
            Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void dangNhap(){
        String email = editEmail.getText().toString().trim();
        String pass = editPass.getText().toString().trim();

        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
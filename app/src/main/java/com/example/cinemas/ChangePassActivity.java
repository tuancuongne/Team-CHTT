package com.example.cinemas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassActivity extends AppCompatActivity {
    private ImageView backArrow;
    private EditText editPassword, editNewPass, editComfirmPass;
    private Button btChangePass;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_change_pass);

        backArrow = findViewById(R.id.back_arrow);
        editPassword = findViewById(R.id.edit_password);
        editNewPass = findViewById(R.id.edit_new_pass);
        editComfirmPass = findViewById(R.id.edit_confirm_pass);
        btChangePass = findViewById(R.id.bt_change_pass);

        auth = FirebaseAuth.getInstance();

        backArrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // xử lí nút xác nhận
        btChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Password = editPassword.getText().toString().trim();
                String newPass = editNewPass.getText().toString().trim();
                String confirmPass = editComfirmPass.getText().toString().trim();

                //kiểm tra dữ liệu nhập vào
                if(Password.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()){
                    Toast.makeText(ChangePassActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!newPass.equals(confirmPass)){
                    Toast.makeText(ChangePassActivity.this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(newPass.length() < 6){
                    Toast.makeText(ChangePassActivity.this, "Mật khẩu phải có ít nhất 6 kí tự!", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser user = auth.getCurrentUser();
                if(user != null && user.getEmail() != null){
                    //xác thực lại người dùng với mk hiện tại
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), Password);
                    user.reauthenticate(credential).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            //xác thực thành công, tiến hành đổi mk
                            user.updatePassword(newPass).addOnCompleteListener(updateTask -> {
                                if(updateTask.isSuccessful()){
                                    Toast.makeText(ChangePassActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    Toast.makeText(ChangePassActivity.this, "Đổi mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            Toast.makeText(ChangePassActivity.this, "Mật khẩu hiện tại không đúng!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(ChangePassActivity.this, "Người dùng không tồn tại, vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChangePassActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}

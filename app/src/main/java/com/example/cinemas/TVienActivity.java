package com.example.cinemas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TVienActivity extends AppCompatActivity {
    private ImageView backArrow, arrowAccountInfo, arrowTransaction, arrowChangePass;
    private TextView userName;
    private LinearLayout menuAccountInfo, menuTransaction, menuLogout, menuChangePass;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_thanhvien);

        backArrow = findViewById(R.id.back_arrow);
        userName = findViewById(R.id.user_name_tv);
        menuAccountInfo = findViewById(R.id.menu_account_info);
        menuTransaction = findViewById(R.id.menu_transaction_history);
        menuLogout = findViewById(R.id.menu_logout);
        menuChangePass = findViewById(R.id.menu_change_password);
        arrowAccountInfo = findViewById(R.id.arrow_account_info);
        arrowTransaction = findViewById(R.id.arrow_transaction_history);
        arrowChangePass = findViewById(R.id.arrow_change_password);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        //Lấy và hiển thị tên người dùng từ Firebase
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            databaseReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String hoTen = snapshot.child("hoTen").getValue(String.class);
                        if(hoTen != null){
                            userName.setText(hoTen);
                        }else{
                            userName.setText("Người dùng");
                        }
                    }else{
                        userName.setText("Người dùng");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    userName.setText("Người dùng");
                }
            });
        }else{
            userName.setText("Người dùng");
        }

        // Xử lý sự kiện nhấn nút quay lại
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Xử lý nhấn Thông tin tài khoản
        menuAccountInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TVienActivity.this, AccountInfoActivity.class);
                startActivity(intent);
            }
        });

        // xử lý nhấn lịch sử giao dịch
        // Trong TVienActivity
        menuTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TVienActivity.this, TransactionHistoryActivity.class);
                startActivity(intent); // Chuyển sang TransactionHistoryActivity
            }
        });

        //xử lý nhấn đổi mật khẩu
        menuChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TVienActivity.this, ChangePassActivity.class);
                startActivity(intent);
            }
        });

        // xử lý nhấn đăng xuất
        menuLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(TVienActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //xóa stack activity
                startActivity(intent);
                finish();
            }
        });
    }
}

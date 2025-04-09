package com.example.cinemas;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountInfoActivity extends AppCompatActivity {
    private EditText editName, editEmail, editGT, editDate;
    private ImageView backArrow;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_account_info);

        editName = findViewById(R.id.editTextName);
        editEmail = findViewById(R.id.editTextEmail);
        editGT = findViewById(R.id.editTextGT);
        editDate = findViewById(R.id.editTextDate);
        backArrow = findViewById(R.id.back_arrow);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        //Lấy và hiển thị thông tin người dùng từ Firebase
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            databaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        //lấy dữ liệu từ Firebase
                        String hoTen = snapshot.child("hoTen").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String gioiTinh = snapshot.child("gioiTinh").getValue(String.class);
                        String ngaySinh = snapshot.child("ngaySinh").getValue(String.class);

                        //hiển thị dữ liệu lên giao diện
                        editName.setText(hoTen != null ? hoTen : "Không có dữ liệu");
                        editEmail.setText(email != null ? email : "Không có dữ liệu");
                        editGT.setText(gioiTinh != null ? gioiTinh : "Không có dữ liệu");
                        editDate.setText(ngaySinh != null ? ngaySinh : "Không có dữ liệu");
                    }else{
                        Toast.makeText(AccountInfoActivity.this, "Không tìm thấy dữ liệu!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(AccountInfoActivity.this, "Lỗi!", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "Người dùng chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
        }

        //xử lý nút quay lại
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
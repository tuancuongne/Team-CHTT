package com.example.cinemas;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView sapChieu, dangChieu, chieuSom, userName;
    private LinearLayout layoutLoggedIn;
    private ViewPager2 viewPager;
    private MoviePagerAdapter moviePagerAdapter;

    // Firebase Authentication và Database
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các thành phần giao diện
        initViews();

        // Khởi tạo Firebase Auth và Database
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Thiết lập ViewPager2
        setupViewPager();

        // Xử lý sự kiện click tab
        sapChieu.setOnClickListener(v -> updateTabSelection(0));
        dangChieu.setOnClickListener(v -> updateTabSelection(1));
        chieuSom.setOnClickListener(v -> updateTabSelection(2));

        // Mặc định chọn "Đang chiếu"
        updateTabSelection(1);

        // Xử lý sự kiện click vào "Đăng nhập" hoặc thông tin người dùng
        userName.setOnClickListener(v -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                // Người dùng đã đăng nhập, chuyển sang TVienActivity
                Intent intent = new Intent(MainActivity.this, TVienActivity.class);
                startActivity(intent);
            } else {
                // Người dùng chưa đăng nhập, chuyển sang LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Kiểm tra trạng thái đăng nhập
        kiemTraTrangThaiDangNhap();
    }

    // Khởi tạo các view
    private void initViews() {
        sapChieu = findViewById(R.id.sap_chieu);
        dangChieu = findViewById(R.id.dang_chieu);
        chieuSom = findViewById(R.id.chieu_som);
        userName = findViewById(R.id.user_name);
        layoutLoggedIn = findViewById(R.id.layout_logged_in);
        viewPager = findViewById(R.id.viewPager);

        // Kiểm tra null để tránh crash
        if (viewPager == null) {
            Log.e("MainActivity", "ViewPager not found in layout");
            Toast.makeText(this, "Lỗi giao diện", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // Kiểm tra trạng thái đăng nhập
    private void kiemTraTrangThaiDangNhap() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Người dùng đã đăng nhập
            layoutLoggedIn.setVisibility(View.VISIBLE);

            // Lấy thông tin người dùng từ Realtime Database
            databaseReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String hoTen = dataSnapshot.child("hoTen").getValue(String.class);
                        userName.setText(hoTen != null ? hoTen : "Người dùng");
                    } else {
                        userName.setText("Người dùng");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("MainActivity", "Database error: " + databaseError.getMessage());
                    userName.setText("Người dùng");
                }
            });
        } else {
            // Người dùng chưa đăng nhập
            layoutLoggedIn.setVisibility(View.GONE);
            userName.setText("Đăng nhập");
        }
    }

    // Thiết lập ViewPager2
    private void setupViewPager() {
        List<String> categories = new ArrayList<>();
        categories.add("Sapchieu");
        categories.add("Dangchieu");
        categories.add("Chieusom");

        moviePagerAdapter = new MoviePagerAdapter(this, categories);
        viewPager.setAdapter(moviePagerAdapter);

        // Đồng bộ tab với ViewPager
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateTabSelection(position);
            }
        });
    }

    // Cập nhật trạng thái tab
    private void updateTabSelection(int position) {
        viewPager.setCurrentItem(position, true); // true để có hiệu ứng cuộn mượt
        int selectedColor = Color.parseColor("#0000FF"); // Màu xanh dương
        int defaultColor = Color.BLACK;

        sapChieu.setTextColor(position == 0 ? selectedColor : defaultColor);
        dangChieu.setTextColor(position == 1 ? selectedColor : defaultColor);
        chieuSom.setTextColor(position == 2 ? selectedColor : defaultColor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        kiemTraTrangThaiDangNhap(); // Cập nhật trạng thái khi quay lại activity
    }
}
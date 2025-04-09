package com.example.cinemas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView imagePoster, imageMuiTen;
    private TextView txtTen, txtDaoDien, txtDienVien, txtTheLoai, txtNgonNgu, txtNgayChieu, txtThoiLuong, txtMoTa, txtNoiDung;
    private Button btDatVe;
    private DatabaseReference movieRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        initViews();
        Intent intent = getIntent();
        String movieId = intent.getStringExtra("Id");
        String category = intent.getStringExtra("Category");
        Log.d("MovieDetailActivity", "Received Id: " + movieId + ", Category: " + category);
        if (movieId == null || movieId.isEmpty() || category == null || category.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin phim", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        movieRef = FirebaseDatabase.getInstance().getReference("movies").child(category).child(movieId);
        Log.d("MovieDetailActivity", "Querying path: movies/" + category + "/" + movieId);
        loadMovieDetails();
        imageMuiTen.setOnClickListener(v -> finish());
        btDatVe.setOnClickListener(v -> {
            Intent bookTicketIntent = new Intent(MovieDetailActivity.this, BookTicketActivity.class);
            bookTicketIntent.putExtra("movie_id", movieId); // Sửa key từ "movieId" thành "movie_id"
            bookTicketIntent.putExtra("movie_title", txtTen.getText().toString()); // Truyền tiêu đề phim
            Log.d("MovieDetailActivity", "Navigating to BookTicketActivity with movie_id: " + movieId);
            startActivity(bookTicketIntent);
        });
    }
    private void initViews() {
        imagePoster = findViewById(R.id.image_poster);
        imageMuiTen = findViewById(R.id.image_muiten);
        txtTen = findViewById(R.id.txt_ten);
        txtDaoDien = findViewById(R.id.txt_dao_dien);
        txtDienVien = findViewById(R.id.txt_dien_vien);
        txtTheLoai = findViewById(R.id.txt_the_loai);
        txtNgonNgu = findViewById(R.id.txt_ngon_ngu);
        txtNgayChieu = findViewById(R.id.txt_ngay_chieu);
        txtThoiLuong = findViewById(R.id.txt_thoi_luong);
        txtNoiDung = findViewById(R.id.txt_noi_dung);
        txtMoTa = findViewById(R.id.txt_mo_ta);
        btDatVe = findViewById(R.id.bt_dat_ve);
    }
    private void loadMovieDetails() {
        movieRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("MovieDetailActivity", "Snapshot exists: " + snapshot.exists());
                if (snapshot.exists()) {
                    Log.d("MovieDetailActivity", "Raw snapshot data: " + snapshot.getValue().toString());
                    Movie movie = snapshot.getValue(Movie.class);
                    if (movie != null) {
                        Log.d("MovieDetailActivity", "Movie loaded - Title: " + movie.getTen() +
                                ", Director: " + movie.getDdien() +
                                ", Actors: " + movie.getDvien() +
                                ", Genre: " + movie.getTheloai() +
                                ", Language: " + movie.getNgonngu() +
                                ", Release Date: " + movie.getNgay() +
                                ", Duration: " + movie.getThoiluong() +
                                ", Description: " + movie.getNdung() +
                                ", Poster: " + movie.getAnh());
                        displayMovieDetails(movie);
                    } else {
                        Log.e("MovieDetailActivity", "Movie object is null - Failed to deserialize");
                        Toast.makeText(MovieDetailActivity.this, "Dữ liệu phim không hợp lệ", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Log.e("MovieDetailActivity", "Movie not found in database at path: movies/" + snapshot.getRef().toString());
                    Toast.makeText(MovieDetailActivity.this, "Không tìm thấy phim", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MovieDetailActivity", "Database error: " + error.getMessage());
                Toast.makeText(MovieDetailActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    private void displayMovieDetails(Movie movie) {
        Glide.with(this)
                .load(movie.getAnh())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_close_clear_cancel)
                .into(imagePoster);
        txtTen.setText(movie.getTen() != null ? movie.getTen() : "Không có tiêu đề");
        txtDaoDien.setText("Đạo diễn: " + (movie.getDdien() != null ? movie.getDdien() : "Không rõ"));
        txtDienVien.setText("Diễn viên: " + (movie.getDvien() != null ? movie.getDvien() : "Không rõ"));
        txtTheLoai.setText("Thể loại: " + (movie.getTheloai() != null ? movie.getTheloai() : "Không rõ"));
        txtNgonNgu.setText("Ngôn ngữ: " + (movie.getNgonngu() != null ? movie.getNgonngu() : "Không rõ"));
        txtNgayChieu.setText("Ngày khởi chiếu: " + (movie.getNgay() != null ? movie.getNgay() : "Không rõ"));
        txtThoiLuong.setText("Thời lượng: " + (movie.getThoiluong() != null ? movie.getThoiluong() : "Không rõ"));
        txtMoTa.setText(movie.getNdung() != null ? movie.getNdung() : "Không có mô tả");
    }
}
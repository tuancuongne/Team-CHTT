package com.example.cinemas;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class MoviePagerAdapter extends RecyclerView.Adapter<MoviePagerAdapter.ViewHolder> {
    private Context context;
    private List<String> categories;
    private FirebaseDatabase database;
    public MoviePagerAdapter(Context context, List<String> categories) {
        this.context = context;
        this.categories = categories;
        this.database = FirebaseDatabase.getInstance();// Khởi tạo kết nối đến Firebase
    }
    // Lớp ViewHolder để lưu trữ RecyclerView con (danh sách phim trong mỗi danh mục)
    public static class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            if (recyclerView == null) {
                Log.e("ViewHolder", "recyclerView không tìm thấy trong movie_page.xml");
            }
        }
    }
    @NonNull
    @Override
    // Tạo ViewHolder mới khi cần hiển thị một danh mục
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout movie_page.xml để tạo giao diện cho mỗi danh mục
        View view = LayoutInflater.from(context).inflate(R.layout.movie_page, parent, false);
        return new ViewHolder(view);
    }
    // Gắn dữ liệu và cấu hình RecyclerView con cho từng danh mục
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String category = categories.get(position);// Lấy danh mục tại vị trí position
        MovieListAdapter movieListAdapter = new MovieListAdapter(context, category);// Tạo Adapter cho danh sách phim trong danh mục
        if (holder.recyclerView != null) {
            holder.recyclerView.setLayoutManager(new GridLayoutManager(context, 3)); // Thiết lập GridLayoutManager với 3 cột để hiển thị phim dạng lưới
            holder.recyclerView.setAdapter(movieListAdapter);// Gắn MovieListAdapter vào RecyclerView con
            loadMoviesFromRealtimeDatabase(category, movieListAdapter);// Tải danh sách phim từ Firebase
        } else {
            Log.e("MoviePagerAdapter", "recyclerView is null");
        }
    }
    // Hàm tải danh sách phim từ Firebase Realtime Database
    private void loadMoviesFromRealtimeDatabase(String category, MovieListAdapter movieListAdapter) {
        // Tham chiếu đến node "movies/category" trong Firebase
        DatabaseReference movieRef = database.getReference("movies").child(category);
        movieRef.addValueEventListener(new ValueEventListener() { //Lắng nghe sự thay đổi dữ liệu từ Firebase
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Movie> movieList = new ArrayList<>();// Danh sách phim để lưu trữ dữ liệu từ Firebase
                // Duyệt qua từng phim trong snapshot
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Lấy các trường dữ liệu từ Firebase
                    String Anh = dataSnapshot.child("Anh").getValue(String.class);
                    String Ngay = dataSnapshot.child("Ngay").getValue(String.class);
                    String Ten = dataSnapshot.child("Ten").getValue(String.class);
                    String Ddien = dataSnapshot.child("Ddien").getValue(String.class);
                    String Dvien = dataSnapshot.child("Dvien").getValue(String.class);
                    String Theloai = dataSnapshot.child("Theloai").getValue(String.class);
                    String Ngonngu = dataSnapshot.child("Ngonngu").getValue(String.class);
                    String Thoiluong = dataSnapshot.child("Thoiluong").getValue(String.class);
                    String Ndung = dataSnapshot.child("Ndung").getValue(String.class);
                    String Id = dataSnapshot.getKey();
                    // Kiểm tra các trường bắt buộc có tồn tại không
                    if (Anh != null && Ngay != null && Ten != null && Id != null) {
                        // Tạo đối tượng Movie từ dữ liệu
                        Movie movie = new Movie(Anh, Ngay, Ten, Ddien, Dvien, Theloai, Ngonngu, Thoiluong, Ndung, Id);
                        movieList.add(movie);
                        Log.d("MoviePagerAdapter", "Loaded movie: " + Ten + ", Id: " + Id);
                    } else {
                        Log.e("MoviePagerAdapter", "Missing required fields for movie: " + Id);
                    }
                }
                movieListAdapter.setMovies(movieList);// Cập nhật danh sách phim vào MovieListAdapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MoviePagerAdapter", "Database error: " + error.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
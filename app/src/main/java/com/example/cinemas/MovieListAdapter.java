package com.example.cinemas;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {
    private Context context;
    private List<Movie> movies;
    private String category;
    public MovieListAdapter(Context context, String category) {
        this.context = context;
        this.movies = new ArrayList<>();
        this.category = category;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePoster;
        TextView txtNgay, txtTen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePoster = itemView.findViewById(R.id.image_poster);
            txtNgay = itemView.findViewById(R.id.txt_ngay);
            txtTen = itemView.findViewById(R.id.txt_ten);
        }
    }
    // Hàm cập nhật danh sách phim và làm mới RecyclerView
    public void setMovies(List<Movie> movies) {
        this.movies = movies;// Gán danh sách phim mới
        notifyDataSetChanged(); // Thông báo RecyclerView cập nhật giao diện
    }
    // Tạo ViewHolder mới khi cần hiển thị một item
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_movie.xml để tạo giao diện cho mỗi item
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }
    // Gắn dữ liệu vào ViewHolder tại vị trí position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        Glide.with(context).load(movie.getAnh()).into(holder.imagePoster);
        holder.txtTen.setText(movie.getTen());
        if (category.equals("Sapchieu")) {
            holder.txtNgay.setText(movie.getNgay());
        } else {
            holder.txtNgay.setVisibility(View.GONE); // ẩn ngày
        }
        // Xử lý sự kiện khi người dùng nhấn vào item
        holder.itemView.setOnClickListener(v -> {
            Log.d("MovieListAdapter", "Clicked on movie: " + movie.getTen() + ", Id: " + movie.getId() + ", Category: " + category);
            // Tạo Intent để chuyển sang MovieDetailActivity
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("Id", movie.getId());
            intent.putExtra("Category", category);
            context.startActivity(intent);//Khởi động Activity chi tiết phim
        });
    }
    // Trả về số lượng item trong danh sách
    @Override
    public int getItemCount() {
        return movies.size();
    }
}
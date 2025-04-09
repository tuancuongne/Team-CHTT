package com.example.cinemas;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CinemaAdapter extends RecyclerView.Adapter<CinemaAdapter.CinemaViewHolder> {

    private List<Cinema> cinemaList;
    private Context context;
    private String selectedDate;
    private String movieTitle;

    public CinemaAdapter(Context context, List<Cinema> cinemaList) {
        this.context = context;
        this.cinemaList = cinemaList;

        // Debug: In danh sách để kiểm tra thứ tự sau khi khởi tạo
        System.out.println("Danh sách rạp sau khi khởi tạo:");
        for (int i = 0; i < cinemaList.size(); i++) {
            Cinema cinema = cinemaList.get(i);
            System.out.println("Vị trí " + i + ": Area = " + cinema.getArea() + ", Name = " + cinema.getName());
        }
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    @NonNull
    @Override
    public CinemaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cinemas, parent, false);
        return new CinemaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CinemaViewHolder holder, int position) {
        Cinema cinema = cinemaList.get(position);

        // Kiểm tra xem đây có phải là lần đầu tiên khu vực xuất hiện trong danh sách không
        boolean isFirstOccurrence = position == 0 || !cinema.getArea().equals(cinemaList.get(position - 1).getArea());

        // Hiển thị tiêu đề khu vực nếu đây là lần đầu tiên khu vực xuất hiện
        if (isFirstOccurrence) {
            holder.textViewArea.setText(cinema.getArea());
            holder.textViewArea.setVisibility(View.VISIBLE);
            System.out.println("Hiển thị khu vực tại vị trí " + position + ": " + cinema.getArea() + " (phim: " + movieTitle + ", ngày: " + selectedDate + ")");
        } else {
            holder.textViewArea.setVisibility(View.GONE);
            System.out.println("Ẩn khu vực tại vị trí " + position + ": " + cinema.getArea() + " (phim: " + movieTitle + ", ngày: " + selectedDate + ")");
        }

        holder.textViewCinemaName.setText(cinema.getName());

        if (cinema.isExpanded()) {
            holder.layoutCinemaDetails.setVisibility(View.VISIBLE);
            holder.textViewSubtitle.setText(cinema.getSubtitle());

            // Xoá thời gian chiếu cũ
            holder.layoutShowtimes.removeAllViews();

            for (String showtime : cinema.getShowtimes()) {
                TextView showtimeView = new TextView(context);
                showtimeView.setText(showtime);
                showtimeView.setTextSize(14);
                showtimeView.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
                showtimeView.setPadding(8, 4, 8, 4);
                showtimeView.setBackgroundResource(android.R.drawable.btn_default_small);

                // Xử lý khi nhấn vào thời gian chiếu
                showtimeView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, SeatSelectionActivity.class);
                    intent.putExtra("selected_date", selectedDate);
                    intent.putExtra("selected_time", showtime);
                    intent.putExtra("cinema_name", cinema.getName());
                    intent.putExtra("movie_title", movieTitle);
                    context.startActivity(intent);
                });

                holder.layoutShowtimes.addView(showtimeView);
            }

            holder.arrowCinema.setRotation(180);
        } else {
            holder.layoutCinemaDetails.setVisibility(View.GONE);
            holder.arrowCinema.setRotation(0);
        }

        holder.layoutCinemaHeader.setOnClickListener(v -> {
            cinema.setExpanded(!cinema.isExpanded());
            notifyItemChanged(position);
        });
    }

    @Override
    public void onViewRecycled(@NonNull CinemaViewHolder holder) {
        super.onViewRecycled(holder);
        // Reset trạng thái khu vực khi view holder được tái sử dụng
        holder.textViewArea.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return cinemaList.size();
    }

    // Phương thức cập nhật dữ liệu
    public void updateCinemaList(List<Cinema> newCinemaList) {
        this.cinemaList = newCinemaList;
        notifyDataSetChanged();

        // Debug: In danh sách sau khi cập nhật
        System.out.println("Cập nhật CinemaAdapter - Danh sách rạp (phim: " + movieTitle + ", ngày: " + selectedDate + "):");
        for (int i = 0; i < cinemaList.size(); i++) {
            Cinema cinema = cinemaList.get(i);
            System.out.println("Vị trí " + i + ": Area = " + cinema.getArea() + ", Name = " + cinema.getName());
        }
    }

    public static class CinemaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewArea;
        TextView textViewCinemaName;
        TextView textViewSubtitle;
        ImageView arrowCinema;
        LinearLayout layoutCinemaHeader;
        LinearLayout layoutCinemaDetails;
        LinearLayout layoutShowtimes;

        public CinemaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewArea = itemView.findViewById(R.id.text_view_area);
            textViewCinemaName = itemView.findViewById(R.id.text_view_cinema_name);
            textViewSubtitle = itemView.findViewById(R.id.text_view_subtitle);
            arrowCinema = itemView.findViewById(R.id.arrow_cinema);
            layoutCinemaHeader = itemView.findViewById(R.id.layout_cinema_header);
            layoutCinemaDetails = itemView.findViewById(R.id.layout_cinema_details);
            layoutShowtimes = itemView.findViewById(R.id.layout_showtimes);
        }
    }
}
package com.example.cinemas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    private List<DateItem> dateList;
    private Context context;
    private int selectedPosition = 0;
    private OnDateSelectedListener listener;

    // Interface để truyền ngày được chọn ra ngoài
    public interface OnDateSelectedListener {
        void onDateSelected(String date);
    }

    // Constructor
    public DateAdapter(Context context, List<DateItem> dateList) {
        this.context = context;
        this.dateList = dateList;
        if (context instanceof OnDateSelectedListener) {
            this.listener = (OnDateSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " phải triển khai OnDateSelectedListener");
        }
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        DateItem dateItem = dateList.get(position);

        // Lấy ngày hiện tại
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String today = dateFormat.format(new Date());

        // So sánh ngày trong dateItem với ngày hiện tại
        if (dateItem.getDate().equals(today)) {
            holder.textViewDate.setText("H.nay"); // Hiển thị "H.nay" nếu là hôm nay
        } else {
            holder.textViewDate.setText(dateItem.getDisplayText()); // Hiển thị ngày bình thường
        }

        // Highlight ngày được chọn
        if (position == selectedPosition) {
            holder.textViewDate.setBackgroundColor(ContextCompat.getColor(context, R.color.custom_red));
            holder.textViewDate.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.textViewDate.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            holder.textViewDate.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }

        // Xử lý sự kiện click
        holder.textViewDate.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            int previousPosition = selectedPosition;
            selectedPosition = adapterPosition;
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onDateSelected(dateList.get(adapterPosition).getDate());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    // Phương thức để cập nhật danh sách ngày
    public void updateDateList(List<DateItem> newDateList) {
        this.dateList = newDateList;
        notifyDataSetChanged();
    }

    // Getter để lấy vị trí ngày được chọn
    public int getSelectedPosition() {
        return selectedPosition;
    }

    // Setter để thay đổi ngày được chọn từ bên ngoài
    public void setSelectedPosition(int position) {
        if (position >= 0 && position < dateList.size()) {
            int previousPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onDateSelected(dateList.get(position).getDate());
            }
        }
    }

    // ViewHolder
    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.text_view_date);
        }
    }
}
package com.example.cinemas;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookTicketActivity extends AppCompatActivity implements DateAdapter.OnDateSelectedListener {

    private ImageView imageBack;
    private TextView textViewTitle;
    private RecyclerView recyclerViewDates;
    private RecyclerView recyclerViewCinemas;
    private DateAdapter dateAdapter;
    private CinemaAdapter cinemaAdapter;
    private List<DateItem> dateList;
    private List<Cinema> cinemaList;
    private DatabaseReference databaseReference;
    private String movieId;
    private String selectedDate;
    private String movieTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_ticket);

        // Nhận movie_id và movie_title từ Intent
        movieId = getIntent().getStringExtra("movie_id");
        movieTitle = getIntent().getStringExtra("movie_title");

        // Kiểm tra movieId hợp lệ
        if (movieId == null || movieId.isEmpty()) {
            Log.e("BookTicketActivity", "movie_id is null or empty");
            Toast.makeText(this, "Không tìm thấy thông tin phim", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo các thành phần giao diện
        imageBack = findViewById(R.id.image_back);
        textViewTitle = findViewById(R.id.text_view_title);
        recyclerViewDates = findViewById(R.id.recycler_view_dates);
        recyclerViewCinemas = findViewById(R.id.recycler_view_cinemas);

        // Kiểm tra nếu imageBack hoặc recyclerView là null
        if (imageBack == null || recyclerViewDates == null || recyclerViewCinemas == null) {
            Log.e("BookTicketActivity", "Không tìm thấy image_back hoặc recyclerView trong layout");
            Toast.makeText(this, "Lỗi giao diện, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cập nhật tiêu đề
        if (textViewTitle != null) {
            if (movieTitle != null && !movieTitle.isEmpty()) {
                textViewTitle.setText("Đặt vé xem phim: " + movieTitle);
            } else {
                textViewTitle.setText("Đặt vé xem phim");
            }
        } else {
            Log.e("BookTicketActivity", "Không tìm thấy text_view_title trong layout");
        }

        // Xử lý sự kiện nhấn nút quay lại
        imageBack.setOnClickListener(v -> finish());

        // Khởi tạo danh sách ngày
        dateList = new ArrayList<>();
        populateDates();

        // Kiểm tra danh sách ngày
        if (dateList.isEmpty()) {
            Log.e("BookTicketActivity", "Danh sách ngày trống");
            Toast.makeText(this, "Không có lịch chiếu nào trong tháng này", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Thiết lập RecyclerView cho danh sách ngày
        recyclerViewDates.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        dateAdapter = new DateAdapter(this, dateList);
        recyclerViewDates.setAdapter(dateAdapter);

        // Khởi tạo danh sách rạp chiếu phim
        cinemaList = new ArrayList<>();

        // Thiết lập RecyclerView cho danh sách rạp chiếu phim
        recyclerViewCinemas.setLayoutManager(new LinearLayoutManager(this));
        cinemaAdapter = new CinemaAdapter(this, cinemaList);
        cinemaAdapter.setMovieTitle(movieTitle); // Truyền movieTitle cho CinemaAdapter
        recyclerViewCinemas.setAdapter(cinemaAdapter);

        // Lấy ngày đầu tiên làm ngày mặc định và tải lịch chiếu
        if (!dateList.isEmpty()) {
            selectedDate = dateList.get(0).getDate();
            cinemaAdapter.setSelectedDate(selectedDate); // Truyền selectedDate cho CinemaAdapter
            // Kết nối với Firebase
            databaseReference = FirebaseDatabase.getInstance().getReference("cinemas").child(movieId);
            loadCinemasFromFirebase();
        }
    }

    // Hàm tạo danh sách ngày chiếu động từ ngày hiện tại đến cuối tháng
    private void populateDates() {
        dateList.clear();

        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayMonthYearFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat firebaseDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());

        // Lấy ngày hiện tại và số ngày trong tháng
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Đặt lại calendar về ngày hiện tại
        calendar.set(Calendar.DAY_OF_MONTH, currentDay);

        // Tạo danh sách ngày từ ngày hiện tại đến cuối tháng
        for (int i = currentDay; i <= daysInMonth; i++) {
            // Lấy thứ trong tuần
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            String dayOfWeekText = getDayOfWeekText(dayOfWeek, i == currentDay);

            // Lấy ngày (chỉ lấy số ngày, ví dụ: 03, 04, 05,...)
            String dayText = dayFormat.format(calendar.getTime());

            // Kết hợp thứ và ngày để hiển thị (ví dụ: "H.nay T.6 03" hoặc "T.7 04")
            String displayText = dayOfWeekText + "\n" + dayText;

            // Định dạng ngày để lưu vào Firebase (ví dụ: 03-04-2025)
            String fullDate = firebaseDateFormat.format(calendar.getTime());

            // Thêm vào danh sách
            dateList.add(new DateItem(displayText, fullDate));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    // Hàm chuyển đổi thứ trong tuần thành định dạng mong muốn
    private String getDayOfWeekText(int dayOfWeek, boolean isToday) {
        if (isToday) {
            return "H.nay";
        }
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return "T.2";
            case Calendar.TUESDAY:
                return "T.3";
            case Calendar.WEDNESDAY:
                return "T.4";
            case Calendar.THURSDAY:
                return "T.5";
            case Calendar.FRIDAY:
                return "T.6";
            case Calendar.SATURDAY:
                return "T.7";
            case Calendar.SUNDAY:
                return "CN";
            default:
                return "";
        }
    }

    // Hàm lấy dữ liệu rạp chiếu phim từ Firebase và nhóm theo khu vực
    private void loadCinemasFromFirebase() {
        if (selectedDate == null) {
            Log.e("BookTicketActivity", "Ngày được chọn là null");
            return;
        }

        databaseReference.child(selectedDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Cinema> tempCinemaList = new ArrayList<>();
                Log.d("BookTicketActivity", "Snapshot for date " + selectedDate + ": " + snapshot.toString());
                for (DataSnapshot cinemaSnapshot : snapshot.getChildren()) {
                    String area = cinemaSnapshot.child("area").getValue(String.class);
                    String name = cinemaSnapshot.child("name").getValue(String.class);
                    String subtitle = cinemaSnapshot.child("subtitle").getValue(String.class);

                    List<String> showtimes = new ArrayList<>();
                    DataSnapshot showtimesSnapshot = cinemaSnapshot.child("showtimes");
                    Log.d("BookTicketActivity", "showtimesSnapshot: " + showtimesSnapshot.toString());
                    for (DataSnapshot showtimeSnapshot : showtimesSnapshot.getChildren()) {
                        String showtime = showtimeSnapshot.getValue(String.class);
                        Log.d("BookTicketActivity", "showtime: " + showtime);
                        if (showtime != null) {
                            showtimes.add(showtime);
                        }
                    }

                    if (area != null && name != null && subtitle != null) {
                        tempCinemaList.add(new Cinema(area, name, subtitle, showtimes));
                    }
                }

                // Nhóm danh sách rạp theo khu vực
                cinemaList.clear();
                cinemaList.addAll(groupByArea(tempCinemaList));

                Log.d("BookTicketActivity", "cinemaList size: " + cinemaList.size());
                if (cinemaList.isEmpty()) {
                    Toast.makeText(BookTicketActivity.this, "Không có lịch chiếu cho ngày " + selectedDate, Toast.LENGTH_SHORT).show();
                }

                // Cập nhật danh sách rạp trong CinemaAdapter
                cinemaAdapter.updateCinemaList(cinemaList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BookTicketActivity", "Lỗi khi lấy dữ liệu từ Firebase: " + error.getMessage());
                Toast.makeText(BookTicketActivity.this, "Lỗi khi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức nhóm danh sách rạp theo khu vực
    private List<Cinema> groupByArea(List<Cinema> cinemas) {
        // Sử dụng HashMap để nhóm các rạp theo khu vực
        Map<String, List<Cinema>> areaMap = new HashMap<>();

        // Nhóm các rạp vào danh sách theo khu vực
        for (Cinema cinema : cinemas) {
            String area = cinema.getArea();
            if (!areaMap.containsKey(area)) {
                areaMap.put(area, new ArrayList<>());
            }
            areaMap.get(area).add(cinema);
        }

        // Sắp xếp rạp trong mỗi khu vực theo tên
        for (List<Cinema> cinemaInArea : areaMap.values()) {
            Collections.sort(cinemaInArea, Comparator.comparing(Cinema::getName));
        }

        // Tạo danh sách khu vực đã sắp xếp (Thành phố Hồ Chí Minh trước, Hà Nội sau)
        List<String> sortedAreas = new ArrayList<>(areaMap.keySet());
        Collections.sort(sortedAreas, (area1, area2) -> {
            // Đảm bảo "Thành phố Hồ Chí Minh" đứng trước "Hà Nội"
            if (area1.equals("Thành phố Hồ Chí Minh") && area2.equals("Hà Nội")) {
                return -1;
            } else if (area1.equals("Hà Nội") && area2.equals("Thành phố Hồ Chí Minh")) {
                return 1;
            }
            return area1.compareTo(area2);
        });

        // Debug: In danh sách khu vực đã sắp xếp
        Log.d("BookTicketActivity", "Danh sách khu vực đã sắp xếp: " + sortedAreas.toString());

        // Gộp các rạp từ các khu vực thành một danh sách duy nhất
        List<Cinema> groupedCinemas = new ArrayList<>();
        for (String area : sortedAreas) {
            groupedCinemas.addAll(areaMap.get(area));
        }

        // Debug: In danh sách sau khi nhóm
        Log.d("BookTicketActivity", "Danh sách rạp sau khi nhóm (ngày: " + selectedDate + "):");
        for (int i = 0; i < groupedCinemas.size(); i++) {
            Cinema cinema = groupedCinemas.get(i);
            Log.d("BookTicketActivity", "Vị trí " + i + ": Area = " + cinema.getArea() + ", Name = " + cinema.getName());
        }

        return groupedCinemas;
    }

    @Override
    public void onDateSelected(String date) {
        selectedDate = date;
        cinemaAdapter.setSelectedDate(selectedDate); // Cập nhật selectedDate cho CinemaAdapter
        loadCinemasFromFirebase();
    }
}
package com.example.cinemas;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SeatSelectionActivity extends AppCompatActivity {

    private GridLayout gridSeats;
    private TextView textTotalPrice;
    private Button buttonContinue;
    private ImageView imageBack;
    private List<String> selectedSeats;
    private static final int PRICE_PER_SEAT = 300000;

    private String selectedDate, selectedTime, cinemaName, movieTitle;

    private Set<String> bookedSeats = new HashSet<>();

    private DatabaseReference seatsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        gridSeats = findViewById(R.id.grid_seats);
        textTotalPrice = findViewById(R.id.text_total_price);
        buttonContinue = findViewById(R.id.button_continue);
        imageBack = findViewById(R.id.image_back);

        Intent intent = getIntent();
        selectedDate = intent.getStringExtra("selected_date");
        selectedTime = intent.getStringExtra("selected_time");
        cinemaName = intent.getStringExtra("cinema_name");
        movieTitle = intent.getStringExtra("movie_title");

        selectedSeats = new ArrayList<>();

        seatsRef = FirebaseDatabase.getInstance().getReference("booked_seats")
                .child(cinemaName)
                .child(movieTitle)
                .child(selectedDate)
                .child(selectedTime);

        imageBack.setOnClickListener(v -> finish());

        buttonContinue.setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một ghế!", Toast.LENGTH_SHORT).show();
            } else {
                Intent foodComboIntent = new Intent(SeatSelectionActivity.this, FoodComboActivity.class);
                foodComboIntent.putStringArrayListExtra("selected_seats", new ArrayList<>(selectedSeats));
                foodComboIntent.putExtra("seat_price", selectedSeats.size() * PRICE_PER_SEAT);
                foodComboIntent.putExtra("selected_date", selectedDate);
                foodComboIntent.putExtra("selected_time", selectedTime);
                foodComboIntent.putExtra("cinema_name", cinemaName);
                foodComboIntent.putExtra("movie_title", movieTitle);
                startActivity(foodComboIntent);
            }
        });

        loadBookedSeats();
    }

    private void loadBookedSeats() {
        seatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                bookedSeats.clear();
                for (DataSnapshot seatSnap : snapshot.getChildren()) {
                    bookedSeats.add(seatSnap.getKey());
                }
                setupSeats();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(SeatSelectionActivity.this, "Lỗi tải ghế đã đặt", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSeats() {
        String[] rows = {"A", "B", "C", "D"};
        int subColumns = 2;
        int groups = 3;
        gridSeats.setColumnCount(groups * subColumns + (groups - 1));

        for (int row = 0; row < rows.length; row++) {
            for (int group = 0; group < groups; group++) {
                for (int subCol = 0; subCol < subColumns; subCol++) {
                    int colIndex = group * (subColumns + 1) + subCol;
                    int seatNumber = group * subColumns + subCol + 1;
                    String seatId = rows[row] + seatNumber;

                    TextView seat = new TextView(this);
                    seat.setText(seatId);
                    seat.setPadding(8, 8, 8, 8);
                    seat.setTextColor(getResources().getColor(android.R.color.black));
                    seat.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = 0;
                    params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    params.columnSpec = GridLayout.spec(colIndex, 1f);
                    params.rowSpec = GridLayout.spec(row);
                    params.setMargins(4, 4, 4, 4);
                    seat.setLayoutParams(params);

                    if (bookedSeats.contains(seatId)) {
                        seat.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
                        seat.setEnabled(false);
                    } else {
                        seat.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
                        seat.setOnClickListener(v -> toggleSeat(seat, seatId));
                    }

                    gridSeats.addView(seat);
                }

                if (group < groups - 1) {
                    View spacer = new View(this);
                    GridLayout.LayoutParams spacerParams = new GridLayout.LayoutParams();
                    spacerParams.width = 0;
                    spacerParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    spacerParams.columnSpec = GridLayout.spec(group * (subColumns + 1) + subColumns, 1f);
                    spacerParams.rowSpec = GridLayout.spec(row);
                    spacerParams.setMargins(8, 4, 8, 4);
                    spacer.setLayoutParams(spacerParams);
                    gridSeats.addView(spacer);
                }
            }
        }
    }

    private void toggleSeat(TextView seat, String seatId) {
        ColorDrawable background = (ColorDrawable) seat.getBackground();
        int color = background.getColor();

        int selectedColor = ContextCompat.getColor(this, R.color.seat_selected);
        int defaultColor = ContextCompat.getColor(this, android.R.color.darker_gray);

        if (color == defaultColor) {
            seat.setBackgroundColor(selectedColor);
            selectedSeats.add(seatId);
        } else if (color == selectedColor) {
            seat.setBackgroundColor(defaultColor);
            selectedSeats.remove(seatId);
        }

        updateTotalPrice();
    }

    private void updateTotalPrice() {
        int totalPrice = selectedSeats.size() * PRICE_PER_SEAT;
        textTotalPrice.setText(String.format("%,d đ", totalPrice));
    }
}
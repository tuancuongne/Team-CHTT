package com.example.cinemas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private ImageView imageBack;
    private LinearLayout momoLayout, zalopayLayout, tknhLayout, shopeepayLayout;
    private CheckBox checkboxMomo, checkboxZaloPay, checkboxTknh, checkboxShopeePay;
    private Button buttonPay;
    private TextView textTicketCount, textTotalPrice;
    private LinearLayout layoutSingleCombo;
    private ImageView imageSingleCombo;
    private TextView textSingleComboName, textSingleComboQuantity;
    private RecyclerView recyclerViewCombos;
    private DatabaseReference seatsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        imageBack = findViewById(R.id.image_back);
        momoLayout = findViewById(R.id.momo);
        zalopayLayout = findViewById(R.id.zalopay);
        tknhLayout = findViewById(R.id.tknh);
        shopeepayLayout = findViewById(R.id.shopeepay);
        checkboxMomo = findViewById(R.id.checkbox_momo);
        checkboxZaloPay = findViewById(R.id.checkbox_zalopay);
        checkboxTknh = findViewById(R.id.checkbox_tknh);
        checkboxShopeePay = findViewById(R.id.checkbox_shopeepay);
        buttonPay = findViewById(R.id.button_pay);
        textTicketCount = findViewById(R.id.text_ticket_count);
        textTotalPrice = findViewById(R.id.text_total_price);
        layoutSingleCombo = findViewById(R.id.layout_single_combo);
        imageSingleCombo = findViewById(R.id.image_single_combo);
        textSingleComboName = findViewById(R.id.text_single_combo_name);
        textSingleComboQuantity = findViewById(R.id.text_single_combo_quantity);
        recyclerViewCombos = findViewById(R.id.recycler_view_combos);

        Intent intent = getIntent();
        String ticketCount = String.valueOf(intent.getIntExtra("ticket_count", 0));
        String totalPrice = String.valueOf(intent.getIntExtra("total_price", 0));
        String cinemaName = intent.getStringExtra("cinema_name");
        String movieTitle = intent.getStringExtra("movie_title");
        String selectedDate = intent.getStringExtra("selected_date");
        String selectedTime = intent.getStringExtra("selected_time");
        String seats = intent.getStringExtra("seats");
        ArrayList<String> selectedSeats = intent.getStringArrayListExtra("selected_seats");
        ArrayList<SelectedCombo> selectedCombos = (ArrayList<SelectedCombo>) intent.getSerializableExtra("selected_combos");

        seatsRef = FirebaseDatabase.getInstance().getReference("booked_seats")
                .child(cinemaName).child(movieTitle).child(selectedDate).child(selectedTime);

        if (ticketCount != null && !ticketCount.isEmpty()) {
            textTicketCount.setText(ticketCount);
        }

        if (totalPrice != null && !totalPrice.isEmpty()) {
            textTotalPrice.setText(String.format("%,d", Integer.parseInt(totalPrice)) + " đ");
        }

        if (selectedCombos != null && !selectedCombos.isEmpty()) {
            if (selectedCombos.size() == 1) {
                layoutSingleCombo.setVisibility(View.VISIBLE);
                recyclerViewCombos.setVisibility(View.GONE);
                SelectedCombo combo = selectedCombos.get(0);
                imageSingleCombo.setImageResource(combo.getImageResId());
                textSingleComboName.setText(combo.getName());
                textSingleComboQuantity.setText(String.valueOf(combo.getQuantity()));
            } else {
                layoutSingleCombo.setVisibility(View.GONE);
                recyclerViewCombos.setVisibility(View.VISIBLE);
                recyclerViewCombos.setLayoutManager(new LinearLayoutManager(this));
                ComboAdapter adapter = new ComboAdapter(this, selectedCombos);
                recyclerViewCombos.setAdapter(adapter);
            }
        } else {
            layoutSingleCombo.setVisibility(View.GONE);
            recyclerViewCombos.setVisibility(View.GONE);
        }

        imageBack.setOnClickListener(v -> finish());

        momoLayout.setOnClickListener(v -> {
            checkboxMomo.setVisibility(View.VISIBLE);
            checkboxMomo.setChecked(true);
            checkboxZaloPay.setChecked(false);
            checkboxTknh.setChecked(false);
            checkboxShopeePay.setChecked(false);
            checkboxZaloPay.setVisibility(View.GONE);
            checkboxTknh.setVisibility(View.GONE);
            checkboxShopeePay.setVisibility(View.GONE);
        });

        zalopayLayout.setOnClickListener(v -> {
            checkboxZaloPay.setVisibility(View.VISIBLE);
            checkboxZaloPay.setChecked(true);
            checkboxMomo.setChecked(false);
            checkboxTknh.setChecked(false);
            checkboxShopeePay.setChecked(false);
            checkboxMomo.setVisibility(View.GONE);
            checkboxTknh.setVisibility(View.GONE);
            checkboxShopeePay.setVisibility(View.GONE);
        });

        tknhLayout.setOnClickListener(v -> {
            checkboxTknh.setVisibility(View.VISIBLE);
            checkboxTknh.setChecked(true);
            checkboxMomo.setChecked(false);
            checkboxZaloPay.setChecked(false);
            checkboxShopeePay.setChecked(false);
            checkboxMomo.setVisibility(View.GONE);
            checkboxZaloPay.setVisibility(View.GONE);
            checkboxShopeePay.setVisibility(View.GONE);
        });

        shopeepayLayout.setOnClickListener(v -> {
            checkboxShopeePay.setVisibility(View.VISIBLE);
            checkboxShopeePay.setChecked(true);
            checkboxMomo.setChecked(false);
            checkboxZaloPay.setChecked(false);
            checkboxTknh.setChecked(false);
            checkboxMomo.setVisibility(View.GONE);
            checkboxZaloPay.setVisibility(View.GONE);
            checkboxTknh.setVisibility(View.GONE);
        });

        buttonPay.setOnClickListener(v -> {
            if (checkboxMomo.isChecked()) {
                handlePaymentSuccess("Momo", cinemaName, movieTitle, selectedDate, selectedTime, seats, totalPrice, selectedSeats, selectedCombos);
            } else if (checkboxZaloPay.isChecked()) {
                handlePaymentSuccess("ZaloPay", cinemaName, movieTitle, selectedDate, selectedTime, seats, totalPrice, selectedSeats, selectedCombos);
            } else if (checkboxShopeePay.isChecked()) {
                handlePaymentSuccess("ShopeePay", cinemaName, movieTitle, selectedDate, selectedTime, seats, totalPrice, selectedSeats, selectedCombos);
            } else if (checkboxTknh.isChecked()) {
                Intent bankIntent = new Intent(PaymentActivity.this, BankSelectionActivity.class);
                bankIntent.putExtra("bank_name", "Tài khoản ngân hàng");
                bankIntent.putExtra("cinema_name", cinemaName);
                bankIntent.putExtra("movie_title", movieTitle);
                bankIntent.putExtra("selected_date", selectedDate);
                bankIntent.putExtra("selected_time", selectedTime);
                bankIntent.putExtra("seats", seats);
                bankIntent.putExtra("total_price", totalPrice);
                bankIntent.putStringArrayListExtra("selected_seats", selectedSeats);
                bankIntent.putExtra("selected_combos", selectedCombos);
                startActivity(bankIntent);
                finish();
            } else {
                Toast.makeText(PaymentActivity.this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            }
        });

        checkboxMomo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkboxZaloPay.setChecked(false);
                checkboxTknh.setChecked(false);
                checkboxShopeePay.setChecked(false);
            }
        });

        checkboxZaloPay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkboxMomo.setChecked(false);
                checkboxTknh.setChecked(false);
                checkboxShopeePay.setChecked(false);
            }
        });

        checkboxTknh.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkboxMomo.setChecked(false);
                checkboxZaloPay.setChecked(false);
                checkboxShopeePay.setChecked(false);
            }
        });

        checkboxShopeePay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkboxMomo.setChecked(false);
                checkboxZaloPay.setChecked(false);
                checkboxTknh.setChecked(false);
            }
        });
    }

    private void handlePaymentSuccess(String paymentMethod,
                                      String cinemaName,
                                      String movieTitle,
                                      String selectedDate,
                                      String selectedTime,
                                      String seats,
                                      String totalPrice,
                                      ArrayList<String> selectedSeats,
                                      ArrayList<SelectedCombo> selectedCombos) {
        Map<String, Object> bookedSeatsMap = new HashMap<>();
        for (String seat : selectedSeats) {
            bookedSeatsMap.put(seat, true);
        }

        seatsRef.updateChildren(bookedSeatsMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PaymentActivity.this, "Thanh toán thành công qua " + paymentMethod, Toast.LENGTH_SHORT).show();

                    Intent successIntent = new Intent(PaymentActivity.this, PaymentSuccessActivity.class);
                    successIntent.putExtra("cinema_name", cinemaName);
                    successIntent.putExtra("movie_title", movieTitle);
                    successIntent.putExtra("selected_date", selectedDate);
                    successIntent.putExtra("selected_time", selectedTime);
                    successIntent.putExtra("seats", seats);
                    successIntent.putExtra("food", getSelectedCombosString(selectedCombos));
                    successIntent.putExtra("total_price", totalPrice);
                    successIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(successIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PaymentActivity.this, "Lỗi lưu ghế: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String getSelectedCombosString(ArrayList<SelectedCombo> selectedCombos) {
        if (selectedCombos == null || selectedCombos.isEmpty()) {
            return "Không có";
        }
        StringBuilder food = new StringBuilder();
        for (SelectedCombo combo : selectedCombos) {
            food.append(combo.getName()).append(" (").append(combo.getQuantity()).append("), ");
        }
        return food.substring(0, food.length() - 2);
    }
}
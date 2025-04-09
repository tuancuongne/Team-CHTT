package com.example.cinemas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class FoodComboActivity extends AppCompatActivity {

    private TextView textTotalPrice;
    private Button buttonContinue;
    private ImageView imageBack;
    private TextView quantityMyCombo, quantityCgvCombo, quantityPremiumCgvCombo, quantityPremiumMyCombo;
    private TextView priceMyCombo, priceCgvCombo, pricePremiumCgvCombo, pricePremiumMyCombo;
    private ImageButton buttonIncreaseMyCombo, buttonDecreaseMyCombo;
    private ImageButton buttonIncreaseCgvCombo, buttonDecreaseCgvCombo;
    private ImageButton buttonIncreasePremiumCgvCombo, buttonDecreasePremiumCgvCombo;
    private ImageButton buttonIncreasePremiumMyCombo, buttonDecreasePremiumMyCombo;

    private int myComboCount = 0;
    private int cgvComboCount = 0;
    private int premiumCgvComboCount = 0;
    private int premiumMyComboCount = 0;

    private static final int PRICE_MY_COMBO = 95000;
    private static final int PRICE_CGV_COMBO = 125000;
    private static final int PRICE_PREMIUM_CGV_COMBO = 135000;
    private static final int PRICE_PREMIUM_MY_COMBO = 115000;

    private int seatPrice;
    private ArrayList<String> selectedSeats;
    private ArrayList<Combo> comboList;
    private String selectedDate, selectedTime, cinemaName, movieTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_combo);

        textTotalPrice = findViewById(R.id.text_total_price);
        buttonContinue = findViewById(R.id.button_continue);
        imageBack = findViewById(R.id.back_arrow);

        quantityMyCombo = findViewById(R.id.quantity_my_combo);
        quantityCgvCombo = findViewById(R.id.quantity_cgv_combo);
        quantityPremiumCgvCombo = findViewById(R.id.quantity_premium_cgv_combo);
        quantityPremiumMyCombo = findViewById(R.id.quantity_premium_my_combo);

        priceMyCombo = findViewById(R.id.price_my_combo);
        priceCgvCombo = findViewById(R.id.price_cgv_combo);
        pricePremiumCgvCombo = findViewById(R.id.price_premium_cgv_combo);
        pricePremiumMyCombo = findViewById(R.id.price_premium_my_combo);

        buttonIncreaseMyCombo = findViewById(R.id.button_increase_my_combo);
        buttonDecreaseMyCombo = findViewById(R.id.button_decrease_my_combo);
        buttonIncreaseCgvCombo = findViewById(R.id.button_increase_cgv_combo);
        buttonDecreaseCgvCombo = findViewById(R.id.button_decrease_cgv_combo);
        buttonIncreasePremiumCgvCombo = findViewById(R.id.button_increase_premium_cgv_combo);
        buttonDecreasePremiumCgvCombo = findViewById(R.id.button_decrease_premium_cgv_combo);
        buttonIncreasePremiumMyCombo = findViewById(R.id.button_increase_premium_my_combo);
        buttonDecreasePremiumMyCombo = findViewById(R.id.button_decrease_premium_my_combo);

        Intent intent = getIntent();
        seatPrice = intent.getIntExtra("seat_price", 0);
        selectedSeats = intent.getStringArrayListExtra("selected_seats");
        selectedDate = intent.getStringExtra("selected_date");
        selectedTime = intent.getStringExtra("selected_time");
        cinemaName = intent.getStringExtra("cinema_name");
        movieTitle = intent.getStringExtra("movie_title");

        comboList = new ArrayList<>();
        comboList.add(new Combo("My Combo", PRICE_MY_COMBO, R.drawable.combo1));
        comboList.add(new Combo("CGV Combo", PRICE_CGV_COMBO, R.drawable.combo2));
        comboList.add(new Combo("Premium CGV Combo", PRICE_PREMIUM_CGV_COMBO, R.drawable.combo3));
        comboList.add(new Combo("Premium My Combo", PRICE_PREMIUM_MY_COMBO, R.drawable.combo4));

        priceMyCombo.setText(String.format("%,d", PRICE_MY_COMBO));
        priceCgvCombo.setText(String.format("%,d", PRICE_CGV_COMBO));
        pricePremiumCgvCombo.setText(String.format("%,d", PRICE_PREMIUM_CGV_COMBO));
        pricePremiumMyCombo.setText(String.format("%,d", PRICE_PREMIUM_MY_COMBO));

        updateTotalPrice();

        imageBack.setOnClickListener(v -> finish());

        buttonIncreaseMyCombo.setOnClickListener(v -> {
            myComboCount++;
            quantityMyCombo.setText(String.valueOf(myComboCount));
            updateTotalPrice();
        });

        buttonDecreaseMyCombo.setOnClickListener(v -> {
            if (myComboCount > 0) {
                myComboCount--;
                quantityMyCombo.setText(String.valueOf(myComboCount));
                updateTotalPrice();
            }
        });

        buttonIncreaseCgvCombo.setOnClickListener(v -> {
            cgvComboCount++;
            quantityCgvCombo.setText(String.valueOf(cgvComboCount));
            updateTotalPrice();
        });

        buttonDecreaseCgvCombo.setOnClickListener(v -> {
            if (cgvComboCount > 0) {
                cgvComboCount--;
                quantityCgvCombo.setText(String.valueOf(cgvComboCount));
                updateTotalPrice();
            }
        });

        buttonIncreasePremiumCgvCombo.setOnClickListener(v -> {
            premiumCgvComboCount++;
            quantityPremiumCgvCombo.setText(String.valueOf(premiumCgvComboCount));
            updateTotalPrice();
        });

        buttonDecreasePremiumCgvCombo.setOnClickListener(v -> {
            if (premiumCgvComboCount > 0) {
                premiumCgvComboCount--;
                quantityPremiumCgvCombo.setText(String.valueOf(premiumCgvComboCount));
                updateTotalPrice();
            }
        });

        buttonIncreasePremiumMyCombo.setOnClickListener(v -> {
            premiumMyComboCount++;
            quantityPremiumMyCombo.setText(String.valueOf(premiumMyComboCount));
            updateTotalPrice();
        });

        buttonDecreasePremiumMyCombo.setOnClickListener(v -> {
            if (premiumMyComboCount > 0) {
                premiumMyComboCount--;
                quantityPremiumMyCombo.setText(String.valueOf(premiumMyComboCount));
                updateTotalPrice();
            }
        });

        buttonContinue.setOnClickListener(v -> {
            ArrayList<SelectedCombo> selectedCombos = new ArrayList<>();

            if (myComboCount > 0) {
                selectedCombos.add(new SelectedCombo(
                        comboList.get(0).getName(),
                        comboList.get(0).getPrice(),
                        myComboCount,
                        comboList.get(0).getImageResId()
                ));
            }

            if (cgvComboCount > 0) {
                selectedCombos.add(new SelectedCombo(
                        comboList.get(1).getName(),
                        comboList.get(1).getPrice(),
                        cgvComboCount,
                        comboList.get(1).getImageResId()
                ));
            }

            if (premiumCgvComboCount > 0) {
                selectedCombos.add(new SelectedCombo(
                        comboList.get(2).getName(),
                        comboList.get(2).getPrice(),
                        premiumCgvComboCount,
                        comboList.get(2).getImageResId()
                ));
            }

            if (premiumMyComboCount > 0) {
                selectedCombos.add(new SelectedCombo(
                        comboList.get(3).getName(),
                        comboList.get(3).getPrice(),
                        premiumMyComboCount,
                        comboList.get(3).getImageResId()
                ));
            }

            int totalComboCount = myComboCount + cgvComboCount + premiumCgvComboCount + premiumMyComboCount;
            int comboPrice = (myComboCount * PRICE_MY_COMBO) +
                    (cgvComboCount * PRICE_CGV_COMBO) +
                    (premiumCgvComboCount * PRICE_PREMIUM_CGV_COMBO) +
                    (premiumMyComboCount * PRICE_PREMIUM_MY_COMBO);
            int totalPrice = seatPrice + comboPrice;

            int ticketCount = selectedSeats != null ? selectedSeats.size() : 0;

            Intent paymentIntent = new Intent(FoodComboActivity.this, PaymentActivity.class);
            paymentIntent.putExtra("ticket_count", ticketCount);
            paymentIntent.putExtra("total_price", totalPrice);
            paymentIntent.putExtra("selected_combos", selectedCombos);
            paymentIntent.putExtra("selected_date", selectedDate);
            paymentIntent.putExtra("selected_time", selectedTime);
            paymentIntent.putExtra("cinema_name", cinemaName);
            paymentIntent.putExtra("movie_title", movieTitle);
            paymentIntent.putExtra("seats", String.join(", ", selectedSeats));
            paymentIntent.putStringArrayListExtra("selected_seats", selectedSeats);
            startActivity(paymentIntent);
        });
    }

    private void updateTotalPrice() {
        int comboPrice = (myComboCount * PRICE_MY_COMBO) +
                (cgvComboCount * PRICE_CGV_COMBO) +
                (premiumCgvComboCount * PRICE_PREMIUM_CGV_COMBO) +
                (premiumMyComboCount * PRICE_PREMIUM_MY_COMBO);
        int totalPrice = seatPrice + comboPrice;
        textTotalPrice.setText(String.format("%,d", totalPrice) + " đ");
    }
}
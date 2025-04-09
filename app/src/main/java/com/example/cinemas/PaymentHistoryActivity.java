package com.example.cinemas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.text.DecimalFormat;

public class PaymentHistoryActivity extends AppCompatActivity {

    private ImageView imageBack, qrCodeImage;
    private Button buttonBackToHome;
    private TextView textBookingCode, textCinemaName, textMovieInfo, textShowtime, textSeats, textFood, textTotalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        // Ánh xạ các thành phần từ XML
        imageBack = findViewById(R.id.image_back);
        qrCodeImage = findViewById(R.id.qr_code_image);
        buttonBackToHome = findViewById(R.id.button_back_to_home);
        textBookingCode = findViewById(R.id.text_booking_code);
        textCinemaName = findViewById(R.id.text_cinema_name);
        textMovieInfo = findViewById(R.id.text_movie_info);
        textShowtime = findViewById(R.id.text_showtime);
        textSeats = findViewById(R.id.text_seats);
        textFood = findViewById(R.id.text_food);
        textTotalPrice = findViewById(R.id.text_total_price);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        String bookingCode = intent.getStringExtra("BOOKING_CODE");
        String cinemaName = intent.getStringExtra("CINEMA_NAME");
        String movieInfo = intent.getStringExtra("MOVIE_INFO");
        String showtime = intent.getStringExtra("SHOWTIME");
        String seats = intent.getStringExtra("SEATS");
        String food = intent.getStringExtra("FOOD");
        String totalPrice = intent.getStringExtra("TOTAL_PRICE");

        // Định dạng totalPrice với dấu chấm phân cách
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
        String formattedTotalPrice = totalPrice != null ? decimalFormat.format(Double.parseDouble(totalPrice)) : "790,000";
        formattedTotalPrice = formattedTotalPrice.replace(",", ".");

        // Hiển thị dữ liệu
        textBookingCode.setText(bookingCode != null ? bookingCode : "abczy");
        textCinemaName.setText(cinemaName != null ? cinemaName : "CGV Hoàng Văn Thụ");
        textMovieInfo.setText(movieInfo != null ? movieInfo : "The Avengers - Hồi kết (2023)");
        textShowtime.setText(showtime != null ? showtime : "03/04/2023, 21:00");
        textSeats.setText(seats != null ? seats : "C6, C5");
        textFood.setText(food != null ? food : "My Combo");
        textTotalPrice.setText(formattedTotalPrice + " đ"); // Thêm "đ" vào sau số tiền đã định dạng

        // Tạo và hiển thị mã QR
        Bitmap qrCodeBitmap = generateQRCode(bookingCode != null ? bookingCode : "abczy", 300, 300);
        if (qrCodeBitmap != null) {
            qrCodeImage.setImageBitmap(qrCodeBitmap);
        }

        // Xử lý nút Back
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Xử lý nút "Quay lại Trang chủ"
        buttonBackToHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(PaymentHistoryActivity.this, MainActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(homeIntent);
            finish(); // Kết thúc PaymentSuccessActivity
        });
    }

    // Hàm tạo mã QR
    private Bitmap generateQRCode(String text, int width, int height) {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
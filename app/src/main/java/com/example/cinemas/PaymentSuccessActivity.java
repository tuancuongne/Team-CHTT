package com.example.cinemas;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PaymentSuccessActivity extends AppCompatActivity {

    private TextView textBookingCode, textCinemaName, textMovieInfo, textShowtime, textSeats, textFood, textTotalPrice;
    private ImageView qrCodeImage;
    private Button buttonBackToHome, buttonSaveImage;
    private Bitmap qrBitmap;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        textBookingCode = findViewById(R.id.text_booking_code);
        textCinemaName = findViewById(R.id.text_cinema_name);
        textMovieInfo = findViewById(R.id.text_movie_info);
        textShowtime = findViewById(R.id.text_showtime);
        textSeats = findViewById(R.id.text_seats);
        textFood = findViewById(R.id.text_food);
        textTotalPrice = findViewById(R.id.text_total_price);
        qrCodeImage = findViewById(R.id.qr_code_image);
        buttonBackToHome = findViewById(R.id.button_back_to_home);
        buttonSaveImage = findViewById(R.id.button_save_image);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("transactions");

        Intent intent = getIntent();
        String cinemaName = intent.getStringExtra("cinema_name");
        String movieTitle = intent.getStringExtra("movie_title");
        String selectedDate = intent.getStringExtra("selected_date");
        String selectedTime = intent.getStringExtra("selected_time");
        String showtime = (selectedDate != null && selectedTime != null) ? selectedDate + ", " + selectedTime : null;
        String seats = intent.getStringExtra("seats");
        String food = intent.getStringExtra("food");
        String totalPrice = intent.getStringExtra("total_price");

        String bookingCode = generateRandomBookingCode();

        // Định dạng totalPrice với dấu chấm phân cách
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
        String formattedTotalPrice = totalPrice != null ? decimalFormat.format(Double.parseDouble(totalPrice)) : "790,000";
        formattedTotalPrice = formattedTotalPrice.replace(",", ".");

        textBookingCode.setText(bookingCode);
        textCinemaName.setText(cinemaName != null ? cinemaName : "CGV Hoàng Văn Thụ");
        textMovieInfo.setText(movieTitle != null ? movieTitle : "The Avengers - Hồi kết (2023)");
        textShowtime.setText(showtime != null ? showtime : "03/04/2023, 21:00");
        textSeats.setText(seats != null ? seats : "C6, C5");
        textFood.setText(food != null ? food : "Không có");
        textTotalPrice.setText(formattedTotalPrice + " đ");

        qrBitmap = generateQRCode(bookingCode);
        if (qrBitmap != null) {
            qrCodeImage.setImageBitmap(qrBitmap);
        }

        saveTransactionToFirebase(bookingCode, cinemaName, movieTitle, showtime, seats, food, totalPrice);

        buttonBackToHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(PaymentSuccessActivity.this, MainActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(homeIntent);
            finish();
        });

        buttonSaveImage.setOnClickListener(v -> {
            if (qrBitmap != null) {
                saveImageToGallery(qrBitmap, "QR_" + bookingCode);
            } else {
                Toast.makeText(this, "Không có mã QR để lưu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTransactionToFirebase(String bookingCode, String cinemaName, String movieTitle, String showtime, String seats, String food, String totalPrice) {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "anonymous";
        String transactionId = databaseReference.push().getKey();

        Map<String, Object> transaction = new HashMap<>();
        transaction.put("bookingCode", bookingCode);
        transaction.put("cinemaName", cinemaName != null ? cinemaName : "CGV Hoàng Văn Thụ");
        transaction.put("movieTitle", movieTitle != null ? movieTitle : "The Avengers - Hồi kết (2023)");
        transaction.put("showtime", showtime != null ? showtime : "03/04/2023, 21:00");
        transaction.put("seats", seats != null ? seats : "C6, C5");
        transaction.put("food", food != null ? food : "Không có");
        transaction.put("totalPrice", totalPrice != null ? totalPrice : "790,000");

        databaseReference.child(userId).child(transactionId).setValue(transaction)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã lưu giao dịch!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi lưu giao dịch!", Toast.LENGTH_SHORT).show());
    }

    private String generateRandomBookingCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder bookingCode = new StringBuilder();
        Random random = new Random();
        int length = 5;
        for (int i = 0; i < length; i++) {
            bookingCode.append(characters.charAt(random.nextInt(characters.length())));
        }
        return bookingCode.toString();
    }

    private Bitmap generateQRCode(String content) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            Bitmap bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565);
            for (int x = 0; x < 512; x++) {
                for (int y = 0; y < 512; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveImageToGallery(Bitmap bitmap, String name) {
        OutputStream fos;
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".png");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.IS_PENDING, 1);
            }

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                fos = getContentResolver().openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                if (fos != null) fos.close();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear();
                    values.put(MediaStore.Images.Media.IS_PENDING, 0);
                    getContentResolver().update(uri, values, null, null);
                }

                Toast.makeText(this, "Đã lưu mã QR vào Thư viện!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lưu ảnh!", Toast.LENGTH_SHORT).show();
        }
    }
}
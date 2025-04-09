package com.example.cinemas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BankPaymentActivity extends AppCompatActivity {

    private ImageView imageBack, imageBankLogo;
    private TextView textBankName;
    private EditText editCardNumber, editExpiryDate, editCardHolder;
    private CheckBox checkBoxSaveInfo;
    private Button buttonConfirmPayment;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "BankPaymentPrefs";
    private static final String KEY_CARD_NUMBER = "card_number";
    private static final String KEY_EXPIRY_DATE = "expiry_date";
    private static final String KEY_CARD_HOLDER = "card_holder";
    private static final String KEY_SAVE_INFO = "save_info";
    private boolean isFormatting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_payment);

        imageBack = findViewById(R.id.image_back);
        textBankName = findViewById(R.id.text_bank_name);
        editCardNumber = findViewById(R.id.edit_card_number);
        editExpiryDate = findViewById(R.id.edit_expiry_date);
        editCardHolder = findViewById(R.id.edit_card_holder);
        checkBoxSaveInfo = findViewById(R.id.checkbox);
        buttonConfirmPayment = findViewById(R.id.button_confirm_payment);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        String bankName = getIntent().getStringExtra("bank_name");
        textBankName.setText("Thanh toán qua " + bankName);

        loadSavedPaymentInfo();

        editCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;

                isFormatting = true;
                String input = s.toString().replaceAll("\\s", "");
                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < input.length(); i++) {
                    if (i > 0 && i % 4 == 0) {
                        formatted.append(" ");
                    }
                    formatted.append(input.charAt(i));
                }

                editCardNumber.setText(formatted.toString());
                editCardNumber.setSelection(formatted.length());
                isFormatting = false;
            }
        });

        imageBack.setOnClickListener(v -> finish());

        buttonConfirmPayment.setOnClickListener(v -> {
            String cardNumber = editCardNumber.getText().toString().trim();
            String expiryDate = editExpiryDate.getText().toString().trim();
            String cardHolder = editCardHolder.getText().toString().trim();

            if (cardNumber.isEmpty() || expiryDate.isEmpty() || cardHolder.isEmpty()) {
                Toast.makeText(BankPaymentActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            String cardNumberDigits = cardNumber.replaceAll("\\s", "");
            if (!cardNumberDigits.matches("\\d{16}")) {
                Toast.makeText(BankPaymentActivity.this, "Số thẻ không hợp lệ, phải có đúng 16 chữ số", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!expiryDate.matches("(0[1-9]|1[0-2])/\\d{4}")) {
                Toast.makeText(this, "Ngày hết hạn không hợp lệ (MM/YYYY)", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!cardHolder.matches("[A-Z\\s]+")) {
                Toast.makeText(this, "Tên chủ thẻ không hợp lệ (chỉ chứa chữ cái in hoa và khoảng trắng)", Toast.LENGTH_SHORT).show();
                return;
            }

            if (checkBoxSaveInfo.isChecked()) {
                savePaymentInfo(cardNumber, expiryDate, cardHolder);
            } else {
                clearSavedPaymentInfo();
            }

            Toast.makeText(BankPaymentActivity.this, "Thanh toán thành công qua " + bankName, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(BankPaymentActivity.this, PaymentSuccessActivity.class);
            intent.putExtra("cinema_name", getIntent().getStringExtra("cinema_name"));
            intent.putExtra("movie_title", getIntent().getStringExtra("movie_title"));
            intent.putExtra("selected_date", getIntent().getStringExtra("selected_date"));
            intent.putExtra("selected_time", getIntent().getStringExtra("selected_time"));
            intent.putExtra("seats", getIntent().getStringExtra("seats"));
            ArrayList<SelectedCombo> selectedCombos = (ArrayList<SelectedCombo>) getIntent().getSerializableExtra("selected_combos");
            intent.putExtra("food", getSelectedCombosString(selectedCombos));
            intent.putExtra("total_price", getIntent().getStringExtra("total_price"));
            startActivity(intent);
            finish();
        });
    }

    private void savePaymentInfo(String cardNumber, String expiryDate, String cardHolder) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_CARD_NUMBER, cardNumber);
        editor.putString(KEY_EXPIRY_DATE, expiryDate);
        editor.putString(KEY_CARD_HOLDER, cardHolder);
        editor.putBoolean(KEY_SAVE_INFO, true);
        editor.apply();
    }

    private void loadSavedPaymentInfo() {
        boolean isSaved = sharedPreferences.getBoolean(KEY_SAVE_INFO, false);
        if (isSaved) {
            String cardNumber = sharedPreferences.getString(KEY_CARD_NUMBER, "");
            String expiryDate = sharedPreferences.getString(KEY_EXPIRY_DATE, "");
            String cardHolder = sharedPreferences.getString(KEY_CARD_HOLDER, "");

            editCardNumber.setText(cardNumber);
            editExpiryDate.setText(expiryDate);
            editCardHolder.setText(cardHolder);
            checkBoxSaveInfo.setChecked(true);
        }
    }

    private void clearSavedPaymentInfo() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
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
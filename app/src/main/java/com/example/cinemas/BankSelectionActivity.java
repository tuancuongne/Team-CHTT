package com.example.cinemas;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BankSelectionActivity extends AppCompatActivity {

    private GridView gridViewBanks;
    private EditText editSearchBank;
    private BankAdapter bankAdapter;
    private List<Bank> bankList;
    private List<Bank> filteredBankList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_selection);

        // Khởi tạo giao diện
        gridViewBanks = findViewById(R.id.grid_view_banks);
        editSearchBank = findViewById(R.id.edit_search_bank);
        ImageView imageBack = findViewById(R.id.image_back);

        // Xử lý nút quay lại
        imageBack.setOnClickListener(v -> finish());

        // Khởi tạo danh sách ngân hàng
        initBankList();

        // Thiết lập GridView
        filteredBankList = new ArrayList<>(bankList);
        bankAdapter = new BankAdapter(this, filteredBankList);
        gridViewBanks.setAdapter(bankAdapter);

        // Xử lý tìm kiếm ngân hàng
        editSearchBank.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBanks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Xử lý nhấn vào ngân hàng
        gridViewBanks.setOnItemClickListener((parent, view, position, id) -> {
            Bank selectedBank = filteredBankList.get(position);
            // Mở BankPaymentActivity và truyền thông tin ngân hàng
            Intent intent = new Intent(BankSelectionActivity.this, BankPaymentActivity.class);
            intent.putExtra("bank_name", selectedBank.getName());
            intent.putExtra("bank_logo", selectedBank.getLogo());
            startActivity(intent);
        });
    }

    // Khởi tạo danh sách ngân hàng
    private void initBankList() {
        bankList = new ArrayList<>();
        // Thêm các ngân hàng (dùng placeholder cho logo vì bạn cần thêm hình ảnh vào drawable)
        bankList.add(new Bank("Vietcombank", R.drawable.vietcombank));
        bankList.add(new Bank("BIDV Bank", R.drawable.bidv));
        bankList.add(new Bank("Agribank", R.drawable.agribank));
        bankList.add(new Bank("Vietinbank", R.drawable.vietinbank));
        bankList.add(new Bank("VPBank", R.drawable.vpbank));
        bankList.add(new Bank("Sacombank", R.drawable.sacombank));
        bankList.add(new Bank("TPBank", R.drawable.tpbank));
        bankList.add(new Bank("Techcombank", R.drawable.techcombank));
        bankList.add(new Bank("MB Bank", R.drawable.mbbank));
        bankList.add(new Bank("VIB Bank", R.drawable.vib));
        bankList.add(new Bank("Eximbank", R.drawable.eximbank));
        bankList.add(new Bank("SHB Bank", R.drawable.shb));
        bankList.add(new Bank("MSB Bank", R.drawable.msb));
        bankList.add(new Bank("HDBank", R.drawable.hdbank));
        bankList.add(new Bank("SeABank", R.drawable.seabank));
        bankList.add(new Bank("ABBank", R.drawable.abbank));
        bankList.add(new Bank("Bac A Bank", R.drawable.bacabank));
        bankList.add(new Bank("Nam A Bank", R.drawable.namabank));
        bankList.add(new Bank("NCB Bank", R.drawable.ncb));
        bankList.add(new Bank("MBV Bank", R.drawable.mbv));
        bankList.add(new Bank("PVcomBank", R.drawable.pvcom));
        bankList.add(new Bank("SCB Bank", R.drawable.scb));
        bankList.add(new Bank("BVB Bank", R.drawable.bvb));
        bankList.add(new Bank("VietABank", R.drawable.vietabank));
        bankList.add(new Bank("ACB Bank", R.drawable.acb));
        bankList.add(new Bank("OCB Bank", R.drawable.ocb));
        bankList.add(new Bank("LPBank", R.drawable.lpbank));
        bankList.add(new Bank("Kien Long Bank", R.drawable.kienlong));
        bankList.add(new Bank("PGBank", R.drawable.pgbank));
        bankList.add(new Bank("Woori Bank", R.drawable.woori));
        bankList.add(new Bank("Bao Viet Bank", R.drawable.baoviet));
        bankList.add(new Bank("GPBank", R.drawable.gp));
    }

    // Lọc danh sách ngân hàng theo từ khóa tìm kiếm
    private void filterBanks(String query) {
        filteredBankList.clear();
        if (query.isEmpty()) {
            filteredBankList.addAll(bankList);
        } else {
            for (Bank bank : bankList) {
                if (bank.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredBankList.add(bank);
                }
            }
        }
        bankAdapter.notifyDataSetChanged();
    }
}
package com.example.cinemas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity {

    private ImageView buttonBack;
    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        buttonBack = findViewById(R.id.button_back);
        recyclerView = findViewById(R.id.recycler_view_transactions); // Thêm RecyclerView vào XML

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("transactions");

        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactionList, transaction -> {
            // Khi nhấn vào giao dịch, chuyển sang PaymentHistoryActivity
            Intent intent = new Intent(TransactionHistoryActivity.this, PaymentHistoryActivity.class);
            intent.putExtra("BOOKING_CODE", transaction.getBookingCode());
            intent.putExtra("CINEMA_NAME", transaction.getCinemaName());
            intent.putExtra("MOVIE_INFO", transaction.getMovieTitle());
            intent.putExtra("SHOWTIME", transaction.getShowtime());
            intent.putExtra("SEATS", transaction.getSeats());
            intent.putExtra("FOOD", transaction.getFood());
            intent.putExtra("TOTAL_PRICE", transaction.getTotalPrice());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(transactionAdapter);

        // Lấy danh sách giao dịch từ Firebase
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "anonymous";
        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                transactionList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Transaction transaction = snapshot.getValue(Transaction.class);
                    transactionList.add(transaction);
                }
                transactionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });

        // Xử lý nút Back
        buttonBack.setOnClickListener(v -> finish());
    }
}
package com.example.cinemas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;
    private OnTransactionClickListener listener;

    public TransactionAdapter(List<Transaction> transactionList, OnTransactionClickListener listener) {
        this.transactionList = transactionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.textMovieTitle.setText(transaction.getMovieTitle());
        holder.textDatetime.setText(transaction.getShowtime());
        holder.textCinema.setText(transaction.getCinemaName());

        holder.itemView.setOnClickListener(v -> listener.onTransactionClick(transaction));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView textMovieTitle, textDatetime, textCinema;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            textMovieTitle = itemView.findViewById(R.id.text_movie_title);
            textDatetime = itemView.findViewById(R.id.text_datetime);
            textCinema = itemView.findViewById(R.id.text_cinema);
        }
    }

    interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }
}
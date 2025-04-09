package com.example.cinemas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

public class BankAdapter extends BaseAdapter {
    private Context context;
    private List<Bank> bankList;

    public BankAdapter(Context context, List<Bank> bankList) {
        this.context = context;
        this.bankList = bankList;
    }

    @Override
    public int getCount() {
        return bankList.size();
    }

    @Override
    public Object getItem(int position) {
        return bankList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_bank, parent, false);
        }

        ImageView imageBankLogo = convertView.findViewById(R.id.image_bank_logo);

        Bank bank = bankList.get(position);
        imageBankLogo.setImageResource(bank.getLogo());

        return convertView;
    }
}
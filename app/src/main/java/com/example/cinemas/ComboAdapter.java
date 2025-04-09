package com.example.cinemas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ComboAdapter extends RecyclerView.Adapter<ComboAdapter.ComboViewHolder> {

    private Context context;
    private ArrayList<SelectedCombo> comboList;

    public ComboAdapter(Context context, ArrayList<SelectedCombo> comboList) {
        this.context = context;
        this.comboList = comboList;
    }

    @NonNull
    @Override
    public ComboViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_combo, parent, false);
        return new ComboViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComboViewHolder holder, int position) {
        SelectedCombo combo = comboList.get(position);
        holder.imageCombo.setImageResource(combo.getImageResId());
        holder.textComboName.setText(combo.getName());
        holder.textComboQuantity.setText(String.valueOf(combo.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return comboList != null ? comboList.size() : 0;
    }

    static class ComboViewHolder extends RecyclerView.ViewHolder {
        ImageView imageCombo;
        TextView textComboName, textComboQuantity;

        public ComboViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCombo = itemView.findViewById(R.id.image_combo);
            textComboName = itemView.findViewById(R.id.text_combo_name);
            textComboQuantity = itemView.findViewById(R.id.text_combo_quantity);
        }
    }
}
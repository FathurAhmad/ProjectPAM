package com.example.projectpambaru;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.TransaksiViewHolder> {

    private List<TransaksiItem> transaksiList;

    public TransaksiAdapter(List<TransaksiItem> transaksiList) {
        this.transaksiList = transaksiList;
    }

    public static class TransaksiViewHolder extends RecyclerView.ViewHolder {
        TextView tvKategori, tvTransaksi, tvDeskripsi, tvJumlah;

        public TransaksiViewHolder(View itemView) {
            super(itemView);
            tvKategori = itemView.findViewById(R.id.tvKategori);
            tvTransaksi = itemView.findViewById(R.id.tvTransaksi);
            tvDeskripsi = itemView.findViewById(R.id.tvDeskripsi);
            tvJumlah = itemView.findViewById(R.id.tvJumlah);
        }
    }

    @NonNull
    @Override
    public TransaksiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_text, parent, false);
        return new TransaksiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransaksiViewHolder holder, int position) {
        TransaksiItem item = transaksiList.get(position);
        holder.tvKategori.setText(item.getKategori());
        holder.tvTransaksi.setText(item.getTransaksiKe());
        holder.tvDeskripsi.setText(item.getDeskripsi());
        holder.tvJumlah.setText(item.getJumlah());
        if (item.getKategori().equalsIgnoreCase("pengeluaran")) {
            holder.tvJumlah.setTextColor(Color.RED);
        } else {
            holder.tvJumlah.setTextColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return transaksiList.size();
    }
}

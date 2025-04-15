package com.example.projectpambaru;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterTransaksi extends RecyclerView.Adapter<AdapterTransaksi.HolderData> {
    List<Transaksi> listTransaksi;  // Ganti menjadi List<Transaksi>
    LayoutInflater inflater;

    public AdapterTransaksi(Context context, List<Transaksi> listTransaksi) {
        this.listTransaksi = listTransaksi;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_transaksi, parent, false);
        return new HolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        Transaksi transaksi = listTransaksi.get(position);  // Ambil objek transaksi
        holder.jenisTransaksi.setText(transaksi.getJenisTransaksi());
        holder.deskripsiTransaksi.setText(transaksi.getNama());
        holder.namaTransaksi.setText(transaksi.getDeskripsi());  // Kamu bisa mengganti dengan nama atau deskripsi
        holder.nominalTransaksi.setText("Rp " + transaksi.getNominal());
    }

    @Override
    public int getItemCount() {
        return listTransaksi.size();  // Pastikan ini mengembalikan jumlah data yang benar
    }


    public class HolderData extends RecyclerView.ViewHolder {
        TextView jenisTransaksi, deskripsiTransaksi, namaTransaksi, nominalTransaksi;

        public HolderData(@NonNull View itemView) {
            super(itemView);
            jenisTransaksi = itemView.findViewById(R.id.jenis_transaksi);
            namaTransaksi = itemView.findViewById(R.id.nama_transaksi);
            deskripsiTransaksi = itemView.findViewById(R.id.deskripsi_transaksi);
            nominalTransaksi = itemView.findViewById(R.id.nominal_transaksi);
        }
    }
}


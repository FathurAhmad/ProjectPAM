package com.example.projectpambaru;

import static com.example.projectpambaru.DashboardActivity.formatAngka;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.TransaksiViewHolder> {
    private String halaman;
    private List<Transaksi> transaksiList;
    private LayoutInflater inflater;

    public TransaksiAdapter(Context context, List<Transaksi> transaksiList) {
        this.halaman = context.getClass().getSimpleName();
        this.transaksiList = transaksiList;
        this.inflater = LayoutInflater.from(context);
    }

    public static class TransaksiViewHolder extends RecyclerView.ViewHolder {
        TextView tvKategori, tvTransaksi, tvDeskripsi, tvJumlah;
        ImageView imgTransaksi;

        public TransaksiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvKategori = itemView.findViewById(R.id.tvKategori);
            tvTransaksi = itemView.findViewById(R.id.tvTransaksi);
            tvDeskripsi = itemView.findViewById(R.id.tvDeskripsi);
            tvJumlah = itemView.findViewById(R.id.tvJumlah);
            imgTransaksi = itemView.findViewById(R.id.ivTransaksi);
        }

    }

    @NonNull
    @Override
    public TransaksiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (this.halaman.equals("DashboardActivity")) {
            View view = inflater.inflate(R.layout.item_dashboard, parent, false);
            return new TransaksiViewHolder(view);
        }
        View view = inflater.inflate(R.layout.item_transaksi, parent, false);
        return new TransaksiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransaksiViewHolder holder, int position) {
        Transaksi transaksi = transaksiList.get(position);
        int nominal = (int)transaksi.getNominal();
        String format = formatAngka(nominal);

        holder.tvKategori.setText(transaksi.getJenisTransaksi());
        holder.tvTransaksi.setText(transaksi.getNama());
        holder.tvDeskripsi.setText(transaksi.getDeskripsi());
        holder.tvJumlah.setText("Rp" + format);

        if (transaksi != null){
            Glide.with(holder.itemView.getContext())
                    .load(transaksi.getGambarUrl())
                    .into(holder.imgTransaksi);
        }

        if (halaman.equals("DashboardActivity")) {
            if (transaksi.getJenisTransaksi().equalsIgnoreCase("pengeluaran")) {
                holder.tvJumlah.setTextColor(Color.RED);
            } else {
                holder.tvJumlah.setTextColor(Color.GREEN);
            }
        }


    }

    @Override
    public int getItemCount() {
        return transaksiList.size();
    }
}

package com.example.projectpambaru;

import static com.example.projectpambaru.DashboardActivity.formatAngka;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.TransaksiViewHolder> {

    private List<Transaksi> transaksiList;
    private LayoutInflater inflater;

    public TransaksiAdapter(Context context, List<Transaksi> transaksiList) {
        this.transaksiList = transaksiList;
        this.inflater = LayoutInflater.from(context);
    }

    public static class TransaksiViewHolder extends RecyclerView.ViewHolder {
        TextView tvKategori, tvTransaksi, tvDeskripsi, tvJumlah;

        public TransaksiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvKategori = itemView.findViewById(R.id.tvKategori);       // ex: jenisTransaksi
            tvTransaksi = itemView.findViewById(R.id.tvTransaksi);     // ex: namaTransaksi
            tvDeskripsi = itemView.findViewById(R.id.tvDeskripsi);     // ex: deskripsiTransaksi
            tvJumlah = itemView.findViewById(R.id.tvJumlah);           // ex: nominalTransaksi
        }
    }

    @NonNull
    @Override
    public TransaksiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_text, parent, false);  // Ganti layout sesuai yang digunakan
        return new TransaksiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransaksiViewHolder holder, int position) {
        Transaksi transaksi = transaksiList.get(position);
        int nominal = (int)transaksi.getNominal();
        String format = formatAngka(nominal);

        holder.tvKategori.setText(transaksi.getJenisTransaksi());        // "Pemasukan" / "Pengeluaran"
        holder.tvTransaksi.setText(transaksi.getNama());                 // Nama transaksi
        holder.tvDeskripsi.setText(transaksi.getDeskripsi());            // Deskripsi
        holder.tvJumlah.setText("Rp" + format);         // Nominal

        // Pewarnaan nominal berdasarkan jenis
        if (transaksi.getJenisTransaksi().equalsIgnoreCase("pengeluaran")) {
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

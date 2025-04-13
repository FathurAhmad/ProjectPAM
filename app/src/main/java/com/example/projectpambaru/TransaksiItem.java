package com.example.projectpambaru;

public class TransaksiItem {
    private String kategori;
    private String transaksiKe;
    private String deskripsi;
    private String jumlah;

    public TransaksiItem(String kategori, String transaksiKe, String deskripsi, String jumlah) {
        this.kategori = kategori;
        this.transaksiKe = transaksiKe;
        this.deskripsi = deskripsi;
        this.jumlah = jumlah;
    }

    public String getKategori() {
        return kategori;
    }

    public String getTransaksiKe() {
        return transaksiKe;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getJumlah() {
        return jumlah;
    }
}

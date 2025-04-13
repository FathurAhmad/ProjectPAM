package com.example.projectpambaru;

public class Transaksi {
    private String jenisTransaksi;
    private String nama;
    private String deskripsi;
    private double nominal;

    public Transaksi(String jenisTransaksi, String nama, String deskripsi, double nominal) {
        this.jenisTransaksi = jenisTransaksi;
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.nominal = nominal;
    }

    public String getJenisTransaksi() {
        return jenisTransaksi;
    }

    public String getNama() {
        return nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public double getNominal() {
        return nominal;
    }
}

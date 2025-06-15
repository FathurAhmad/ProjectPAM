package com.example.projectpambaru;

import android.net.Uri;

import java.net.URI;

public class Transaksi {
    private String id;
    private String jenisTransaksi;
    private String nama;
    private String deskripsi;
    private double nominal;
    private String gambarUrl;

    public Transaksi() {

    }

    public Transaksi(String id, String jenisTransaksi, String nama, String deskripsi, double nominal, String gambarUrl) {
        this.id = id;
        this.jenisTransaksi = jenisTransaksi;
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.nominal = nominal;
        this.gambarUrl = gambarUrl;
    }

    public String getId() { return id; }
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
    public String getGambarUrl() { return gambarUrl; }
}

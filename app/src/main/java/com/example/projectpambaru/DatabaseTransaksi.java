package com.example.projectpambaru;

import java.util.ArrayList;
import java.util.List;

public class DatabaseTransaksi {

    private static List<Transaksi> transaksiList = new ArrayList<>();
    private static List<Transaksi> pemasukanList = new ArrayList<>();
    private static List<Transaksi> pengeluaranList = new ArrayList<>();

    public static void tambahTransaksi(Transaksi transaksi) {
        transaksiList.add(transaksi);
        if (transaksi.getJenisTransaksi().equalsIgnoreCase("pemasukan")) {
            pemasukanList.add(transaksi);
        } else if (transaksi.getJenisTransaksi().equalsIgnoreCase("pengeluaran")) {
            pengeluaranList.add(transaksi);
        }
    }

    public static int getNominal() {
        int total = 0;
        for (Transaksi t : transaksiList) {
            if (t.getJenisTransaksi().equalsIgnoreCase("Pemasukan")) {
                total += t.getNominal();
            } else if (t.getJenisTransaksi().equalsIgnoreCase("Pengeluaran")) {
                total -= t.getNominal();
            }
        }
        return total;
    }


    public static List<Transaksi> getTransaksiList() {
        return transaksiList;
    }

    public static List<Transaksi> getTransaksiPemasukan() {
        return pemasukanList;
    }

    public static List<Transaksi> getTransaksiPengeluaran() {
        return pengeluaranList;
    }
}

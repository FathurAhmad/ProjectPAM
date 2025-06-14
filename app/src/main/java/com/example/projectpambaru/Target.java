package com.example.projectpambaru;

import android.net.Uri;

public class Target {
    public String id;
    public String target;
    public int nominal;
    public String gambarUrl;
    public Target(){

    }

    public Target(String id, String target, int nominal, String gambarUrl){
        this.id = id;
        this.target = target;
        this.nominal = nominal;
        this.gambarUrl = gambarUrl;
    }
}

package com.example.projectpambaru;

import android.net.Uri;

public class Target {
    public String id;
    public String target;
    public int nominal;
    private Uri urlFile;
    public Target(){

    }

    public Target(String id, String target, int nominal, Uri urlFile){
        this.id = id;
        this.target = target;
        this.nominal = nominal;
        this.urlFile = urlFile;
    }
}

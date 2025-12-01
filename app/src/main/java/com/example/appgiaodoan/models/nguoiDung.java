package com.example.appgiaodoan.models;

public class nguoiDung {
    private String SDT;
    private double viDo;
    private double kinhDo;

    public nguoiDung(String SDT) {
        this.SDT = SDT;
        this.viDo = 0.0;
        this.kinhDo = 0.0;
    }

    public String getSDT() {
        return SDT;
    }

    public void setSDT(String SDT) {
        this.SDT = SDT;
    }

    public double getViDo() {
        return viDo;
    }

    public void setViDo(double viDo) {
        this.viDo = viDo;
    }

    public double getKinhDo() {
        return kinhDo;
    }

    public void setKinhDo(double kinhDo) {
        this.kinhDo = kinhDo;
    }
}
package com.example.appgiaodoan.models;

import java.io.Serializable;

public class monAn implements Serializable {
    private String idmonan;
    private String tenmonan;
    private String mota;
    private String hinhanh_url;
    private double giatien;

    public monAn(String idMonAn, String tenMonAn, String moTa, String hinhAnhURL, double gia) {
        this.idmonan = idMonAn;
        this.tenmonan = tenMonAn;
        this.mota = moTa;
        this.hinhanh_url = hinhAnhURL;
        this.giatien = gia;
    }

    public monAn() {}

    public String getIdMonAn() { return idmonan; }
    public void setIdMonAn(String idMonAn) { this.idmonan = idMonAn; }

    public String getTenMonAn() { return tenmonan; }
    public void setTenMonAn(String tenMonAn) { this.tenmonan = tenMonAn; }

    public String getMoTa() { return mota; }
    public void setMoTa(String moTa) { this.mota = moTa; }

    public String getHinhAnh() { return hinhanh_url; }
    public void setHinhAnh(String hinhAnh) { this.hinhanh_url = hinhAnh; }

    public double getGia() { return giatien; }
    public void setGia(double gia) { this.giatien = gia; }
}
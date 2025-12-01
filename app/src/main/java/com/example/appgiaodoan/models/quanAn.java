package com.example.appgiaodoan.models;

import java.io.Serializable;

public class quanAn implements Serializable {
    private String idnhahang;
    private String tennhahang;
    private String diachi;
    private String anhdaidien_url;
    private float rating;
    private String idchunhahang;
    private boolean trangthai;
    private double latitude;
    private double longitude;
    private String khoangcach;
    private String danhgia;

    public quanAn() {}


    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getAnhDaiDien_URL() {
        return anhdaidien_url;
    }
    public void setAnhDaiDien_URL(String url) {
        this.anhdaidien_url = url;
    }

    public String getIdNhaHang() { return idnhahang; }
    public void setIdNhaHang(String idNhaHang) { this.idnhahang = idNhaHang; }

    public String getTenNhaHang() { return tennhahang; }
    public void setTenNhaHang(String tenNhaHang) { this.tennhahang = tenNhaHang; }

    public String getDiaChi() { return diachi; }
    public void setDiaChi(String diaChi) { this.diachi = diaChi; }

    public String getIdChuNhaHang() { return idchunhahang; }
    public void setIdChuNhaHang(String idChuNhaHang) { this.idchunhahang = idChuNhaHang; }

    public boolean isTrangThai() { return trangthai; }
    public void setTrangThai(boolean trangThai) { this.trangthai = trangThai; }

    public String getKhoangCach() { return khoangcach; }
    public void setKhoangCach(String khoangCach) { this.khoangcach = khoangCach; }

    public String getDanhGia() { return danhgia; }
    public void setDanhGia(String danhGia) { this.danhgia = danhGia; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}
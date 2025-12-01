package com.example.appgiaodoan.models;

public class lichSuDonHang {
    private String idDonHang;
    private String tenQuan;
    private String hinhAnhQuan;
    private String thoiGianDat;
    private double tongTien;
    private String trangThai;

    public lichSuDonHang(String idDonHang, String tenQuan, String hinhAnhQuan, String thoiGianDat, double tongTien, String trangThai) {
        this.idDonHang = idDonHang;
        this.tenQuan = tenQuan;
        this.hinhAnhQuan = hinhAnhQuan;
        this.thoiGianDat = thoiGianDat;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
    }

    public String getIdDonHang() { return idDonHang; }
    public String getTenQuan() { return tenQuan; }
    public String getHinhAnhQuan() { return hinhAnhQuan; }
    public String getThoiGianDat() { return thoiGianDat; }
    public double getTongTien() { return tongTien; }
    public String getTrangThai() { return trangThai; }
}
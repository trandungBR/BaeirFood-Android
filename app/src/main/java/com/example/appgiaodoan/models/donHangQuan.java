package com.example.appgiaodoan.models;

public class donHangQuan {
    private String idDonHang;
    private String thoiGian;
    private double tongTien;
    private String trangThai;

    public donHangQuan(String idDonHang, String thoiGian, double tongTien, String trangThai) {
        this.idDonHang = idDonHang;
        this.thoiGian = thoiGian;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
    }

    public String getIdDonHang() { return idDonHang; }
    public String getThoiGian() { return thoiGian; }
    public double getTongTien() { return tongTien; }
    public String getTrangThai() { return trangThai; }
}
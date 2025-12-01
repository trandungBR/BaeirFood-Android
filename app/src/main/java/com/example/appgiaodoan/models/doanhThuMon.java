package com.example.appgiaodoan.models;

public class doanhThuMon {
    private String tenMon;
    private int soLuongBan;
    private double tongTien;

    public doanhThuMon(String tenMon, int soLuongBan, double tongTien) {
        this.tenMon = tenMon;
        this.soLuongBan = soLuongBan;
        this.tongTien = tongTien;
    }

    public String getTenMon() { return tenMon; }
    public int getSoLuongBan() { return soLuongBan; }
    public double getTongTien() { return tongTien; }
    public void add(int soLuong, double tien) {
        this.soLuongBan += soLuong;
        this.tongTien += tien;
    }
}
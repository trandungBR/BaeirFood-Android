package com.example.appgiaodoan.models;

public class thuNhapTaiXe {
    private String idDonHang;
    private String tenQuan;
    private String thoiGian;
    private double phiShip;

    public thuNhapTaiXe(String idDonHang, String tenQuan, String thoiGian, double phiShip) {
        this.idDonHang = idDonHang;
        this.tenQuan = tenQuan;
        this.thoiGian = thoiGian;
        this.phiShip = phiShip;
    }

    public String getIdDonHang() { return idDonHang; }
    public String getTenQuan() { return tenQuan; }
    public String getThoiGian() { return thoiGian; }
    public double getPhiShip() { return phiShip; }

    public double getTienLoi() {
        return phiShip * 0.4;
    }
}
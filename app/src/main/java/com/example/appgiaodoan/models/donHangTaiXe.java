package com.example.appgiaodoan.models;

public class donHangTaiXe {
    private String idDonHang;
    private String tenQuan;
    private String hinhAnhQuan;
    private String diaChiQuan;
    private String diaChiKhach;
    private int soLuongMon;
    private double tongTienDon;
    private double phiGiaoHang;
    private double khoangCachKm;

    public donHangTaiXe(String idDonHang, String tenQuan, String hinhAnhQuan, String diaChiQuan, String diaChiKhach, int soLuongMon, double tongTienDon, double phiGiaoHang) {
        this.idDonHang = idDonHang;
        this.tenQuan = tenQuan;
        this.hinhAnhQuan = hinhAnhQuan;
        this.diaChiQuan = diaChiQuan;
        this.diaChiKhach = diaChiKhach;
        this.soLuongMon = soLuongMon;
        this.tongTienDon = tongTienDon;
        this.phiGiaoHang = phiGiaoHang;
        this.khoangCachKm = 0;
    }

    // Getter & Setter
    public String getIdDonHang() { return idDonHang; }
    public String getTenQuan() { return tenQuan; }
    public String getHinhAnhQuan() { return hinhAnhQuan; }
    public String getDiaChiQuan() { return diaChiQuan; }
    public String getDiaChiKhach() { return diaChiKhach; }
    public int getSoLuongMon() { return soLuongMon; }
    public double getTongTienDon() { return tongTienDon; }
    public double getPhiGiaoHang() { return phiGiaoHang; }

    public double getKhoangCachKm() { return khoangCachKm; }
    public void setKhoangCachKm(double km) { this.khoangCachKm = km; }

    public double getTienLoi() {
        return phiGiaoHang * 0.40;
    }
}
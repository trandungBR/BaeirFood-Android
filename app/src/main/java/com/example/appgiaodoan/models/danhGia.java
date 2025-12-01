package com.example.appgiaodoan.models;

public class danhGia {
    private String idDanhGia;
    private String tenNguoiDung;
    private int diem;
    private String noiDung;
    private String thoiGian;

    public danhGia(String idDanhGia, String tenNguoiDung, int diem, String noiDung, String thoiGian) {
        this.idDanhGia = idDanhGia;
        this.tenNguoiDung = tenNguoiDung;
        this.diem = diem;
        this.noiDung = noiDung;
        this.thoiGian = thoiGian;
    }

    // Getter
    public String getIdDanhGia() { return idDanhGia; }
    public String getTenNguoiDung() { return tenNguoiDung; }
    public int getDiem() { return diem; }
    public String getNoiDung() { return noiDung; }
    public String getThoiGian() { return thoiGian; }
}
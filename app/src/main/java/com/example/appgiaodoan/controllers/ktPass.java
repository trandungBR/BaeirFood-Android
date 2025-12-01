package com.example.appgiaodoan.controllers;

import java.util.regex.Pattern;

public class ktPass {
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         // ít nhất 1 số
                    "(?=.*[A-Z])" +         // ít nhất 1 chữ hoa
                    "(?=.*[@#$%^&+=!.])" +   // ít nhất 1 ký tự đặc biệt
                    "(?=\\S+$)" +           // không có khoảng trắng
                    ".{6,}" +               // tối thiểu 6 ký tự
                    "$");

    public String ktDoPhucTap(String matKhau) {
        if (matKhau.isEmpty()) {
            return "Vui lòng nhập mật khẩu!";
        }

        if (matKhau.length() < 6) {
            return "Mật khẩu phải có tối thiểu 6 ký tự!";
        }

        if (!Pattern.compile(".*[A-Z].*").matcher(matKhau).matches()) {
            return "Mật khẩu phải chứa ít nhất 1 chữ viết hoa!";
        }

        if (!Pattern.compile(".*[@#$%^&+=!.].*").matcher(matKhau).matches()) {
            return "Mật khẩu phải chứa ít nhất 1 ký tự đặc biệt!";
        }

        if (!Pattern.compile(".*[0-9].*").matcher(matKhau).matches()) {
            return "Mật khẩu phải chứa ít nhất 1 chữ số!";
        }

        if (Pattern.compile(".*\\s.*").matcher(matKhau).matches()) {
            return "Mật khẩu không được chứa khoảng trắng!";
        }

        return null;
    }
}
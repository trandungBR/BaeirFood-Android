package com.example.appgiaodoan.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.app.AlertDialog;
import com.bumptech.glide.Glide;
import com.example.appgiaodoan.R;
import com.example.appgiaodoan.controllers.mainControllers;
import com.example.appgiaodoan.models.quanAn;

public class trangchuquanan extends AppCompatActivity implements mainControllers.QuanAnDashboardListener {

    private ImageView ivAnhBia, ivAvatar;
    private TextView tvTenQuan, tvDiaChi, tvRating;
    private SwitchCompat switchMoCua;
    private ImageView btnBack;
    private CardView btnQuanLyDon, btnDoanhThu, btnQuanLyMon, btnDanhGia;
    private TextView tvDonMoi, tvDoanhThu;

    private mainControllers mController;
    private String currentUserId;
    private String currentNhaHangId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trangchuquanan);

        mController = new mainControllers();
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getString("USER_ID", null);
        btnBack = findViewById(R.id.btnBack);
        ivAnhBia = findViewById(R.id.ivAnhBiaQuan);
        ivAvatar = findViewById(R.id.ivAvatarQuan);
        tvTenQuan = findViewById(R.id.tvTenQuanOwner);
        tvDiaChi = findViewById(R.id.tvDiaChiOwner);
        tvRating = findViewById(R.id.tvRatingOwner);
        switchMoCua = findViewById(R.id.switchMoCua);

        tvDonMoi = findViewById(R.id.tvDonMoi);
        tvDoanhThu = findViewById(R.id.tvDoanhThu);

        btnQuanLyDon = findViewById(R.id.btnQuanLyDon);
        btnDoanhThu = findViewById(R.id.btnDoanhThu);
        btnQuanLyMon = findViewById(R.id.btnQuanLyMon);
        btnDanhGia = findViewById(R.id.btnDanhGiaOwner);

        if (currentUserId != null) {
            mController.taiDuLieuQuanAn(currentUserId, this);
        }
        btnQuanLyDon.setOnClickListener(v -> {
            if (currentNhaHangId == null) return;
            Intent intent = new Intent(this, quanlydon.class);
            intent.putExtra("ID_NHA_HANG", currentNhaHangId);
            startActivity(intent);
        });
        btnDoanhThu.setOnClickListener(v -> {
            if (currentNhaHangId == null) return;
            Intent intent = new Intent(this, quanlydoanhthu.class);
            intent.putExtra("ID_NHA_HANG", currentNhaHangId);
            startActivity(intent);
        });
        btnQuanLyMon.setOnClickListener(v -> {
            if (currentNhaHangId == null) return;
            Intent intent = new Intent(this, quanlymonan.class);
            intent.putExtra("ID_NHA_HANG", currentNhaHangId);
            startActivity(intent);
        });
        btnDanhGia.setOnClickListener(v -> {
            if (currentNhaHangId == null) return;
            Intent intent = new Intent(this, quanlydanhgia.class);
            intent.putExtra("ID_NHA_HANG", currentNhaHangId);
            startActivity(intent);
        });
        switchMoCua.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchMoCua.setText(isChecked ? "Đang mở cửa" : "Đang đóng cửa");

            if (currentNhaHangId != null) {
                mController.capNhatTrangThaiCuaHang(currentNhaHangId, isChecked, this);
            } else {
                Toast.makeText(this, "Chưa tải được thông tin quán", Toast.LENGTH_SHORT).show();
                switchMoCua.setChecked(!isChecked);
            }
        });
        btnBack.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có muốn đăng xuất khỏi tài khoản?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).edit();
                        editor.clear();
                        editor.apply();
                        Intent intent = new Intent(trangchuquanan.this, dangnhap.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }



    @Override
    public void onInfoLoaded(quanAn quanInfo) {
        this.currentNhaHangId = quanInfo.getIdNhaHang();

        tvTenQuan.setText(quanInfo.getTenNhaHang());
        tvDiaChi.setText(quanInfo.getDiaChi());
        tvRating.setText("⭐ " + quanInfo.getRating());

        switchMoCua.setChecked(quanInfo.isTrangThai());
        switchMoCua.setText(quanInfo.isTrangThai() ? "Đang mở cửa" : "Đang đóng cửa");

        if (quanInfo.getAnhDaiDien_URL() != null && !quanInfo.getAnhDaiDien_URL().isEmpty()) {
            Glide.with(this).load(quanInfo.getAnhDaiDien_URL()).into(ivAnhBia);
            Glide.with(this).load(quanInfo.getAnhDaiDien_URL()).into(ivAvatar);
        }
    }

    @Override
    public void onOrdersLoaded(java.util.List<com.example.appgiaodoan.models.donHangQuan> list) {
        int countNew = 0;
        double revenue = 0;
        for (com.example.appgiaodoan.models.donHangQuan d : list) {
            if ("Đang xử lý".equals(d.getTrangThai())) countNew++;
            if ("Hoàn thành".equals(d.getTrangThai())) revenue += d.getTongTien();
        }

        tvDonMoi.setText(String.valueOf(countNew));
        tvDoanhThu.setText(String.format(java.util.Locale.getDefault(), "%,.0fđ", revenue));
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusUpdated(String message) { }
}
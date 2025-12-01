package com.example.appgiaodoan.views;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.appgiaodoan.R;
import com.example.appgiaodoan.controllers.mainControllers;
import com.example.appgiaodoan.models.donHangQuan;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class chitietgiaohang extends AppCompatActivity implements mainControllers.DriverDetailListener, mainControllers.DriverHomeListener {

    private TextView tvTenQuan, tvDiaChiQuan, tvTenKhach, tvDiaChiKhach, tvSdtKhach, tvThuNhap, tvChiTietMon;
    private Button btnHanhDong;

    private mainControllers mController;
    private String idDonHang;
    private String currentStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chi_tiet_giao_hang);

        idDonHang = getIntent().getStringExtra("ID_DON_HANG");
        mController = new mainControllers();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvTenQuan = findViewById(R.id.tvTenQuan);
        tvDiaChiQuan = findViewById(R.id.tvDiaChiQuan);
        tvTenKhach = findViewById(R.id.tvTenKhach);
        tvDiaChiKhach = findViewById(R.id.tvDiaChiKhach);
        tvSdtKhach = findViewById(R.id.tvSdtKhach);
        tvThuNhap = findViewById(R.id.tvThuNhap);
        tvChiTietMon = findViewById(R.id.tvChiTietMon);
        btnHanhDong = findViewById(R.id.btnHanhDong);
        tvSdtKhach.setOnClickListener(v -> {
            String phone = tvSdtKhach.getText().toString();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            startActivity(intent);
        });
        btnHanhDong.setOnClickListener(v -> xuLyTrangThai());
        if (idDonHang != null) {
            mController.layChiTietDonTaiXe(idDonHang, this);
        }
    }
    @Override
    public void onDetailLoaded(String tenQuan, String dcQuan, String tenKhach, String dcKhach, String sdtKhach, String dsMon, double thuNhap, String trangThai) {
        tvTenQuan.setText(tenQuan);
        tvDiaChiQuan.setText(dcQuan);
        tvTenKhach.setText(tenKhach);
        tvDiaChiKhach.setText(dcKhach);
        tvSdtKhach.setText(sdtKhach);
        tvChiTietMon.setText(dsMon);

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvThuNhap.setText("+" + nf.format(thuNhap));

        this.currentStatus = trangThai;
        updateButtonState();
    }

    private void updateButtonState() {
        if ("Đang giao".equals(currentStatus)) {
            btnHanhDong.setText("XÁC NHẬN HOÀN THÀNH");
            btnHanhDong.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else if ("Hoàn thành".equals(currentStatus)) {
            btnHanhDong.setText("ĐƠN ĐÃ HOÀN THÀNH");
            btnHanhDong.setEnabled(false);
            btnHanhDong.setBackgroundColor(Color.GRAY);
        } else {
            btnHanhDong.setText("TRẠNG THÁI: " + currentStatus);
        }
    }

    private void xuLyTrangThai() {
        if ("Đang giao".equals(currentStatus)) {
            mController.capNhatTrangThaiDonQuan(idDonHang, "Hoàn thành", new mainControllers.QuanLyDonListener() {
                @Override public void onStatusUpdated(String message) {
                    Toast.makeText(chitietgiaohang.this, "Giao hàng thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(chitietgiaohang.this, trangchutaixe.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }
                @Override public void onError(String msg) { Toast.makeText(chitietgiaohang.this, msg, Toast.LENGTH_SHORT).show(); }
                @Override public void onListLoaded(List<donHangQuan> list) {}
            });
        }
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
    }
    @Override public void onOrdersLoaded(List<com.example.appgiaodoan.models.donHangTaiXe> list) {}
    @Override public void onOrderAccepted(String message) {}
}
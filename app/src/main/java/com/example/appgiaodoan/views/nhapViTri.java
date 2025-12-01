package com.example.appgiaodoan.views;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appgiaodoan.R;
import com.example.appgiaodoan.controllers.mainControllers;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class nhapViTri extends AppCompatActivity implements mainControllers.AuthViewListenerSimple {

    private EditText edtDiaChi;
    private Button btnXacNhan;
    private mainControllers mMainController;
    private String currentUserId;
    private String diaChiDaNhap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nhapvitri);

        edtDiaChi = findViewById(R.id.edtDiaChi);
        btnXacNhan = findViewById(R.id.btnXacNhan);

        mMainController = new mainControllers();

        currentUserId = getIntent().getStringExtra("USER_ID");
        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID người dùng.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnXacNhan.setOnClickListener(v -> {
            diaChiDaNhap = edtDiaChi.getText().toString().trim();

            mMainController.xuLyCapNhatDiaChi(currentUserId, diaChiDaNhap, this);
        });
    }

    private void chuyenDiaChiThanhToaDo(String diaChi) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> danhSachDiaChi = geocoder.getFromLocationName(diaChi, 1);

            if (danhSachDiaChi != null && !danhSachDiaChi.isEmpty()) {
                Address diaChiTimDuoc = danhSachDiaChi.get(0);
                double viDo = diaChiTimDuoc.getLatitude();
                double kinhDo = diaChiTimDuoc.getLongitude();
                String diaChiDayDu = diaChiTimDuoc.getAddressLine(0);

                Intent duLieuTraVe = new Intent();
                duLieuTraVe.putExtra("VI_DO_TRA_VE", viDo);
                duLieuTraVe.putExtra("KINH_DO_TRA_VE", kinhDo);
                duLieuTraVe.putExtra("DIA_CHI_TRA_VE", (diaChiDayDu != null) ? diaChiDayDu : diaChi);
                setResult(Activity.RESULT_OK, duLieuTraVe);

                finish();

            } else {
                showError("Không tìm thấy địa chỉ này. Vui lòng thử lại.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Lỗi mạng hoặc dịch vụ Geocoder không khả dụng.");
        }
    }

    private void runOnUIThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    @Override
    public void showLoading(boolean isLoading) {
        runOnUIThread(() -> {
            btnXacNhan.setEnabled(!isLoading);
            btnXacNhan.setText(isLoading ? "Đang xử lý..." : "Xác nhận");
        });
    }

    @Override
    public void showError(String message) {
        runOnUIThread(() ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        );
    }

    @Override
    public void showSuccess(String message) {
        runOnUIThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            chuyenDiaChiThanhToaDo(diaChiDaNhap);
        });
    }

    @Override
    public void dieuHuong(Class<?> activityClass, String... extras) {

    }
}
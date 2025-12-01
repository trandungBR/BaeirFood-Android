package com.example.appgiaodoan.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appgiaodoan.R;
import com.example.appgiaodoan.controllers.mainControllers;

public class nhapmatkhau extends AppCompatActivity implements mainControllers.AuthViewListenerSimple {

    private EditText etNhapMatKhau;
    private EditText etXacNhanMatKhau;
    private Button bXacNhan;
    private mainControllers mMainController;
    private String sdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nhapmatkhau);

        etNhapMatKhau = findViewById(R.id.etnhapmatkhau);
        etXacNhanMatKhau = findViewById(R.id.etxacnhanmatkhau);
        bXacNhan = findViewById(R.id.bXacNhan);
        // mProgressBar = findViewById(R.id.progressBar);

        mMainController = new mainControllers();

        sdt = getIntent().getStringExtra("phone_number");
        if (sdt == null || sdt.isEmpty()) {
            showError("Lỗi: Không tìm thấy SĐT.");
            finish();
            return;
        }

        bXacNhan.setOnClickListener(v -> {
            String matKhau = etNhapMatKhau.getText().toString();
            String xacNhan = etXacNhanMatKhau.getText().toString();

            mMainController.dangKiTaiKhoanMoi(sdt, matKhau, xacNhan, this);
        });
    }


    @Override
    public void showLoading(boolean isLoading) {
        new Handler(Looper.getMainLooper()).post(() -> {

            bXacNhan.setEnabled(!isLoading);
            bXacNhan.setText(isLoading ? "Đang xử lý..." : "Xác nhận");
        });
    }

    @Override
    public void showError(String message) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        );
    }

    @Override
    public void showSuccess(String message) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(this, message + ". Vui lòng đăng nhập.", Toast.LENGTH_LONG).show();
            // Đăng ký thành công, quay về trang Đăng nhập
            Intent intent = new Intent(nhapmatkhau.this, dangnhap.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    public void dieuHuong(Class<?> activityClass, String... extras) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Intent intent = new Intent(this, activityClass);
            if (extras.length > 0 && extras.length % 2 == 0) {
                for (int i = 0; i < extras.length; i += 2) {
                    intent.putExtra(extras[i], extras[i + 1]);
                }
            }
            // Nếu là quay về Đăng nhập, xóa stack
            if (activityClass == dangnhap.class) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            startActivity(intent);
        });
    }
}
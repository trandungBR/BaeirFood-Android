package com.example.appgiaodoan.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appgiaodoan.R;
import com.example.appgiaodoan.controllers.mainControllers;

public class dangnhap extends AppCompatActivity implements mainControllers.AuthViewListenerLogin, mainControllers.AuthViewListenerSimple {

    private EditText etSDT, etPass;
    private Button bDangNhap;
    private TextView tvDangKi, tvOTP;
    private ImageView ivPass;
    private boolean passVisable = false;
    private boolean isOTPMode = false;
    private mainControllers mMainController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dangnhap);

        etSDT = findViewById(R.id.etSDT);
        etPass = findViewById(R.id.etPass);
        bDangNhap = findViewById(R.id.bDangNhap);
        tvDangKi = findViewById(R.id.tvDangKi);
        tvOTP = findViewById(R.id.tvOTP);
        ivPass = findViewById(R.id.ivPass);

        mMainController = new mainControllers();

        bDangNhap.setOnClickListener(v -> {
            String sdt = etSDT.getText().toString().trim();
            String pass = etPass.getText().toString().trim();
            if (isOTPMode) {
                mMainController.guiOTP(sdt, this, com.example.appgiaodoan.views.xacthucotp.class, mainControllers.OTP.DANG_NHAP);
            } else {
                mMainController.dangNhap(sdt, pass, this);
            }
        });

        tvDangKi.setOnClickListener(v -> mMainController.chuyenTrangDangKi(this));

        tvOTP.setOnClickListener(v -> {
            isOTPMode = !isOTPMode;
            etPass.setVisibility(isOTPMode ? View.GONE : View.VISIBLE);
            tvOTP.setText(isOTPMode ? "Đăng nhập bằng mật khẩu" : "Đăng nhập bằng OTP");
        });

        ivPass.setOnClickListener(v -> {
            passVisable = !passVisable;
            etPass.setInputType(passVisable ?
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPass.setSelection(etPass.getText().length());
        });
    }

    @Override
    public void showLoading(boolean isLoading) {
        bDangNhap.setEnabled(!isLoading);
        bDangNhap.setText(isLoading ? "Đang xử lý..." : "Đăng nhập");
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSuccess(String message, String accessToken, String userId,String vaiTro) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        getSharedPreferences("AppPrefs", MODE_PRIVATE).edit()
                .putString("USER_TOKEN", accessToken)
                .putString("USER_ID", userId)
                .putString("USER_ROLE", vaiTro)
                .apply();
        Intent intent;
        if ("quanan".equalsIgnoreCase(vaiTro)) {
            intent = new Intent(this, trangchuquanan.class);
        } else if ("taixe".equalsIgnoreCase(vaiTro)) {
            intent = new Intent(this, trangchutaixe.class);
        } else {
            intent = new Intent(this, trangchu.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void dieuHuong(Class<?> activityClass, String... extras) {
        Intent intent = new Intent(this, activityClass);
        if (extras.length > 0 && extras.length % 2 == 0) {
            for (int i = 0; i < extras.length; i += 2) {
                intent.putExtra(extras[i], extras[i + 1]);
            }
        }
        if (activityClass == com.example.appgiaodoan.views.trangchu.class ||
                activityClass == com.example.appgiaodoan.views.dangnhap.class) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        startActivity(intent);
    }
}

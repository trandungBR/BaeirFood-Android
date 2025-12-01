package com.example.appgiaodoan.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appgiaodoan.R;
import com.example.appgiaodoan.controllers.mainControllers;

public class dangki extends AppCompatActivity implements mainControllers.AuthViewListenerSimple {

    private EditText etSDT;
    private Button bGuiOTP;
    private TextView tvDangNhap;
    private mainControllers mMainController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dangki);

        etSDT = findViewById(R.id.etSDT);
        bGuiOTP = findViewById(R.id.bDangKi);

        mMainController = new mainControllers();

        bGuiOTP.setOnClickListener(v -> {
            String sdt = etSDT.getText().toString().trim();
            mMainController.guiOTP(sdt, dangki.this, xacthucotp.class, mainControllers.OTP.DANG_KI);
        });

        tvDangNhap.setOnClickListener(v -> {
            dieuHuong(dangnhap.class);
        });
    }

    @Override
    public void showLoading(boolean isLoading) {
        bGuiOTP.setEnabled(!isLoading);
        bGuiOTP.setText(isLoading ? "Đang gửi OTP..." : "Gửi OTP");
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
    public void dieuHuong(Class<?> activityClass, String... extras) {
        android.content.Intent intent = new android.content.Intent(this, activityClass);
        if (extras.length > 0 && extras.length % 2 == 0) {
            for (int i = 0; i < extras.length; i += 2) {
                intent.putExtra(extras[i], extras[i + 1]);
            }
        }
        startActivity(intent);
    }
}

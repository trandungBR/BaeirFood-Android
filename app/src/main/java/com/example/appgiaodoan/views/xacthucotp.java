package com.example.appgiaodoan.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appgiaodoan.R;
import com.example.appgiaodoan.controllers.mainControllers;

public class xacthucotp extends AppCompatActivity implements mainControllers.AuthViewListenerSimple {

    private EditText etOTP;
    private Button bXacNhan;
    private mainControllers mMainController;
    private mainControllers.OTP purpose;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp);

        etOTP = findViewById(R.id.etSDT);
        bXacNhan = findViewById(R.id.bXacNhan);
        mMainController = new mainControllers();

        phoneNumber = getIntent().getStringExtra("phone_number");
        String purposeStr = getIntent().getStringExtra("purpose");
        if (purposeStr == null) {
            purposeStr = mainControllers.OTP.DANG_NHAP.name();
        }
        purpose = mainControllers.OTP.valueOf(purposeStr);

        bXacNhan.setOnClickListener(v -> {
            String otp = etOTP.getText().toString().trim();
            mMainController.xacThucOTP(phoneNumber, otp, xacthucotp.this, purpose);
        });
    }

    @Override
    public void showLoading(boolean isLoading) {
        bXacNhan.setEnabled(!isLoading);
        bXacNhan.setText(isLoading ? "Đang xác thực..." : "Xác nhận OTP");
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
        finish();
    }
}

package com.example.appgiaodoan.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.appgiaodoan.R;

public class loadgiaohang extends AppCompatActivity {

    private ProgressBar progressBar;
    private ImageView ivSuccess;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadgiaohang);

        progressBar = findViewById(R.id.progressBar);
        ivSuccess = findViewById(R.id.ivSuccess);
        tvStatus = findViewById(R.id.tvStatus);

        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            ivSuccess.setVisibility(View.VISIBLE);
            tvStatus.setText("Tài xế đã nhận đơn!");

            new Handler().postDelayed(() -> {
                Intent intent = new Intent(loadgiaohang.this, trangchu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }, 1000);

        }, 1000);
    }
}
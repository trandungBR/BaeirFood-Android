package com.example.appgiaodoan.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appgiaodoan.R;

public class chitietthanhtoan extends AppCompatActivity {

    private ImageView btnClose;
    private RadioGroup radioGroupPayment;

    private String selectedMethodId = "CASH";
    private String selectedMethodName = "Tiền mặt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chitietthanhtoan);

        radioGroupPayment = findViewById(R.id.radioGroupPayment);
        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> sendResultBack());

        radioGroupPayment.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = findViewById(checkedId);

            // Cập nhật giá trị
            selectedMethodName = rb.getText().toString();

            if (checkedId == R.id.rbCash) {
                selectedMethodId = "CASH";
                selectedMethodName = "Tiền mặt";
            } else if (checkedId == R.id.rbCard) {
                selectedMethodId = "CARD";
            } else if (checkedId == R.id.rbMomo) {
                selectedMethodId = "MOMO";
            }
        });

    }

    private void sendResultBack() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("PAYMENT_METHOD_ID", selectedMethodId);
        resultIntent.putExtra("PAYMENT_METHOD_NAME", selectedMethodName);

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
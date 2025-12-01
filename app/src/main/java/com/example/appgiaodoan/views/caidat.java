package com.example.appgiaodoan.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.appgiaodoan.R;
import com.example.appgiaodoan.controllers.mainControllers;
import com.google.android.material.materialswitch.MaterialSwitch;

public class caidat extends AppCompatActivity implements
        mainControllers.SettingsUpdateListener,
        mainControllers.ProfileViewListener,
        mainControllers.UpdateListener
{
    @Override
    public void onSettingsUpdated() {
        Toast.makeText(this, "Cài đặt đã được cập nhật", Toast.LENGTH_SHORT).show();
    }
    private mainControllers mMainController;
    private SharedPreferences sharedPreferences;
    private String currentUserEmail;
    private String currentUserId;

    private TextView tvDoiMatKhau, tvDoiEmail;
    private MaterialSwitch switchThongBao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.caidat);

        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentUserEmail = sharedPreferences.getString("USER_EMAIL", null);
        currentUserId = sharedPreferences.getString("USER_ID", null);

        if (currentUserId == null) {
            Toast.makeText(this, "Lỗi phiên đăng nhập.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mMainController = new mainControllers();

        Toolbar toolbar = findViewById(R.id.tBCaiDat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvDoiMatKhau = findViewById(R.id.tvDoiMatKhau);
        tvDoiEmail = findViewById(R.id.tvDoiEmail);
        switchThongBao = findViewById(R.id.switchThongBao);

        tvDoiMatKhau.setOnClickListener(v -> hienThiDialogDoiMatKhau());
        tvDoiEmail.setOnClickListener(v -> hienThiDialogDoiEmail());

        switchThongBao.setOnCheckedChangeListener((buttonView, isChecked) ->
                mMainController.xuLyDoiThongBao(currentUserId, isChecked, this));

        mMainController.layThongTinNguoiDung(currentUserId, this);

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(caidat.this, trangchu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void hienThiDialogDoiMatKhau() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đổi mật khẩu mới");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        EditText etMatKhauMoi = new EditText(this);
        etMatKhauMoi.setHint("Mật khẩu mới");
        etMatKhauMoi.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        EditText etXacNhanMK = new EditText(this);
        etXacNhanMK.setHint("Xác nhận mật khẩu");
        etXacNhanMK.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        layout.addView(etMatKhauMoi);
        layout.addView(etXacNhanMK);

        builder.setView(layout);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String matKhauMoi = etMatKhauMoi.getText().toString();
            String xacNhan = etXacNhanMK.getText().toString();
            mMainController.xuLyDoiMatKhau(currentUserId, matKhauMoi, xacNhan, this);
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void hienThiDialogDoiEmail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đổi Email");

        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint("Nhập email mới");
        input.setText(currentUserEmail != null ? currentUserEmail : "");

        LinearLayout layout = new LinearLayout(this);
        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);
        layout.addView(input);

        builder.setView(layout);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String emailMoi = input.getText().toString().trim();
            mMainController.xuLyDoiEmail(currentUserId, emailMoi, this);
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public void onProfileLoaded(String tenNguoiDung, String diaChi, boolean nhanThongBao) {
        switchThongBao.setChecked(nhanThongBao);
    }

    @Override
    public void onProfileLoadError(String message) {
        Toast.makeText(this, "Lỗi tải cài đặt: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateError(String message) {
        Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onEmailUpdateSuccess(String message, String newEmail) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        currentUserEmail = newEmail;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_EMAIL", newEmail);
        editor.apply();
    }
}
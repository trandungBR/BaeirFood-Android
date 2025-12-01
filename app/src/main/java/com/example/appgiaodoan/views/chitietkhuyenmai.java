package com.example.appgiaodoan.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgiaodoan.R;
import com.example.appgiaodoan.adapters.khuyenMaiAdapter;
import com.example.appgiaodoan.controllers.mainControllers;
import com.example.appgiaodoan.models.khuyenMai;

import java.util.ArrayList;
import java.util.List;

public class chitietkhuyenmai extends AppCompatActivity implements
        mainControllers.KhuyenMaiViewListener,
        khuyenMaiAdapter.OnKhuyenMaiSelectedListener {

    private RecyclerView rvKhuyenMai;
    private TextView tvError;
    private ImageView btnClose;

    private khuyenMaiAdapter adapterKhuyenMai;
    private final List<khuyenMai> danhSachKhuyenMai = new ArrayList<>();
    private mainControllers mMainController;

    private String idNhaHang;
    private double tongTienHang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chitietkhuyenmai);

        mMainController = new mainControllers();

        rvKhuyenMai = findViewById(R.id.rvKhuyenMai);
        tvError = findViewById(R.id.tvError);
        btnClose = findViewById(R.id.btnClose);
        idNhaHang = getIntent().getStringExtra("ID_NHA_HANG");
        tongTienHang = getIntent().getDoubleExtra("TONG_TIEN_HANG", 0);

        btnClose.setOnClickListener(v -> finish());

        adapterKhuyenMai = new khuyenMaiAdapter(this, danhSachKhuyenMai, this);
        rvKhuyenMai.setLayoutManager(new LinearLayoutManager(this));
        rvKhuyenMai.setAdapter(adapterKhuyenMai);

        // Tải danh sách khuyến mãi
        if (idNhaHang != null && !idNhaHang.isEmpty()) {
            mMainController.layDanhSachKhuyenMai(idNhaHang, this);
        } else {
            onKhuyenMaiLoadError("Không tìm thấy ID nhà hàng.");
        }
    }

    @Override
    public void onKhuyenMaiLoaded(List<khuyenMai> danhSach) {
        danhSachKhuyenMai.clear();
        danhSachKhuyenMai.addAll(danhSach);
        adapterKhuyenMai.notifyDataSetChanged();

        if (danhSach.isEmpty()) {
            tvError.setVisibility(View.VISIBLE);
        } else {
            tvError.setVisibility(View.GONE);
        }
    }

    @Override
    public void onKhuyenMaiLoadError(String message) {
        tvError.setText("Lỗi tải: " + message);
        tvError.setVisibility(View.VISIBLE);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onGiamGiaCalculated(double tongGiam, String maKhuyenMai) {}

    @Override
    public void onApplyError(String message) {}

    @Override
    public void onSelected(khuyenMai selectedKhuyenMai) {
        mMainController.tinhToanGiamGia(
                selectedKhuyenMai.getIdKhuyenMai(),
                tongTienHang,
                selectedKhuyenMai,
                new mainControllers.KhuyenMaiViewListener() {
                    @Override
                    public void onGiamGiaCalculated(double tongGiam, String maKhuyenMai) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("SELECTED_MA_KM", maKhuyenMai);
                        resultIntent.putExtra("GIAM_GIA_TRI", tongGiam);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }

                    @Override
                    public void onApplyError(String message) {
                        Toast.makeText(chitietkhuyenmai.this, message, Toast.LENGTH_LONG).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("SELECTED_MA_KM", (String)null);
                        resultIntent.putExtra("GIAM_GIA_TRI", 0.0);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }

                    @Override
                    public void onKhuyenMaiLoaded(List<khuyenMai> danhSach) {}
                    @Override
                    public void onKhuyenMaiLoadError(String message) {}
                });
    }
}
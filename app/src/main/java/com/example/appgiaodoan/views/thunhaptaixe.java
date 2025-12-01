package com.example.appgiaodoan.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgiaodoan.R;
import com.example.appgiaodoan.adapters.thuNhapTaiXeAdapter;
import com.example.appgiaodoan.controllers.mainControllers;
import com.example.appgiaodoan.models.thuNhapTaiXe;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class thunhaptaixe extends AppCompatActivity implements mainControllers.DriverIncomeListener {

    private TextView tvDoanhThuNgay, tvDoanhThuThang;
    private RecyclerView rvLichSu;
    private thuNhapTaiXeAdapter adapter;

    // SỬA LỖI Ở ĐÂY: Phải là List<thuNhapTaiXe> (Model) chứ không phải thunhaptaixe (Activity)
    private List<thuNhapTaiXe> listData = new ArrayList<>();

    private mainControllers mController;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thunhaptaixe);

        mController = new mainControllers();
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getString("USER_ID", null);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        tvDoanhThuNgay = findViewById(R.id.tvDoanhThuNgay);
        tvDoanhThuThang = findViewById(R.id.tvDoanhThuThang);
        rvLichSu = findViewById(R.id.rvLichSuChay);

        adapter = new thuNhapTaiXeAdapter(this, listData);
        rvLichSu.setLayoutManager(new LinearLayoutManager(this));
        rvLichSu.setAdapter(adapter);

        if (currentUserId != null) {
            mController.tinhThuNhapTaiXe(currentUserId, this);
        }
    }

    @Override
    // SỬA LỖI Ở ĐÂY: Tham số list phải là List<thuNhapTaiXe> để khớp với Interface
    public void onIncomeLoaded(double today, double month, List<thuNhapTaiXe> list) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvDoanhThuNgay.setText(nf.format(today));
        tvDoanhThuThang.setText(nf.format(month));

        listData.clear();
        listData.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
    }
}
package com.example.appgiaodoan.views;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgiaodoan.R;
import com.example.appgiaodoan.adapters.doanhThuAdapter;
import com.example.appgiaodoan.controllers.mainControllers;
import com.example.appgiaodoan.models.doanhThuMon;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class quanlydoanhthu extends AppCompatActivity implements mainControllers.DoanhThuListener {

    private TextView tvDoanhThuNgay, tvDoanhThuThang;
    private RecyclerView rvDoanhThuMon;
    private doanhThuAdapter adapter;
    private List<doanhThuMon> listMon = new ArrayList<>();

    private mainControllers mController;
    private String idNhaHang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quan_ly_doanh_thu);

        idNhaHang = getIntent().getStringExtra("ID_NHA_HANG");
        mController = new mainControllers();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        tvDoanhThuNgay = findViewById(R.id.tvDoanhThuNgay);
        tvDoanhThuThang = findViewById(R.id.tvDoanhThuThang);
        rvDoanhThuMon = findViewById(R.id.rvDoanhThuMon);

        adapter = new doanhThuAdapter(this, listMon);
        rvDoanhThuMon.setLayoutManager(new LinearLayoutManager(this));
        rvDoanhThuMon.setAdapter(adapter);

        if (idNhaHang != null) {
            mController.tinhToanDoanhThu(idNhaHang, this);
        }
    }

    @Override
    public void onCalculated(double todayRevenue, double monthRevenue, List<doanhThuMon> topItems) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        tvDoanhThuNgay.setText(nf.format(todayRevenue));
        tvDoanhThuThang.setText(nf.format(monthRevenue));

        listMon.clear();
        listMon.addAll(topItems);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
    }
}
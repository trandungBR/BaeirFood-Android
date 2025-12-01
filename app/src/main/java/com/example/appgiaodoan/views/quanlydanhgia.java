package com.example.appgiaodoan.views;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appgiaodoan.R;
import com.example.appgiaodoan.adapters.quanLyDanhGiaAdapter;
import com.example.appgiaodoan.controllers.mainControllers;
import com.example.appgiaodoan.models.danhGia;

import java.util.ArrayList;
import java.util.List;

public class quanlydanhgia extends AppCompatActivity implements mainControllers.QuanLyDanhGiaListener {

    private RecyclerView rvDanhGia;
    private quanLyDanhGiaAdapter adapter;
    private List<danhGia> dsDanhGia = new ArrayList<>();

    private TextView tvTongDanhGia;
    private RatingBar rbTongQuan;

    private mainControllers mController;
    private String idNhaHang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quan_ly_danh_gia);

        idNhaHang = getIntent().getStringExtra("ID_NHA_HANG");
        mController = new mainControllers();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvDanhGia = findViewById(R.id.rvDanhGiaQuan);
        tvTongDanhGia = findViewById(R.id.tvTongDanhGia);
        rbTongQuan = findViewById(R.id.rbTongQuan);

        adapter = new quanLyDanhGiaAdapter(this, dsDanhGia);
        rvDanhGia.setLayoutManager(new LinearLayoutManager(this));
        rvDanhGia.setAdapter(adapter);

        if (idNhaHang != null) {
            loadData();
        }
    }

    private void loadData() {
        mController.layDanhGiaCuaQuan(idNhaHang, this);
    }

    @Override
    public void onListLoaded(List<danhGia> list) {
        dsDanhGia.clear();
        dsDanhGia.addAll(list);
        adapter.notifyDataSetChanged();
        if (!list.isEmpty()) {
            double tongDiem = 0;
            for (danhGia dg : list) {
                tongDiem += dg.getDiem();
            }
            float trungBinh = (float) (tongDiem / list.size());
            rbTongQuan.setRating(trungBinh);
            tvTongDanhGia.setText("(" + list.size() + " lượt)");
        } else {
            rbTongQuan.setRating(0);
            tvTongDanhGia.setText("(Chưa có đánh giá)");
        }
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
    }
}
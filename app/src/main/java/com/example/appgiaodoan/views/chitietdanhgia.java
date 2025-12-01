package com.example.appgiaodoan.views;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appgiaodoan.R;
import com.example.appgiaodoan.adapters.danhGiaAdapter;
import com.example.appgiaodoan.controllers.mainControllers;
import com.example.appgiaodoan.models.danhGia;
import java.util.ArrayList;
import java.util.List;

public class chitietdanhgia extends AppCompatActivity implements mainControllers.DanhGiaListListener {

    private RecyclerView rvDanhGiaList;
    private ImageView btnBack;
    private TextView tvEmpty;
    private danhGiaAdapter adapter;
    private List<danhGia> dsDanhGia = new ArrayList<>();
    private mainControllers mController;
    private String idNhaHang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chitietdanhgia);

        rvDanhGiaList = findViewById(R.id.rvDanhGiaList);
        btnBack = findViewById(R.id.btnBack);
        tvEmpty = findViewById(R.id.tvEmpty);

        mController = new mainControllers();
        idNhaHang = getIntent().getStringExtra("ID_NHA_HANG");

        adapter = new danhGiaAdapter(this, dsDanhGia);
        rvDanhGiaList.setLayoutManager(new LinearLayoutManager(this));
        rvDanhGiaList.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        if (idNhaHang != null) {
            mController.layDanhSachDanhGia(idNhaHang, this);
        }
    }

    @Override
    public void onDanhGiaLoaded(List<danhGia> list) {
        dsDanhGia.clear();
        dsDanhGia.addAll(list);
        adapter.notifyDataSetChanged();

        if (list.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
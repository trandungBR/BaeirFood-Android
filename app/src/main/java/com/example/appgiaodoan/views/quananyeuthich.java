package com.example.appgiaodoan.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgiaodoan.R;
import com.example.appgiaodoan.adapters.quanAnAdapter;
import com.example.appgiaodoan.controllers.mainControllers;
import com.example.appgiaodoan.models.quanAn;

import java.util.ArrayList;
import java.util.List;

public class quananyeuthich extends AppCompatActivity implements
        mainControllers.YeuThichListener,
        quanAnAdapter.OnItemClickListener {

    private RecyclerView rvYeuThich;
    private ImageView btnBack;
    private TextView tvEmpty;

    private quanAnAdapter adapter;
    private List<quanAn> dsYeuThich = new ArrayList<>();

    private mainControllers mController;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yeuthich);

        mController = new mainControllers();
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getString("USER_ID", null);

        rvYeuThich = findViewById(R.id.rvYeuThich);
        btnBack = findViewById(R.id.btnBack);
        tvEmpty = findViewById(R.id.tvEmpty);

        // Tái sử dụng quanAnAdapter vì cấu trúc hiển thị giống nhau
        adapter = new quanAnAdapter(this, dsYeuThich);
        adapter.setOnItemClickListener(this);

        rvYeuThich.setLayoutManager(new LinearLayoutManager(this));
        rvYeuThich.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        if (currentUserId != null) {
            mController.layDanhSachYeuThich(currentUserId, this);
        }
    }

    @Override
    public void onYeuThichLoaded(List<quanAn> list) {
        dsYeuThich.clear();
        dsYeuThich.addAll(list);
        adapter.notifyDataSetChanged();

        if (list.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(quanAn quanAn) {
        Intent intent = new Intent(this, chitietnhahang.class);
        intent.putExtra("MA_NHA_HANG", quanAn.getIdNhaHang());
        startActivity(intent);
    }
}
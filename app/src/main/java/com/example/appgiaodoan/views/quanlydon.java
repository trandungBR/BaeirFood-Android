package com.example.appgiaodoan.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;

import com.example.appgiaodoan.R;
import com.example.appgiaodoan.adapters.donHangQuanAdapter;
import com.example.appgiaodoan.controllers.mainControllers;
import com.example.appgiaodoan.models.donHangQuan;

import java.util.ArrayList;
import java.util.List;

public class quanlydon extends AppCompatActivity implements mainControllers.QuanLyDonListener, donHangQuanAdapter.OrderActionListener {

    private TabLayout tabLayout;
    private RecyclerView rvDonHang;
    private donHangQuanAdapter adapter;

    private List<donHangQuan> fullList = new ArrayList<>();
    private List<donHangQuan> filteredList = new ArrayList<>();

    private mainControllers mController;
    private String idNhaHang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quan_ly_don);

        idNhaHang = getIntent().getStringExtra("ID_NHA_HANG");
        mController = new mainControllers();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        tabLayout = findViewById(R.id.tabLayout);
        rvDonHang = findViewById(R.id.rvDonHang);

        setupTabs();

        adapter = new donHangQuanAdapter(this, filteredList, this);
        rvDonHang.setLayoutManager(new LinearLayoutManager(this));
        rvDonHang.setAdapter(adapter);

        if (idNhaHang != null) {
            loadData();
        }
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Tất cả"));
        tabLayout.addTab(tabLayout.newTab().setText("Mới"));
        tabLayout.addTab(tabLayout.newTab().setText("Đang làm"));
        tabLayout.addTab(tabLayout.newTab().setText("Hoàn thành"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã hủy"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterData(tab.getPosition());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadData() {
        mController.layDanhSachDonCuaQuan(idNhaHang, this);
    }

    private void filterData(int tabIndex) {
        filteredList.clear();
        if (tabIndex == 0) {
            filteredList.addAll(fullList);
        } else {
            String targetStatus = "";
            switch (tabIndex) {
                case 1: targetStatus = "Đang xử lý"; break;
                case 2: targetStatus = "Đang chuẩn bị"; break;
                case 3: targetStatus = "Hoàn thành"; break;
                case 4: targetStatus = "Đã hủy"; break;
            }

            for (donHangQuan item : fullList) {
                if (item.getTrangThai().equals(targetStatus)) {
                    filteredList.add(item);
                }
                if (tabIndex == 2 && (item.getTrangThai().equals("Đang giao"))) {
                    if (!filteredList.contains(item)) filteredList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onListLoaded(List<donHangQuan> list) {
        fullList.clear();
        fullList.addAll(list);

        // Mặc định hiển thị tab đang chọn
        filterData(tabLayout.getSelectedTabPosition());
    }

    @Override
    public void onStatusUpdated(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        loadData(); // Reload lại danh sách sau khi cập nhật
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
    }

    // --- Adapter Callbacks ---
    @Override
    public void onUpdateStatus(String idDonHang, String newStatus) {
        mController.capNhatTrangThaiDonQuan(idDonHang, newStatus, this);
    }
}
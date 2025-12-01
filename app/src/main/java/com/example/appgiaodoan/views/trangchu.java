package com.example.appgiaodoan.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgiaodoan.R;
import com.example.appgiaodoan.adapters.quanAnAdapter;
import com.example.appgiaodoan.controllers.mainControllers;
import com.example.appgiaodoan.models.quanAn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class trangchu extends AppCompatActivity implements
        SearchView.OnQueryTextListener,
        mainControllers.ProfileViewListener,
        NavigationView.OnNavigationItemSelectedListener,
        mainControllers.TrangChuViewListener,
        quanAnAdapter.OnItemClickListener,
        mainControllers.TrangChuActiveOrderListener
{
    private LinearLayout footerActiveOrder;
    private TextView tvFooterStatus;

    private String currentActiveOrderId;
    private RecyclerView recyclerViewQuanAn;
    private quanAnAdapter adapterQuanAn;
    private List<quanAn> danhSachQuanAnGoc = new ArrayList<>();
    private List<quanAn> danhSachHienThi = new ArrayList<>();

    private DrawerLayout drawerLayoutMenu;
    private ImageView imageViewMenu;
    private TextView tvDiaChi;
    private SearchView searchView;
    private NavigationView navigationView;

    private TextView tvTenNguoiDungHeader;

    private SharedPreferences sharedPreferences;
    private String currentUserId;
    private LinearLayout tabTrangChu;
    private LinearLayout tabHoatDong;
    private mainControllers mMainController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trangchu);

        recyclerViewQuanAn = findViewById(R.id.rVQuanAn);
        recyclerViewQuanAn.setLayoutManager(new LinearLayoutManager(this));

        drawerLayoutMenu = findViewById(R.id.drawer_layout);
        imageViewMenu = findViewById(R.id.imageViewMenu);
        tvDiaChi = findViewById(R.id.tvDiaChi);
        searchView = findViewById(R.id.search);
        tabTrangChu = findViewById(R.id.tabTrangChu);
        LinearLayout tabHoatDong = findViewById(R.id.tabHoatDong);
        navigationView = findViewById(R.id.nav_view);
        footerActiveOrder = findViewById(R.id.footerActiveOrder);
        tvFooterStatus = findViewById(R.id.tvFooterStatus);

        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            tvTenNguoiDungHeader = headerView.findViewById(R.id.tvTenNguoiDungNav);
        }

        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("USER_ID", null);
        mMainController = new mainControllers();

        adapterQuanAn = new quanAnAdapter(this, danhSachHienThi);
        recyclerViewQuanAn.setAdapter(adapterQuanAn);

        adapterQuanAn.setOnItemClickListener(this);

        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, dangnhap.class));
            finish();
            return;
        }

        navigationView.setNavigationItemSelectedListener(this);

        mMainController.layDanhSachQuanAn(this);

        imageViewMenu.setOnClickListener(v -> {
            if (drawerLayoutMenu.isDrawerOpen(GravityCompat.END))
                drawerLayoutMenu.closeDrawer(GravityCompat.END);
            else
                drawerLayoutMenu.openDrawer(GravityCompat.END);
        });
        footerActiveOrder.setOnClickListener(v -> {
            if (currentActiveOrderId != null) {
                Toast.makeText(this, "Xem chi tiết đơn: " + currentActiveOrderId, Toast.LENGTH_SHORT).show();
            }
        });
        tabHoatDong.setOnClickListener(v -> {
            Intent intent = new Intent(trangchu.this, lichsuhoatdong.class);
            startActivity(intent);
            overridePendingTransition(0, 0); // Tắt hiệu ứng chuyển cảnh
        });
        tvDiaChi.setOnClickListener(v -> {
            Intent intent = new Intent(trangchu.this, nhapViTri.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });

        searchView.setOnQueryTextListener(this);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayoutMenu.isDrawerOpen(GravityCompat.END)) {
                    drawerLayoutMenu.closeDrawer(GravityCompat.END);
                } else {
                    finish();
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (currentUserId != null) {
            mMainController.checkDonHangActive(currentUserId, this);
        }
    }
    @Override
    public void onShowActiveOrder(String idDonHang, String thoiGianDat) {
        this.currentActiveOrderId = idDonHang;
        footerActiveOrder.setVisibility(View.VISIBLE);
        String duKien = "30 phút nữa";
        tvFooterStatus.setText("Dự kiến giao: " + duKien + " | Đang xử lý");
    }

    @Override
    public void onHideActiveOrder() {
        footerActiveOrder.setVisibility(View.GONE);
        this.currentActiveOrderId = null;
    }
    @Override
    public void onItemClick(quanAn quanAn) {
        Intent intent = new Intent(this, chitietnhahang.class);

        intent.putExtra("MA_NHA_HANG", quanAn.getIdNhaHang());

        startActivity(intent);
    }

    @Override
    public void hienThiDanhSachQuanAn(List<quanAn> danhSach) {
        danhSachQuanAnGoc.clear();
        danhSachQuanAnGoc.addAll(danhSach);
        danhSachHienThi.clear();
        danhSachHienThi.addAll(danhSach);
        adapterQuanAn.notifyDataSetChanged();

        mMainController.layThongTinNguoiDung(currentUserId, this);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, "Lỗi tải quán ăn: " + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_cai_dat) {
            startActivity(new Intent(this, caidat.class));
        } else if (id == R.id.nav_yeu_thich) {
            startActivity(new Intent(this, quananyeuthich.class));
        } else if (id == R.id.nav_dang_xuat) {
            xuLyDangXuat();
        }

        drawerLayoutMenu.closeDrawer(GravityCompat.END);
        return true;
    }

    private void xuLyDangXuat() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("USER_ID");
        editor.remove("USER_EMAIL");
        editor.apply();

        Intent intent = new Intent(this, dangnhap.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void tinhKhoangCachTheoDiaChi(String diaChiNguoiDung, List<quanAn> danhSach) {
        if (diaChiNguoiDung == null || diaChiNguoiDung.isEmpty()) return;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> userAddresses = geocoder.getFromLocationName(diaChiNguoiDung, 1);
            if (userAddresses == null || userAddresses.isEmpty()) return;

            double userLat = userAddresses.get(0).getLatitude();
            double userLng = userAddresses.get(0).getLongitude();

            for (quanAn qa : danhSach) {
                if (qa.getDiaChi() != null) {
                    List<Address> restAddresses = geocoder.getFromLocationName(qa.getDiaChi(), 1);
                    if (restAddresses != null && !restAddresses.isEmpty()) {
                        double lat = restAddresses.get(0).getLatitude();
                        double lng = restAddresses.get(0).getLongitude();

                        float[] results = new float[1];
                        android.location.Location.distanceBetween(userLat, userLng, lat, lng, results);
                        qa.setKhoangCach(String.format(Locale.US, "%.2f km", results[0] / 1000));
                    } else {
                        qa.setKhoangCach("-- km");
                    }
                }
            }
            adapterQuanAn.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query != null && !query.isEmpty()) {
            locDanhSachQuanAn(query);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        locDanhSachQuanAn(newText);
        return true;
    }

    private void locDanhSachQuanAn(String tuKhoa) {
        String keyword = tuKhoa.toLowerCase(Locale.getDefault()).trim();
        danhSachHienThi.clear();

        if (keyword.isEmpty()) {
            danhSachHienThi.addAll(danhSachQuanAnGoc);
        } else {
            for (quanAn qa : danhSachQuanAnGoc) {
                if (qa.getTenNhaHang().toLowerCase(Locale.getDefault()).contains(keyword)) {
                    danhSachHienThi.add(qa);
                }
            }
        }
        adapterQuanAn.notifyDataSetChanged();
    }

    @Override
    public void onProfileLoaded(String ten, String diaChi, boolean nhanThongBao) {
        if (tvTenNguoiDungHeader != null && ten != null) {
            tvTenNguoiDungHeader.setText(ten);
        }

        if (tvDiaChi != null && diaChi != null) {
            tvDiaChi.setText(diaChi);
        }

        tinhKhoangCachTheoDiaChi(diaChi, danhSachHienThi);
    }

    @Override
    public void onProfileLoadError(String message) {
        Toast.makeText(this, "Lỗi tải thông tin: " + message, Toast.LENGTH_SHORT).show();
    }
}
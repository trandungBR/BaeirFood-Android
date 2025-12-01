package com.example.appgiaodoan.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgiaodoan.R;
import com.example.appgiaodoan.adapters.donHangTaiXeAdapter;
import com.example.appgiaodoan.controllers.mainControllers;
import com.example.appgiaodoan.models.donHangTaiXe;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class trangchutaixe extends AppCompatActivity implements mainControllers.DriverHomeListener {

    private RecyclerView rvDonHang;
    private donHangTaiXeAdapter adapter;
    private List<donHangTaiXe> dsDonHang = new ArrayList<>();
    private mainControllers mController;
    private String currentUserId;
    private String selectedOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trangchutaixe);

        mController = new mainControllers();
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getString("USER_ID", null);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tài Xế - Tìm Đơn");
        }

        rvDonHang = findViewById(R.id.rvDonHangTaiXe);

        adapter = new donHangTaiXeAdapter(this, dsDonHang, donHang -> {
            selectedOrderId = donHang.getIdDonHang();
            mController.taiXeNhanDon(donHang.getIdDonHang(), currentUserId, donHang.getPhiGiaoHang(), this);
        });

        rvDonHang.setLayoutManager(new LinearLayoutManager(this));
        rvDonHang.setAdapter(adapter);

        loadData();
    }

    private void loadData() {
        mController.layDonHangChoTaiXe(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tai_xe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_income) {
            Toast.makeText(this, "Chức năng Thu nhập", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.menu_income) {
            Intent intent = new Intent(this, thunhaptaixe.class);
            startActivity(intent);
        }else if (id == R.id.menu_logout) {
            SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(this, dangnhap.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOrdersLoaded(List<donHangTaiXe> list) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        for (donHangTaiXe item : list) {
            double dist = calculateDistance(geocoder, item.getDiaChiQuan(), item.getDiaChiKhach());
            item.setKhoangCachKm(dist);
        }
        Collections.sort(list, (o1, o2) -> Double.compare(o1.getKhoangCachKm(), o2.getKhoangCachKm()));
        dsDonHang.clear();
        dsDonHang.addAll(list);
        adapter.notifyDataSetChanged();
    }

    private double calculateDistance(Geocoder geocoder, String address1, String address2) {
        if (address1 == null || address2 == null || address1.isEmpty() || address2.isEmpty()) return 0;

        try {
            List<Address> loc1 = geocoder.getFromLocationName(address1, 1);
            List<Address> loc2 = geocoder.getFromLocationName(address2, 1);

            if (loc1 != null && !loc1.isEmpty() && loc2 != null && !loc2.isEmpty()) {
                double lat1 = loc1.get(0).getLatitude();
                double lng1 = loc1.get(0).getLongitude();
                double lat2 = loc2.get(0).getLatitude();
                double lng2 = loc2.get(0).getLongitude();

                float[] results = new float[1];
                android.location.Location.distanceBetween(lat1, lng1, lat2, lng2, results);

                return results[0] / 1000.0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void onOrderAccepted(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        if (selectedOrderId != null) {
            Intent intent = new Intent(this, chitietgiaohang.class);
            intent.putExtra("ID_DON_HANG", selectedOrderId);
            startActivity(intent);
        }

        loadData();
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
    }
}
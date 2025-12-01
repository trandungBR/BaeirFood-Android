package com.example.appgiaodoan.views;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgiaodoan.R;
import com.example.appgiaodoan.adapters.quanAnAdapter;
import com.example.appgiaodoan.controllers.mainControllers;
import com.example.appgiaodoan.models.quanAn;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class timKiem extends AppCompatActivity {

    private static final String TAG = "TimKiem";

    private Toolbar toolbarTimKiem;
    private EditText etTimKiem;
    private ChipGroup chipGroupLoc;
    private Chip chipGanToi, chipDanhGia;
    private RecyclerView rvKetQuaTimKiem;

    private quanAnAdapter adapterKetQua;
    private List<quanAn> danhSachHienThi;

    private String tuKhoaTimKiem = "";
    private int boLocDaChonId = -1;

    private mainControllers mMainController;

    // Biến lưu địa chỉ người dùng từ bảng nguoidung
    private String diaChiNguoiDung = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timkiem);

        toolbarTimKiem = findViewById(R.id.toolbarTimKiem);
        etTimKiem = findViewById(R.id.etTimKiem);
        chipGroupLoc = findViewById(R.id.chipGroupLoc);
        chipGanToi = findViewById(R.id.chipGanToi);
        chipDanhGia = findViewById(R.id.chipDanhGia);
        rvKetQuaTimKiem = findViewById(R.id.rvKetQuaTimKiem);

        setSupportActionBar(toolbarTimKiem);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbarTimKiem.setNavigationOnClickListener(v -> finish());

        danhSachHienThi = new ArrayList<>();
        adapterKetQua = new quanAnAdapter(this, danhSachHienThi);
        rvKetQuaTimKiem.setLayoutManager(new LinearLayoutManager(this));
        rvKetQuaTimKiem.setAdapter(adapterKetQua);

        mMainController = new mainControllers();

        // Lấy từ khóa từ intent
        Intent intent = getIntent();
        tuKhoaTimKiem = intent.getStringExtra("TU_KHOA");
        if (tuKhoaTimKiem == null) tuKhoaTimKiem = "";
        etTimKiem.setText(tuKhoaTimKiem);

        // Lấy địa chỉ người dùng từ database
        layDiaChiNguoiDung();

        // Thực hiện tìm kiếm ban đầu
        timKiemSupabase(tuKhoaTimKiem);

        // Thêm sự kiện realtime search
        etTimKiem.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tuKhoaTimKiem = s.toString().trim();
                timKiemSupabase(tuKhoaTimKiem);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Lọc Gần tôi / Đánh giá
        chipGroupLoc.setOnCheckedChangeListener((group, checkedId) -> {
            boLocDaChonId = (checkedId == View.NO_ID) ? -1 : checkedId;
            sapXepHienThi();
        });
    }

    private void layDiaChiNguoiDung() {
        // Giả sử bạn có userId, ví dụ từ SharedPreferences
        String userId = "userId_example"; // thay bằng thực tế
        mMainController.layThongTinNguoiDung(userId, new mainControllers.ProfileViewListener() {
            @Override
            public void onProfileLoaded(String tenNguoiDung, String diaChi, boolean nhanThongBao) {
                diaChiNguoiDung = diaChi;
                // Nếu danh sách hiện tại đã có quán ăn, tính khoảng cách luôn
                if (!danhSachHienThi.isEmpty()) {
                    tinhKhoangCachTheoDiaChi(diaChiNguoiDung, danhSachHienThi);
                    sapXepHienThi();
                }
            }

            @Override
            public void onProfileLoadError(String message) {
                Toast.makeText(timKiem.this, "Lỗi lấy địa chỉ người dùng: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void timKiemSupabase(String tuKhoa) {
        mMainController.timKiemQuanAnSupabase(tuKhoa, new mainControllers.TimKiemListener() {
            @Override
            public void onSuccess(List<quanAn> danhSach) {
                danhSachHienThi.clear();
                danhSachHienThi.addAll(danhSach);
                // Tính khoảng cách theo địa chỉ người dùng
                if (!diaChiNguoiDung.isEmpty()) {
                    tinhKhoangCachTheoDiaChi(diaChiNguoiDung, danhSachHienThi);
                }
                adapterKetQua.notifyDataSetChanged();
                sapXepHienThi();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(timKiem.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sapXepHienThi() {
        if (boLocDaChonId == R.id.chipGanToi) {
            // Sắp xếp khoảng cách nhỏ -> lớn
            Collections.sort(danhSachHienThi, Comparator.comparingDouble(qa -> parseKhoangCach(qa.getKhoangCach())));
        } else if (boLocDaChonId == R.id.chipDanhGia) {
            // Sắp xếp đánh giá lớn -> nhỏ
            Collections.sort(danhSachHienThi, (qa1, qa2) -> Double.compare(parseDanhGia(qa2.getDanhGia()), parseDanhGia(qa1.getDanhGia())));
        }
        adapterKetQua.notifyDataSetChanged();
        if (!danhSachHienThi.isEmpty()) rvKetQuaTimKiem.scrollToPosition(0);
    }

    private double parseKhoangCach(String khoangCachStr) {
        if (khoangCachStr == null || khoangCachStr.isEmpty()) return Double.MAX_VALUE;
        try {
            String numberStr = khoangCachStr.replace(" km", "").trim().replace(',', '.');
            return Double.parseDouble(numberStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Lỗi parse khoảng cách: " + khoangCachStr, e);
            return Double.MAX_VALUE;
        }
    }

    private double parseDanhGia(String danhGiaStr) {
        if (danhGiaStr == null || danhGiaStr.isEmpty()) return 0.0;
        try {
            int startIndex = 0;
            while (startIndex < danhGiaStr.length() && !Character.isDigit(danhGiaStr.charAt(startIndex)) && danhGiaStr.charAt(startIndex) != '.') {
                startIndex++;
            }
            if (startIndex >= danhGiaStr.length()) return 0.0;

            int endIndex = startIndex;
            while (endIndex < danhGiaStr.length() && (Character.isDigit(danhGiaStr.charAt(endIndex)) || danhGiaStr.charAt(endIndex) == '.')) {
                endIndex++;
            }
            String numberStr = danhGiaStr.substring(startIndex, endIndex).replace(',', '.');
            return Double.parseDouble(numberStr);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi parse đánh giá: " + danhGiaStr, e);
            return 0.0;
        }
    }

    private void tinhKhoangCachTheoDiaChi(String diaChiNguoiDung, List<quanAn> danhSach) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(diaChiNguoiDung, 1);
            if (addresses != null && !addresses.isEmpty()) {
                double userLat = addresses.get(0).getLatitude();
                double userLng = addresses.get(0).getLongitude();

                for (quanAn qa : danhSach) {
                    double lat = qa.getLatitude();
                    double lng = qa.getLongitude();
                    float[] results = new float[1];
                    Location.distanceBetween(userLat, userLng, lat, lng, results);
                    qa.setKhoangCach(String.format(Locale.US, "%.2f km", results[0] / 1000));
                }

                adapterKetQua.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Không lấy được tọa độ từ địa chỉ người dùng", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi geocoding: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

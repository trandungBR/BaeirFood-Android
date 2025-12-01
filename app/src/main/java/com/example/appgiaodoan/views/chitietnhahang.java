package com.example.appgiaodoan.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appgiaodoan.R;
import com.example.appgiaodoan.adapters.monAnAdapter;
import com.example.appgiaodoan.controllers.mainControllers;
import com.example.appgiaodoan.models.monAn;
import com.example.appgiaodoan.models.quanAn;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class chitietnhahang extends AppCompatActivity implements
        monAnAdapter.MonAnListener,
        mainControllers.ChiTietNhaHangViewListener
{

    private ImageView ivQuan;
    private TextView tvTenQuan, tvDiaChi;
    private RatingBar ratingBar;
    private RecyclerView rvMonAn;
    private monAnAdapter adapter;
    private List<monAn> dsMonAn = new ArrayList<>();

    private mainControllers mMainController;
    private String maNhaHang;

    // THÊM BIẾN NÀY
    private String currentUserId;

    private ImageView btnFavorite;
    private boolean isLiked = false;
    private LinearLayout footerGioHang;
    private TextView tvGioHangSoLuong;
    private TextView tvGioHangTongTien;

    private ImageView btnBack;

    private HashMap<String, Integer> cartQuantities = new HashMap<>();
    private int soLuongMon = 0;
    private double tongTien = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chitietnhahang);

        // KHỞI TẠO USER ID
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getString("USER_ID", null);

        mMainController = new mainControllers();

        ivQuan = findViewById(R.id.ivQuan);
        tvTenQuan = findViewById(R.id.tvTenQuan);
        tvDiaChi = findViewById(R.id.tvDiaChiQuan);
        ratingBar = findViewById(R.id.ratingQuan);
        btnFavorite = findViewById(R.id.btnFavorite);
        rvMonAn = findViewById(R.id.rvMonAn);

        btnBack = findViewById(R.id.btnBack);

        footerGioHang = findViewById(R.id.footerGioHang);
        tvGioHangSoLuong = findViewById(R.id.tvGioHangSoLuong);
        tvGioHangTongTien = findViewById(R.id.tvGioHangTongTien);

        // Lấy ID Nhà hàng TRƯỚC KHI sử dụng
        maNhaHang = getIntent().getStringExtra("MA_NHA_HANG");

        if (maNhaHang == null || maNhaHang.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy mã nhà hàng.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Gọi hàm tính điểm đánh giá
        mMainController.tinhDiemDanhGia(maNhaHang, (ratingTrungBinh, tongLuot) -> {
            ratingBar.setRating(ratingTrungBinh);
        });

        mMainController.layChiTietNhaHang(maNhaHang, this);

        // Xử lý click Footer
        footerGioHang.setOnClickListener(v -> {
            if (soLuongMon > 0) {
                luuGioHangTam();

                Intent intent = new Intent(chitietnhahang.this, chitietdonhang.class);
                intent.putExtra("MA_NHA_HANG", maNhaHang);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Giỏ hàng rỗng!", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(chitietnhahang.this, trangchu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // KIỂM TRA YÊU THÍCH (Giờ đã có currentUserId)
        if (maNhaHang != null && currentUserId != null) {
            new com.example.appgiaodoan.models.database().checkYeuThich(currentUserId, maNhaHang, new com.example.appgiaodoan.models.database.BooleanCallback() {
                @Override
                public void onResult(boolean liked) {
                    isLiked = liked;
                    runOnUiThread(() -> updateFavoriteIcon());
                }
                @Override public void onError(String m) {}
            });
        }

        btnFavorite.setOnClickListener(v -> {
            // Hiệu ứng click ngay lập tức cho mượt
            isLiked = !isLiked;
            updateFavoriteIcon();

            // Gửi request lên server
            new com.example.appgiaodoan.models.database().toggleYeuThich(currentUserId, maNhaHang, !isLiked, new com.example.appgiaodoan.models.database.ModelCallbackSimple() {
                @Override
                public void onSuccess(String message) {
                    runOnUiThread(() -> Toast.makeText(chitietnhahang.this, message, Toast.LENGTH_SHORT).show());
                }
                @Override
                public void onError(String message) {
                    // Nếu lỗi thì revert lại icon
                    isLiked = !isLiked;
                    runOnUiThread(() -> {
                        updateFavoriteIcon();
                        Toast.makeText(chitietnhahang.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        ratingBar.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                Intent intent = new Intent(chitietnhahang.this, chitietdanhgia.class);
                intent.putExtra("ID_NHA_HANG", maNhaHang);
                startActivity(intent);
            }
            return true; // Trả về true để chặn sự kiện thay đổi sao
        });

        khoiTaoGioHang();

        adapter = new monAnAdapter(this, dsMonAn, this);
        rvMonAn.setLayoutManager(new LinearLayoutManager(this));
        rvMonAn.setAdapter(adapter);
    }

    private void updateFavoriteIcon() {
        if (isLiked) {
            btnFavorite.setImageResource(R.drawable.heart_check_24dp_e3e3e3_fill0_wght400_grad0_opsz24);
        } else {
            btnFavorite.setImageResource(R.drawable.favorite_24dp_e3e3e3_fill0_wght400_grad0_opsz24);
        }
    }

    private void khoiTaoGioHang() {
        SharedPreferences sharedPreferences = getSharedPreferences("GioHangPrefs", Context.MODE_PRIVATE);

        if (maNhaHang != null && maNhaHang.equals(sharedPreferences.getString("CURRENT_MA_NHA_HANG", null))) {
            String jsonQuantities = sharedPreferences.getString("CURRENT_CART_ITEMS", "{}");

            try {
                java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<HashMap<String, Integer>>() {}.getType();
                cartQuantities = new Gson().fromJson(jsonQuantities, type);
            } catch (Exception e) {
                cartQuantities = new HashMap<>();
            }

            soLuongMon = cartQuantities.values().stream().mapToInt(Integer::intValue).sum();

            updateGioHangUI();
        } else {
            footerGioHang.setVisibility(View.GONE);
        }
    }

    private void luuGioHangTam() {
        SharedPreferences sharedPreferences = getSharedPreferences("GioHangPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String jsonQuantities = new Gson().toJson(cartQuantities);

        editor.putString("CURRENT_CART_ITEMS", jsonQuantities);
        editor.putString("CURRENT_MA_NHA_HANG", maNhaHang);
        editor.apply();
    }


    @Override
    public void onAddToCart(monAn monAn) {
        int currentQty = cartQuantities.getOrDefault(monAn.getIdMonAn(), 0);

        cartQuantities.put(monAn.getIdMonAn(), currentQty + 1);

        soLuongMon++;
        tongTien += monAn.getGia();

        updateGioHangUI();

        Toast.makeText(this, monAn.getTenMonAn() + " đã thêm vào giỏ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSelect(monAn monAn) {
        Toast.makeText(this, "Đã chọn: " + monAn.getTenMonAn(), Toast.LENGTH_SHORT).show();
    }

    private void updateGioHangUI() {
        if (soLuongMon > 0) {
            footerGioHang.setVisibility(View.VISIBLE);
            tvGioHangSoLuong.setText("Giỏ Hàng (" + soLuongMon + " món)");

            String formattedTotal = String.format(Locale.getDefault(), "%,.0f VNĐ", tongTien);
            tvGioHangTongTien.setText(formattedTotal);
        } else {
            footerGioHang.setVisibility(View.GONE);
        }
    }

    @Override
    public void hienThiChiTietNhaHang(quanAn nhaHang, List<monAn> danhSachMonAn) {
        if (isFinishing() || isDestroyed()) return; // Thêm check

        tvTenQuan.setText(nhaHang.getTenNhaHang() != null ? nhaHang.getTenNhaHang() : "Quán ăn");
        tvDiaChi.setText(nhaHang.getDiaChi() != null ? nhaHang.getDiaChi() : "");

        String imageUrl = nhaHang.getAnhDaiDien_URL();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(ivQuan);
        } else {
            ivQuan.setImageResource(R.drawable.ic_launcher_background);
        }

        dsMonAn.clear();
        if (danhSachMonAn != null) {
            dsMonAn.addAll(danhSachMonAn);
        }
        adapter.notifyDataSetChanged();

        tongTien = dsMonAn.stream()
                .mapToDouble(item -> item.getGia() * cartQuantities.getOrDefault(item.getIdMonAn(), 0))
                .sum();
        updateGioHangUI();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, "Lỗi tải chi tiết: " + message, Toast.LENGTH_LONG).show();
        finish();
    }
}
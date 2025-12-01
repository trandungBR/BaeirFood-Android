package com.example.appgiaodoan.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;

import com.example.appgiaodoan.R;
import com.example.appgiaodoan.adapters.monAnGioHangAdapter;
import com.example.appgiaodoan.controllers.mainControllers;
import com.example.appgiaodoan.models.khuyenMai;
import com.example.appgiaodoan.models.monAn;
import com.example.appgiaodoan.models.quanAn;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class chitietdonhang extends AppCompatActivity implements
        mainControllers.ProfileViewListener,
        mainControllers.KhuyenMaiViewListener,
        monAnGioHangAdapter.GioHangUpdateListener,
        mainControllers.GioHangListener,
        mainControllers.MomoPaymentListener
{

    private ImageView btnBack;
    private TextView tvTenQuanHeader, tvDiaChiNguoiDung;
    private TextView tvPhuongThucThanhToan, tvChiTietThanhToan;
    private TextView tvKhuyenMaiText;
    private TextView tvTongCongGiaTri;
    private TextView tvTongTamTinh;
    private Button btnDatDon;
    private RadioGroup radioGroupDeliveryOptions;
    private RecyclerView rvGioHangMonAn;

    private monAnGioHangAdapter adapterGioHang;
    private List<monAn> danhSachMonAnTrongGio = new ArrayList<>();
    private HashMap<String, Integer> currentQuantities = new HashMap<>();

    private mainControllers mMainController;
    private SharedPreferences sharedPreferences;
    private String currentUserId;

    private String maNhaHang;
    private String userPhone = "";
    private double tongTienHang = 0;
    private double phiGiaoHang = 0;
    private double tongGiamGia = 0;
    private double khoangCachKm = 8.5;
    private String maKhuyenMaiDaChon = null;
    private String phuongThucThanhToanDaChon = "CASH";
    private khuyenMai khuyenMaiDuocApDung = null;

    private static final int REQUEST_CODE_KHUYENMAI = 100;
    private static final int REQUEST_CODE_PAYMENT = 200;
    private static final int REQUEST_CODE_MOMO_PAYMENT = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chitietdonhang);

        mMainController = new mainControllers();
        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("USER_ID", null);

        taiGioHangTam();

        btnBack = findViewById(R.id.btnBack);
        tvTenQuanHeader = findViewById(R.id.tvTenQuanHeader);
        tvDiaChiNguoiDung = findViewById(R.id.tvDiaChiNguoiDung);
        radioGroupDeliveryOptions = findViewById(R.id.radioGroupDeliveryOptions);
        tvPhuongThucThanhToan = findViewById(R.id.tvPhuongThucThanhToan);
        tvChiTietThanhToan = findViewById(R.id.tvChiTietThanhToan);
        tvKhuyenMaiText = findViewById(R.id.tvKhuyenMaiText);
        tvTongCongGiaTri = findViewById(R.id.tvTongCongGiaTri);
        tvTongTamTinh = findViewById(R.id.tvTongTamTinh);
        btnDatDon = findViewById(R.id.btnDatDon);
        rvGioHangMonAn = findViewById(R.id.rvGioHangMonAn);

        adapterGioHang = new monAnGioHangAdapter(this, danhSachMonAnTrongGio, currentQuantities);
        rvGioHangMonAn.setLayoutManager(new LinearLayoutManager(this));
        rvGioHangMonAn.setAdapter(adapterGioHang);

        adapterGioHang.setGioHangUpdateListener(this);
        tongTienHang = adapterGioHang.getCurrentTotal();

        mMainController.layThongTinNguoiDung(currentUserId, this);
        mMainController.layDanhSachKhuyenMai(maNhaHang, this);

        calculateAndUpdateDeliveryFee("BinhThuong");

        btnBack.setOnClickListener(v -> finish());

        tvDiaChiNguoiDung.setOnClickListener(v -> {
            Intent intent = new Intent(chitietdonhang.this, nhapViTri.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });

        radioGroupDeliveryOptions.setOnCheckedChangeListener((group, checkedId) -> {
            String loaiDichVu = "";
            if (checkedId == R.id.rbPriority) loaiDichVu = "UuTien";
            else if (checkedId == R.id.rbStandard) loaiDichVu = "BinhThuong";
            else if (checkedId == R.id.rbEconomy) loaiDichVu = "TietKiem";
            calculateAndUpdateDeliveryFee(loaiDichVu);
        });

        findViewById(R.id.llKhuyenMai).setOnClickListener(v -> {
            Intent intent = new Intent(this, chitietkhuyenmai.class);
            intent.putExtra("TONG_TIEN_HANG", tongTienHang);
            intent.putExtra("ID_NHA_HANG", maNhaHang);
            startActivityForResult(intent, REQUEST_CODE_KHUYENMAI);
        });

        tvChiTietThanhToan.setOnClickListener(v -> {
            Intent intent = new Intent(this, chitietthanhtoan.class);
            startActivityForResult(intent, REQUEST_CODE_PAYMENT);
        });

        btnDatDon.setOnClickListener(v -> {
            double tongThanhToan = tongTienHang + phiGiaoHang - tongGiamGia;
            if (tongThanhToan <= 0) tongThanhToan = 1000;

            if ("CASH".equals(phuongThucThanhToanDaChon)) {
                mMainController.xuLyDatDon(this, currentUserId, maNhaHang, tongThanhToan, phiGiaoHang,
                        userPhone,
                        phuongThucThanhToanDaChon,
                        maKhuyenMaiDaChon,
                        adapterGioHang.getQuantities(), this);
            } else if ("MOMO".equals(phuongThucThanhToanDaChon) || "CARD".equals(phuongThucThanhToanDaChon)) {
                String orderId = "DH" + System.currentTimeMillis();
                mMainController.taoThanhToanMomo(tongThanhToan, orderId, this);
            } else {
                Toast.makeText(this, "Vui lòng chọn phương thức thanh toán hợp lệ.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onSuccess(String message) {
        if (message != null && message.toLowerCase(Locale.ROOT).startsWith("http")) {
            onPaymentUrlReceived(message);
        } else {
            runOnUiThread(() -> {
                mMainController.xoaGioHangTam(chitietdonhang.this, chitietdonhang.this);

                Intent intent = new Intent(chitietdonhang.this, loadgiaohang.class);
                startActivity(intent);
                finish();
            });
        }
    }
    private void taiGioHangTam() {
        SharedPreferences sharedPreferences = getSharedPreferences("GioHangPrefs", Context.MODE_PRIVATE);

        maNhaHang = sharedPreferences.getString("CURRENT_MA_NHA_HANG", null);
        String jsonQuantities = sharedPreferences.getString("CURRENT_CART_ITEMS", "{}");

        Type type = new TypeToken<HashMap<String, Integer>>() {}.getType();
        currentQuantities = new Gson().fromJson(jsonQuantities, type);

        if (maNhaHang != null && !currentQuantities.isEmpty()) {
            mMainController.layChiTietGioHang(currentQuantities, maNhaHang, this);
        } else {
            Toast.makeText(this, "Giỏ hàng tạm thời rỗng.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onQuantityChanged() {
        tongTienHang = adapterGioHang.getCurrentTotal();

        int checkedId = radioGroupDeliveryOptions.getCheckedRadioButtonId();
        String type = "BinhThuong";
        if (checkedId == R.id.rbPriority) type = "UuTien";
        else if (checkedId == R.id.rbStandard) type = "BinhThuong";
        else if (checkedId == R.id.rbEconomy) type = "TietKiem";

        calculateAndUpdateDeliveryFee(type);
    }

    private void calculateAndUpdateDeliveryFee(String loaiDichVu) {
        mainControllers.GiaoHang result = mMainController.phiGiaoHang(khoangCachKm, loaiDichVu);
        phiGiaoHang = result.phi;
        double thoiGianPhut = result.thoigian;

        TextView rbPriority = findViewById(R.id.rbPriority);
        TextView rbStandard = findViewById(R.id.rbStandard);
        TextView rbEconomy = findViewById(R.id.rbEconomy);

        rbPriority.setText(String.format(Locale.getDefault(), "Ưu tiên (Thời gian: ~%.0f phút | Phí: %,.0f VNĐ)",
                mMainController.phiGiaoHang(khoangCachKm, "UuTien").thoigian,
                mMainController.phiGiaoHang(khoangCachKm, "UuTien").phi));

        rbStandard.setText(String.format(Locale.getDefault(), "Nhanh (Mặc định) (Thời gian: ~%.0f phút | Phí: %,.0f VNĐ)",
                mMainController.phiGiaoHang(khoangCachKm, "BinhThuong").thoigian,
                mMainController.phiGiaoHang(khoangCachKm, "BinhThuong").phi));

        rbEconomy.setText(String.format(Locale.getDefault(), "Tiết kiệm (Thời gian: ~%.0f phút | Phí: %,.0f VNĐ)",
                mMainController.phiGiaoHang(khoangCachKm, "TietKiem").thoigian,
                mMainController.phiGiaoHang(khoangCachKm, "TietKiem").phi));

        updateTongCong();
    }

    private void updateTongCong() {
        tvTongTamTinh.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", tongTienHang));

        double tongThanhToan = tongTienHang + phiGiaoHang - tongGiamGia;

        tvTongCongGiaTri.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", tongThanhToan));
    }

    @Override
    public void onProfileLoaded(String ten, String diaChi, boolean nhanThongBao) {
        if (diaChi != null && !diaChi.isEmpty()) {
            tvDiaChiNguoiDung.setText(diaChi);
        } else {
            tvDiaChiNguoiDung.setText("Vui lòng chọn địa chỉ giao hàng");
        }
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        userPhone = prefs.getString("USER_PHONE", "");
    }

    @Override
    public void onProfileLoadError(String message) {
        Toast.makeText(this, "Lỗi tải thông tin người dùng.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGioHangLoaded(List<monAn> danhSachMonAn, String maNhaHang) {
        danhSachMonAnTrongGio.clear();
        danhSachMonAnTrongGio.addAll(danhSachMonAn);

        tongTienHang = adapterGioHang.getCurrentTotal();
        adapterGioHang.notifyDataSetChanged();
        updateTongCong();

        mMainController.layDanhSachKhuyenMai(maNhaHang, this);
    }

    @Override
    public void onGioHangLoadError(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, "Lỗi tải giỏ hàng: " + message, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onGioHangCleared(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onKhuyenMaiLoaded(List<khuyenMai> danhSach) {
        if (danhSach.isEmpty()) {
            tvKhuyenMaiText.setText("Không có mã khuyến mãi khả dụng.");
        }
    }

    @Override
    public void onKhuyenMaiLoadError(String message) {
        Toast.makeText(this, "Lỗi tải khuyến mãi: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGiamGiaCalculated(double tongGiam, String maKhuyenMai) {
        this.tongGiamGia = tongGiam;
        this.maKhuyenMaiDaChon = maKhuyenMai;
        tvKhuyenMaiText.setText(String.format(Locale.getDefault(), "Áp dụng: -%,.0f VNĐ", tongGiam));
        updateTongCong();
        Toast.makeText(this, "Áp dụng mã " + maKhuyenMai + " thành công!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onApplyError(String message) {
        this.tongGiamGia = 0;
        this.maKhuyenMaiDaChon = null;
        tvKhuyenMaiText.setText("Áp dụng ưu đãi để được giảm giá");
        updateTongCong();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPaymentUrlReceived(String payUrl) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(payUrl));
        startActivityForResult(browserIntent, REQUEST_CODE_MOMO_PAYMENT);
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, "Lỗi thanh toán MoMo: " + message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_KHUYENMAI) {
                String selectedMaKhuyenMai = data.getStringExtra("SELECTED_MA_KM");
                this.tongGiamGia = data.getDoubleExtra("GIAM_GIA_TRI", 0);
                this.maKhuyenMaiDaChon = selectedMaKhuyenMai;
                onGiamGiaCalculated(tongGiamGia, maKhuyenMaiDaChon);
            } else if (requestCode == REQUEST_CODE_PAYMENT) {
                String paymentMethodName = data.getStringExtra("PAYMENT_METHOD_NAME");
                String paymentMethodId = data.getStringExtra("PAYMENT_METHOD_ID");

                if (paymentMethodName != null) {
                    tvPhuongThucThanhToan.setText(paymentMethodName);
                }
                if (paymentMethodId != null) {
                    this.phuongThucThanhToanDaChon = paymentMethodId;
                }
            }
        } else if (requestCode == REQUEST_CODE_MOMO_PAYMENT) {
            Toast.makeText(this, "Chờ xác nhận giao dịch MoMo...", Toast.LENGTH_LONG).show();
        }
    }
}
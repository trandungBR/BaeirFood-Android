package com.example.appgiaodoan.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgiaodoan.R;
import com.example.appgiaodoan.adapters.lichSuDonHangAdapter;
import com.example.appgiaodoan.controllers.mainControllers;
import com.example.appgiaodoan.models.lichSuDonHang;

import java.util.ArrayList;
import java.util.List;

public class lichsuhoatdong extends AppCompatActivity implements
        lichSuDonHangAdapter.OnLichSuItemClickListener,
        mainControllers.LichSuListener
{

    private LinearLayout tabTrangChu, tabHoatDong;
    private RecyclerView rvLichSu;
    private TextView tvEmpty;

    private lichSuDonHangAdapter adapter;
    private List<lichSuDonHang> dsLichSu = new ArrayList<>();

    private mainControllers mMainController;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lichsuhoatdong);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getString("USER_ID", null);
        mMainController = new mainControllers();

        tabTrangChu = findViewById(R.id.tabTrangChu);
        tabHoatDong = findViewById(R.id.tabHoatDong);
        rvLichSu = findViewById(R.id.rvLichSu);
        tvEmpty = findViewById(R.id.tvEmpty);

        adapter = new lichSuDonHangAdapter(this, dsLichSu, this);
        rvLichSu.setLayoutManager(new LinearLayoutManager(this));
        rvLichSu.setAdapter(adapter);

        tabTrangChu.setOnClickListener(v -> {
            Intent intent = new Intent(lichsuhoatdong.this, trangchu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        if (currentUserId != null) {
            mMainController.layLichSuDonHang(currentUserId, this);
        }
    }

    @Override
    public void onLichSuLoaded(List<lichSuDonHang> list) {
        dsLichSu.clear();
        dsLichSu.addAll(list);
        adapter.notifyDataSetChanged();

        if (list.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLichSuError(String message) {
        Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
    }

    // --- XỬ LÝ ĐẶT LẠI ---
    @Override
    public void onDatLaiClick(String idDonHang) {
        // Gọi Controller để lấy món ăn cũ, lưu vào giỏ hàng và chuyển trang
        mMainController.xuLyDatLai(this, idDonHang, new mainControllers.ReOrderViewListener() {
            @Override
            public void onReOrderSuccess(String idNhaHang) {
                Intent intent = new Intent(lichsuhoatdong.this, chitietdonhang.class);
                intent.putExtra("MA_NHA_HANG", idNhaHang);
                startActivity(intent);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(lichsuhoatdong.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- XỬ LÝ ĐÁNH GIÁ ---
    @Override
    public void onDanhGiaClick(String idDonHang) {
        // Cần ID nhà hàng để đánh giá.
        // Cách tốt nhất là lưu idNhaHang trong Model LichSuDonHang.
        // Tạm thời tôi sẽ tìm idNhaHang từ danh sách hiện tại.
        String idNhaHangTimDuoc = "";
        for(lichSuDonHang item : dsLichSu) {
            if(item.getIdDonHang().equals(idDonHang)) {
                // LƯU Ý: Bạn cần thêm field idNhaHang vào Model LichSuDonHang và cập nhật Database query
                // Hiện tại tôi giả định bạn sẽ làm bước đó. Nếu chưa, code này sẽ thiếu idNhaHang.
                // idNhaHangTimDuoc = item.getIdNhaHang();
                break;
            }
        }

        // Hiển thị Dialog đánh giá
        hienThiDialogDanhGia(idDonHang, "103db1f2-5356-427e-946f-74a8818bc64c"); // Test cứng ID quán nếu chưa cập nhật Model
    }

    private void hienThiDialogDanhGia(String idDonHang, String idNhaHang) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_danh_gia, null);
        builder.setView(view);

        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        EditText etNoidung = view.findViewById(R.id.etNoiDung);

        builder.setTitle("Đánh giá đơn hàng");
        builder.setPositiveButton("Gửi", (dialog, which) -> {
            int diem = (int) ratingBar.getRating();
            String noiDung = etNoidung.getText().toString();

            mMainController.xuLyDanhGiaTrucTiep(idDonHang, currentUserId, idNhaHang, diem, noiDung, new mainControllers.UpdateListener() {
                @Override
                public void onUpdateSuccess(String message) {
                    Toast.makeText(lichsuhoatdong.this, message, Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onUpdateError(String message) {
                    Toast.makeText(lichsuhoatdong.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}
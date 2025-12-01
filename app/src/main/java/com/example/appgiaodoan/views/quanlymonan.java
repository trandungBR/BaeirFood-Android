package com.example.appgiaodoan.views;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appgiaodoan.R;
import com.example.appgiaodoan.adapters.quanLyMonAnAdapter;
import com.example.appgiaodoan.controllers.mainControllers;
import com.example.appgiaodoan.models.monAn;
import com.example.appgiaodoan.models.quanAn;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class quanlymonan extends AppCompatActivity implements
        mainControllers.ChiTietNhaHangViewListener,
        mainControllers.QuanLyMonListener {

    private RecyclerView rvMonAn;
    private quanLyMonAnAdapter adapter;
    private List<monAn> dsMonAn = new ArrayList<>();
    private mainControllers mController;
    private String idNhaHang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quan_ly_mon_an);

        idNhaHang = getIntent().getStringExtra("ID_NHA_HANG");
        mController = new mainControllers();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvMonAn = findViewById(R.id.rvMonAnQuan);
        FloatingActionButton fab = findViewById(R.id.fabThemMon);
        adapter = new quanLyMonAnAdapter(this, dsMonAn, new quanLyMonAnAdapter.OnActionCallback() {
            @Override
            public void onEdit(monAn mon) {
                showDialogEdit(mon);
            }

            @Override
            public void onDelete(monAn mon) {
                showDialogDelete(mon);
            }
        });
        rvMonAn.setLayoutManager(new LinearLayoutManager(this));
        rvMonAn.setAdapter(adapter);

        fab.setOnClickListener(v -> showDialogAdd());

        if (idNhaHang != null) {
            loadData();
        }
    }

    private void loadData() {
        mController.layChiTietNhaHang(idNhaHang, this);
    }

    private void showDialogAdd() {
        hienThiDialogInput(null);
    }

    private void showDialogEdit(monAn mon) {
        hienThiDialogInput(mon);
    }

    private void hienThiDialogInput(monAn mon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(mon == null ? "Thêm món mới" : "Sửa món ăn");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_themmon, null);

        EditText etTen = view.findViewById(R.id.etTenMon);
        EditText etGia = view.findViewById(R.id.etGiaMon);
        EditText etMoTa = view.findViewById(R.id.etMoTaMon);
        EditText etLink = view.findViewById(R.id.etLinkAnh);

        if (mon != null) {
            etTen.setText(mon.getTenMonAn());
            etGia.setText(String.valueOf((int)mon.getGia()));
            etMoTa.setText(mon.getMoTa());
            etLink.setText(mon.getHinhAnh());
        }

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String ten = etTen.getText().toString();
            String giaStr = etGia.getText().toString();
            String moTa = etMoTa.getText().toString();
            String link = etLink.getText().toString();

            if (ten.isEmpty() || giaStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên và giá", Toast.LENGTH_SHORT).show();
                return;
            }

            double gia = Double.parseDouble(giaStr);

            if (mon == null) {
                mController.themMonMoi(idNhaHang, ten, gia, moTa, link, this);
            } else {
                mController.capNhatMon(mon.getIdMonAn(), ten, gia, moTa, link, this);
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showDialogDelete(monAn mon) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa món ăn?")
                .setMessage("Bạn có chắc muốn xóa " + mon.getTenMonAn() + "?")
                .setPositiveButton("Xóa", (d, w) -> mController.xoaMon(mon.getIdMonAn(), this))
                .setNegativeButton("Không", null)
                .show();
    }

    @Override
    public void hienThiChiTietNhaHang(quanAn nhaHang, List<monAn> danhSachMonAn) {
        dsMonAn.clear();
        if (danhSachMonAn != null) {
            dsMonAn.addAll(danhSachMonAn);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, "Lỗi tải dữ liệu: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActionSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        loadData();
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, "Lỗi thao tác: " + message, Toast.LENGTH_SHORT).show();
    }
}
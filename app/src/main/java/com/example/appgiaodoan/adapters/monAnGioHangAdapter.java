package com.example.appgiaodoan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appgiaodoan.R;
import com.example.appgiaodoan.models.monAn;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class monAnGioHangAdapter extends RecyclerView.Adapter<monAnGioHangAdapter.GioHangViewHolder> {

    public interface GioHangUpdateListener {
        void onQuantityChanged();
    }

    private Context mContext;
    private List<monAn> mDanhSachMonAn;
    private GioHangUpdateListener mListener;

    private HashMap<String, Integer> mQuantities;

    public monAnGioHangAdapter(Context context, List<monAn> danhSachMonAn, HashMap<String, Integer> initialQuantities) {
        this.mContext = context;
        this.mDanhSachMonAn = danhSachMonAn;
        this.mQuantities = initialQuantities;
    }

    public void setGioHangUpdateListener(GioHangUpdateListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public GioHangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_monan_giohang, parent, false);
        return new GioHangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GioHangViewHolder holder, int position) {
        monAn monAnHienTai = mDanhSachMonAn.get(position);

        int currentQuantity = mQuantities.getOrDefault(monAnHienTai.getIdMonAn(), 0);

        holder.tvTenMonAn.setText(monAnHienTai.getTenMonAn());
        holder.tvGiaTien.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", monAnHienTai.getGia()));
        holder.tvSoLuong.setText(String.valueOf(currentQuantity));

        Glide.with(mContext)
                .load(monAnHienTai.getHinhAnh())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.ivHinhAnhMon);

        holder.btnTang.setOnClickListener(v -> {
            int newQuantity = mQuantities.getOrDefault(monAnHienTai.getIdMonAn(), 0) + 1;
            mQuantities.put(monAnHienTai.getIdMonAn(), newQuantity);
            holder.tvSoLuong.setText(String.valueOf(newQuantity));
            if (mListener != null) {
                mListener.onQuantityChanged();
            }
        });

        holder.btnGiam.setOnClickListener(v -> {
            int current = mQuantities.getOrDefault(monAnHienTai.getIdMonAn(), 0);
            if (current > 0) {
                int newQuantity = current - 1;
                mQuantities.put(monAnHienTai.getIdMonAn(), newQuantity);
                holder.tvSoLuong.setText(String.valueOf(newQuantity));
                if (mListener != null) {
                    mListener.onQuantityChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDanhSachMonAn.size();
    }

    public HashMap<String, Integer> getQuantities() {
        return mQuantities;
    }

    public double getCurrentTotal() {
        double total = 0;
        for (monAn mon : mDanhSachMonAn) {
            total += mon.getGia() * mQuantities.getOrDefault(mon.getIdMonAn(), 0);
        }
        return total;
    }

    public class GioHangViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHinhAnhMon;
        TextView tvTenMonAn;
        TextView tvGiaTien;
        TextView tvSoLuong;
        ImageView btnTang;
        ImageView btnGiam;

        public GioHangViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHinhAnhMon = itemView.findViewById(R.id.ivHinhAnhMon);
            tvTenMonAn = itemView.findViewById(R.id.tvTenMonAn);
            tvGiaTien = itemView.findViewById(R.id.tvGiaTien);
            tvSoLuong = itemView.findViewById(R.id.tvSoLuong);
            btnTang = itemView.findViewById(R.id.btnTang);
            btnGiam = itemView.findViewById(R.id.btnGiam);
        }
    }
}
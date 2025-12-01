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
import com.example.appgiaodoan.models.lichSuDonHang;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class lichSuDonHangAdapter extends RecyclerView.Adapter<lichSuDonHangAdapter.LichSuViewHolder> {

    public interface OnLichSuItemClickListener {
        void onDatLaiClick(String idDonHang);
        void onDanhGiaClick(String idDonHang);
    }

    private Context mContext;
    private List<lichSuDonHang> mDanhSachDonHang;
    private OnLichSuItemClickListener mListener;

    public lichSuDonHangAdapter(Context context, List<lichSuDonHang> danhSachDonHang, OnLichSuItemClickListener listener) {
        this.mContext = context;
        this.mDanhSachDonHang = danhSachDonHang;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public LichSuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_lich_su_hoat_dong, parent, false);
        return new LichSuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LichSuViewHolder holder, int position) {
        lichSuDonHang item = mDanhSachDonHang.get(position);

        holder.tvTenQuanLS.setText(item.getTenQuan());
        holder.tvThoiGianLS.setText(item.getThoiGianDat());

        String formattedPrice = NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(item.getTongTien());
        holder.tvTongTienLS.setText(formattedPrice);

        Glide.with(mContext)
                .load(item.getHinhAnhQuan())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.ivHinhQuanLS);

        holder.btnDatLai.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onDatLaiClick(item.getIdDonHang());
            }
        });

        holder.btnDanhGia.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onDanhGiaClick(item.getIdDonHang());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDanhSachDonHang.size();
    }

    public static class LichSuViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHinhQuanLS;
        TextView tvTenQuanLS, tvThoiGianLS, tvTongTienLS;
        TextView btnDatLai, btnDanhGia;

        public LichSuViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHinhQuanLS = itemView.findViewById(R.id.ivHinhQuanLS);
            tvTenQuanLS = itemView.findViewById(R.id.tvTenQuanLS);
            tvThoiGianLS = itemView.findViewById(R.id.tvThoiGianLS);
            tvTongTienLS = itemView.findViewById(R.id.tvTongTienLS);
            btnDatLai = itemView.findViewById(R.id.btnDatLai);
            btnDanhGia = itemView.findViewById(R.id.btnDanhGia);
        }
    }
}
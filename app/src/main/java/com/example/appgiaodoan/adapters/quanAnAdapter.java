package com.example.appgiaodoan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar; // Import mới
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appgiaodoan.R;
import com.example.appgiaodoan.controllers.mainControllers; // Import Controller
import com.example.appgiaodoan.models.quanAn;

import java.util.List;

public class quanAnAdapter extends RecyclerView.Adapter<quanAnAdapter.quanAnViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(quanAn quanAn);
    }

    private Context mContext;
    private List<quanAn> mDanhSachQuanAn;
    private OnItemClickListener mListener;
    private mainControllers mController; // Khai báo Controller

    public quanAnAdapter(Context context, List<quanAn> danhSachQuanAn) {
        this.mContext = context;
        this.mDanhSachQuanAn = danhSachQuanAn;
        this.mController = new mainControllers(); // Khởi tạo Controller
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public quanAnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_quanan, parent, false);
        return new quanAnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull quanAnViewHolder holder, int position) {
        quanAn quanAnHienTai = mDanhSachQuanAn.get(position);

        holder.tvTenQuanAn.setText(quanAnHienTai.getTenNhaHang());
        holder.tvKhoangCach.setText(quanAnHienTai.getKhoangCach());
        if (quanAnHienTai.isTrangThai()) {
            mController.tinhDiemDanhGia(quanAnHienTai.getIdNhaHang(), (ratingTrungBinh, tongLuot) -> {
                if (holder.getAdapterPosition() == position) {
                    holder.ratingBar.setRating(ratingTrungBinh);
                    holder.tvSoLuotDanhGia.setText("(" + tongLuot + ")");
                    holder.ratingBar.setVisibility(View.VISIBLE);
                    holder.tvSoLuotDanhGia.setVisibility(View.VISIBLE);
                }
            });
            holder.itemView.setAlpha(1.0f);
            holder.itemView.setEnabled(true);

        } else {
            holder.ratingBar.setVisibility(View.GONE);
            holder.tvSoLuotDanhGia.setVisibility(View.VISIBLE);
            holder.tvSoLuotDanhGia.setText("ĐÃ ĐÓNG CỬA");
            holder.tvSoLuotDanhGia.setTextColor(android.graphics.Color.RED);
            holder.tvSoLuotDanhGia.setTypeface(null, android.graphics.Typeface.BOLD);
            holder.itemView.setAlpha(0.6f);
            holder.itemView.setEnabled(false);
        }

        Glide.with(mContext)
                .load(quanAnHienTai.getAnhDaiDien_URL())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.ivHinhAnhQA);

        holder.itemView.setOnClickListener(v -> {
            if (quanAnHienTai.isTrangThai()) {
                if (mListener != null) {
                    mListener.onItemClick(quanAnHienTai);
                }
            } else {
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDanhSachQuanAn.size();
    }

    public class quanAnViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHinhAnhQA;
        TextView tvTenQuanAn;
        TextView tvKhoangCach;

        // Các View mới cho Rating
        RatingBar ratingBar;
        TextView tvSoLuotDanhGia;

        public quanAnViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHinhAnhQA = itemView.findViewById(R.id.ivHinhAnhQA);
            tvTenQuanAn = itemView.findViewById(R.id.tvTenQuanAn);
            tvKhoangCach = itemView.findViewById(R.id.tvKhoangCach);

            // Ánh xạ mới
            ratingBar = itemView.findViewById(R.id.ratingBarItem);
            tvSoLuotDanhGia = itemView.findViewById(R.id.tvSoLuotDanhGia);
        }
    }
}
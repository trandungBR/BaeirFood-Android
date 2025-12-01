package com.example.appgiaodoan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.appgiaodoan.R;
import com.example.appgiaodoan.models.donHangTaiXe;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class donHangTaiXeAdapter extends RecyclerView.Adapter<donHangTaiXeAdapter.ViewHolder> {

    public interface OnOrderAcceptListener {
        void onAccept(donHangTaiXe donHang);
    }

    private Context context;
    private List<donHangTaiXe> list;
    private OnOrderAcceptListener listener;

    public donHangTaiXeAdapter(Context context, List<donHangTaiXe> list, OnOrderAcceptListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_don_hang_tai_xe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        donHangTaiXe item = list.get(position);
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        holder.tvTenQuan.setText(item.getTenQuan());
        holder.tvDiaChi.setText(item.getDiaChiQuan());
        holder.tvSoLuong.setText(item.getSoLuongMon() + " món");
        holder.tvKhoangCach.setText(String.format(Locale.US, "%.1f km", item.getKhoangCachKm()));
        holder.tvTongTienDon.setText("Tổng đơn: " + currencyVN.format(item.getTongTienDon()));
        holder.tvTienLoi.setText("+" + currencyVN.format(item.getTienLoi()));

        Glide.with(context).load(item.getHinhAnhQuan())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivHinhQuan);

        holder.btnNhan.setOnClickListener(v -> listener.onAccept(item));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHinhQuan;
        TextView tvTenQuan, tvDiaChi, tvSoLuong, tvKhoangCach, tvTienLoi, tvTongTienDon;
        Button btnNhan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHinhQuan = itemView.findViewById(R.id.ivHinhQuanTX);
            tvTenQuan = itemView.findViewById(R.id.tvTenQuanTX);
            tvDiaChi = itemView.findViewById(R.id.tvDiaChiQuanTX);
            tvSoLuong = itemView.findViewById(R.id.tvSoLuongMonTX);
            tvKhoangCach = itemView.findViewById(R.id.tvKhoangCachTX);
            tvTienLoi = itemView.findViewById(R.id.tvTienLoiTX);
            tvTongTienDon = itemView.findViewById(R.id.tvTongTienDonTX);
            btnNhan = itemView.findViewById(R.id.btnNhanDonTX);
        }
    }
}
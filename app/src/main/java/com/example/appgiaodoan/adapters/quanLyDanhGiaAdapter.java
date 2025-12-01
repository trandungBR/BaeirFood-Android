package com.example.appgiaodoan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appgiaodoan.R;
import com.example.appgiaodoan.models.danhGia;
import java.util.List;

public class quanLyDanhGiaAdapter extends RecyclerView.Adapter<quanLyDanhGiaAdapter.ViewHolder> {

    private Context context;
    private List<danhGia> list;

    public quanLyDanhGiaAdapter(Context context, List<danhGia> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_danh_gia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        danhGia item = list.get(position);
        holder.tvTen.setText(item.getTenNguoiDung());
        holder.ratingBar.setRating(item.getDiem());
        holder.tvThoiGian.setText(item.getThoiGian());
        holder.tvNoiDung.setText(item.getNoiDung());
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTen, tvThoiGian, tvNoiDung;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTen = itemView.findViewById(R.id.tvTenNguoiDungDG);
            ratingBar = itemView.findViewById(R.id.ratingBarItem);
            tvThoiGian = itemView.findViewById(R.id.tvThoiGianDG);
            tvNoiDung = itemView.findViewById(R.id.tvNoiDungDG);
        }
    }
}
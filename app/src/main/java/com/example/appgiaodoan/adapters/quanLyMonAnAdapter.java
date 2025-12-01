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
import java.util.List;

public class quanLyMonAnAdapter extends RecyclerView.Adapter<quanLyMonAnAdapter.ViewHolder> {

    public interface OnActionCallback {
        void onEdit(monAn mon);
        void onDelete(monAn mon);
    }

    private Context context;
    private List<monAn> list;
    private OnActionCallback callback;

    public quanLyMonAnAdapter(Context context, List<monAn> list, OnActionCallback callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quan_ly_mon_an, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        monAn item = list.get(position);
        holder.tvTen.setText(item.getTenMonAn());
        holder.tvGia.setText(String.format("%,.0fđ", item.getGia()));

        Glide.with(context).load(item.getHinhAnh()).placeholder(R.drawable.ic_launcher_background).into(holder.ivHinh);

        holder.btnEdit.setOnClickListener(v -> callback.onEdit(item));
        holder.btnDelete.setOnClickListener(v -> callback.onDelete(item));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHinh, btnEdit, btnDelete;
        TextView tvTen, tvGia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHinh = itemView.findViewById(R.id.ivHinhMon);
            tvTen = itemView.findViewById(R.id.tvTenMon);
            tvGia = itemView.findViewById(R.id.tvGiaMon);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
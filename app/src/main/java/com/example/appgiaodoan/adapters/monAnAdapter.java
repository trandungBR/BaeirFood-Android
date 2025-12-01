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
import com.example.appgiaodoan.models.monAn;

import java.util.List;

public class monAnAdapter extends RecyclerView.Adapter<monAnAdapter.MonAnViewHolder> {

    private final Context context;
    private final List<monAn> dsMonAn;
    private final MonAnListener listener;

    public interface MonAnListener {
        void onAddToCart(monAn monAn);
        void onSelect(monAn monAn);
    }

    public monAnAdapter(Context context, List<monAn> dsMonAn, MonAnListener listener) {
        this.context = context;
        this.dsMonAn = dsMonAn;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MonAnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mon_an, parent, false);
        return new MonAnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonAnViewHolder holder, int position) {
        monAn item = dsMonAn.get(position);
        holder.tvTenMonAn.setText(item.getTenMonAn());
        holder.tvMoTa.setText(item.getMoTa());

        // SỬA LỖI GIÁ: Sử dụng getGia() để đảm bảo lấy giaTien
        holder.tvGia.setText(item.getGia() + " đ");

        // SỬA LỖI HÌNH ẢNH: getHinhAnh() giờ trả về hinhAnh_URL
        String imageUrl = item.getHinhAnh();

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background) // Placeholder
                .error(R.drawable.ic_launcher_background) // Nếu URL lỗi/rỗng
                .into(holder.ivMonAn);

        holder.btnAddToCart.setOnClickListener(v -> {
            if (listener != null) listener.onAddToCart(item);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onSelect(item);
        });
    }

    @Override
    public int getItemCount() {
        return dsMonAn.size();
    }

    static class MonAnViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMonAn;
        TextView tvTenMonAn, tvMoTa, tvGia;
        Button btnAddToCart;

        public MonAnViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMonAn = itemView.findViewById(R.id.ivMonAn);
            tvTenMonAn = itemView.findViewById(R.id.tvTenMonAn);
            tvMoTa = itemView.findViewById(R.id.tvMoTa);
            tvGia = itemView.findViewById(R.id.tvGia);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
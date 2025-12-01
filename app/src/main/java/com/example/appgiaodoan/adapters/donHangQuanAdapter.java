package com.example.appgiaodoan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appgiaodoan.R;
import com.example.appgiaodoan.models.donHangQuan;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class donHangQuanAdapter extends RecyclerView.Adapter<donHangQuanAdapter.ViewHolder> {

    public interface OrderActionListener {
        void onUpdateStatus(String idDonHang, String newStatus);
    }

    private Context context;
    private List<donHangQuan> list;
    private OrderActionListener listener;

    public donHangQuanAdapter(Context context, List<donHangQuan> list, OrderActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_don_hang_quan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        donHangQuan item = list.get(position);

        holder.tvMaDon.setText("Đơn #" + item.getIdDonHang().substring(0, 5).toUpperCase());

        String thoiGian = item.getThoiGian().replace("T", " ").split("\\.")[0];
        holder.tvThoiGian.setText(thoiGian);

        holder.tvTrangThai.setText(item.getTrangThai());
        holder.tvTongTien.setText(NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(item.getTongTien()));

        // Xử lý hiển thị nút dựa trên trạng thái
        String status = item.getTrangThai();

        if ("Đang xử lý".equals(status)) {
            holder.llActions.setVisibility(View.VISIBLE);
            holder.btnPositive.setText("Nhận đơn");
            holder.btnPositive.setVisibility(View.VISIBLE);

            holder.btnNegative.setText("Từ chối");
            holder.btnNegative.setVisibility(View.VISIBLE);

            holder.btnPositive.setOnClickListener(v -> listener.onUpdateStatus(item.getIdDonHang(), "Đang chuẩn bị"));
            holder.btnNegative.setOnClickListener(v -> listener.onUpdateStatus(item.getIdDonHang(), "Đã hủy"));

        } else if ("Đang chuẩn bị".equals(status)) {
            holder.llActions.setVisibility(View.VISIBLE);
            holder.btnPositive.setText("Đã xong / Gọi ship");
            holder.btnPositive.setVisibility(View.VISIBLE);
            holder.btnNegative.setVisibility(View.GONE);
            holder.btnPositive.setOnClickListener(v -> listener.onUpdateStatus(item.getIdDonHang(), "Đang giao"));

        } else {
            holder.llActions.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMaDon, tvTrangThai, tvThoiGian, tvTongTien;
        Button btnPositive, btnNegative;
        View llActions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMaDon = itemView.findViewById(R.id.tvMaDon);
            tvTrangThai = itemView.findViewById(R.id.tvTrangThai);
            tvThoiGian = itemView.findViewById(R.id.tvThoiGian);
            tvTongTien = itemView.findViewById(R.id.tvTongTien);
            btnPositive = itemView.findViewById(R.id.btnNhanDon);
            btnNegative = itemView.findViewById(R.id.btnHuy);
            llActions = itemView.findViewById(R.id.llActions);
        }
    }
}
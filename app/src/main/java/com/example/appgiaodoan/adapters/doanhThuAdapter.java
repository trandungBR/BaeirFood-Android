package com.example.appgiaodoan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appgiaodoan.R;
import com.example.appgiaodoan.models.doanhThuMon;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class doanhThuAdapter extends RecyclerView.Adapter<doanhThuAdapter.ViewHolder> {
    private Context context;
    private List<doanhThuMon> list;

    public doanhThuAdapter(Context context, List<doanhThuMon> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doanh_thu_mon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        doanhThuMon item = list.get(position);
        holder.tvTenMon.setText(item.getTenMon());
        holder.tvSoLuong.setText("x" + item.getSoLuongBan());
        holder.tvTongTien.setText(NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(item.getTongTien()));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenMon, tvSoLuong, tvTongTien;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenMon = itemView.findViewById(R.id.tvTenMon);
            tvSoLuong = itemView.findViewById(R.id.tvSoLuong);
            tvTongTien = itemView.findViewById(R.id.tvTongTien);
        }
    }
}
package com.example.appgiaodoan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appgiaodoan.R;
import com.example.appgiaodoan.models.thuNhapTaiXe;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class thuNhapTaiXeAdapter extends RecyclerView.Adapter<thuNhapTaiXeAdapter.ViewHolder> {
    private Context context;
    private List<thuNhapTaiXe> list;

    public thuNhapTaiXeAdapter(Context context, List<thuNhapTaiXe> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_thu_nhap_tai_xe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        thuNhapTaiXe item = list.get(position);
        holder.tvTenQuan.setText(item.getTenQuan());
        holder.tvThoiGian.setText(item.getThoiGian().replace("T", " ").split("\\.")[0]);

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvTienLoi.setText("+" + nf.format(item.getTienLoi()));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenQuan, tvThoiGian, tvTienLoi;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenQuan = itemView.findViewById(R.id.tvTenQuan);
            tvThoiGian = itemView.findViewById(R.id.tvThoiGian);
            tvTienLoi = itemView.findViewById(R.id.tvTienLoi);
        }
    }
}
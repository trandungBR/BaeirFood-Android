package com.example.appgiaodoan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgiaodoan.R;
import com.example.appgiaodoan.models.khuyenMai;

import java.util.List;

public class khuyenMaiAdapter extends RecyclerView.Adapter<khuyenMaiAdapter.KhuyenMaiViewHolder> {

    public interface OnKhuyenMaiSelectedListener {
        void onSelected(khuyenMai selectedKhuyenMai);
    }

    private final Context mContext;
    private final List<khuyenMai> mDanhSachKhuyenMai;
    private final OnKhuyenMaiSelectedListener mListener;
    private int mSelectedPosition = RecyclerView.NO_POSITION;

    public khuyenMaiAdapter(Context context, List<khuyenMai> danhSachKhuyenMai, OnKhuyenMaiSelectedListener listener) {
        this.mContext = context;
        this.mDanhSachKhuyenMai = danhSachKhuyenMai;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public KhuyenMaiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_khuyenmai, parent, false);
        return new KhuyenMaiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KhuyenMaiViewHolder holder, int position) {
        khuyenMai item = mDanhSachKhuyenMai.get(position);

        holder.tvTenKhuyenMai.setText(item.getTenKhuyenMai());
        holder.tvMoTaKhuyenMai.setText(item.getMoTa());
        holder.radioSelected.setChecked(position == mSelectedPosition);

        holder.llItemKhuyenMai.setOnClickListener(v -> {
            int previousSelected = mSelectedPosition;
            mSelectedPosition = position;
            notifyItemChanged(previousSelected);
            notifyItemChanged(position);
            if (mListener != null) {
                mListener.onSelected(item);
            }
        });

        holder.radioSelected.setOnClickListener(v -> holder.llItemKhuyenMai.performClick());
    }

    @Override
    public int getItemCount() {
        return mDanhSachKhuyenMai.size();
    }

    public class KhuyenMaiViewHolder extends RecyclerView.ViewHolder {
        final ConstraintLayout llItemKhuyenMai;
        final TextView tvTenKhuyenMai;
        final TextView tvMoTaKhuyenMai;
        final RadioButton radioSelected;

        public KhuyenMaiViewHolder(@NonNull View itemView) {
            super(itemView);
            llItemKhuyenMai = (ConstraintLayout) itemView;
            tvTenKhuyenMai = itemView.findViewById(R.id.tvTenKhuyenMai);
            tvMoTaKhuyenMai = itemView.findViewById(R.id.tvMoTaKhuyenMai);
            radioSelected = itemView.findViewById(R.id.radioSelected);
        }
    }
}
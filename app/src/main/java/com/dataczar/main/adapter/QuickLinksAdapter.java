package com.dataczar.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.dataczar.R;
import com.dataczar.databinding.RowQuickLinksBinding;
import com.dataczar.main.activity.WebviewActivity;
import com.dataczar.main.model.QuickLinkData;
import com.dataczar.main.viewmodel.ClsCommon;

import java.util.ArrayList;

public class QuickLinksAdapter extends RecyclerView.Adapter<QuickLinksAdapter.DataViewHolder> {

    private ArrayList<QuickLinkData> list;

    public QuickLinksAdapter(ArrayList<QuickLinkData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowQuickLinksBinding binding;
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.row_quick_links, parent, false);
        return new QuickLinksAdapter.DataViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull QuickLinksAdapter.DataViewHolder holder, int position) {
        holder.mBinding.tvTitle.setText(list.get(position).getText());
        if (list.get(position).getText().contains("Started")){
            holder.mBinding.ivIcon.setImageResource(R.drawable.started_links);
        }
        if (list.get(position).getText().contains("View")){
            holder.mBinding.ivIcon.setImageResource(R.drawable.ic_preview_links);
        }

        if (list.get(position).getText().contains("Edit")){
            holder.mBinding.ivIcon.setImageResource(R.drawable.ic_edit_links);
        }

        if (list.get(position).getText().contains("Manage your Website")){
            holder.mBinding.ivIcon.setImageResource(R.drawable.ic_preview_links);
        }

        if (list.get(position).getText().contains("Shopping Cart")){
            holder.mBinding.ivIcon.setImageResource(R.drawable.ic_shop_links);
        }
        holder.mBinding.clMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = holder.mBinding.getRoot().getContext();
                context.startActivity(new Intent(context, WebviewActivity.class).putExtra(ClsCommon.WEBSITE, list.get(position).getText()).putExtra(ClsCommon.URL, list.get(position).getUrl()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        private RowQuickLinksBinding mBinding;

        public DataViewHolder(RowQuickLinksBinding mBinding) {
            super(mBinding.clMainView);
            this.mBinding = mBinding;
        }
    }
}

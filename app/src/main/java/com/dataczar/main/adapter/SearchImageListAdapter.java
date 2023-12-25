package com.dataczar.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dataczar.R;
import com.dataczar.databinding.RowImagesBinding;
import com.dataczar.main.listener.MyImageSelectionListener;
import com.dataczar.main.model.GetFreeImageListResponse;
import com.dataczar.main.model.GetMyImagesListResponse;
import com.dataczar.main.model.GetPostListResponse;

import java.util.ArrayList;

public class SearchImageListAdapter extends RecyclerView.Adapter<SearchImageListAdapter.DataViewHolder> {

    private ArrayList<GetFreeImageListResponse.ImageData> list;
    private MyImageSelectionListener myImageSelectionListener;

    public SearchImageListAdapter(MyImageSelectionListener myImageSelectionListener) {
      this.myImageSelectionListener=myImageSelectionListener;
        list=new ArrayList<>();
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowImagesBinding binding;
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.row_images, parent, false);
        return new SearchImageListAdapter.DataViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchImageListAdapter.DataViewHolder holder, int position) {
        GetFreeImageListResponse.ImageData data = list.get(position);

        Glide.with(holder.mBinding.getRoot().getContext())
                .load(data.getImage())
                .override(600,600)
                .centerInside()
                .into(holder.mBinding.ivImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myImageSelectionListener.selectedFreeImage(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        private RowImagesBinding mBinding;

        public DataViewHolder(RowImagesBinding mBinding) {
            super(mBinding.clMainView);
            this.mBinding = mBinding;
        }
    }

    public void add(GetFreeImageListResponse.ImageData data) {
        list.add(data);
        notifyItemInserted(list.size() - 1);
    }

    public void clearData() {
        if (list!=null && list.size()!=0){
            list.clear();
            notifyDataSetChanged();
        }
    }

    public void addAll(ArrayList<GetFreeImageListResponse.ImageData> dataArrayList) {
        for (GetFreeImageListResponse.ImageData result : dataArrayList) {
            add(result);
        }
    }
}

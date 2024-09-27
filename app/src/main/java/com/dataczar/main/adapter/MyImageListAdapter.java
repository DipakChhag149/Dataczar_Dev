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
import com.dataczar.databinding.RowPostBinding;
import com.dataczar.main.listener.MyImageSelectionListener;
import com.dataczar.main.model.GetMyImagesListResponse;
import com.dataczar.main.model.GetPostListResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyImageListAdapter extends RecyclerView.Adapter<MyImageListAdapter.DataViewHolder> {

    private ArrayList<GetMyImagesListResponse> list;
    private MyImageSelectionListener myImageSelectionListener;

    public MyImageListAdapter(ArrayList<GetMyImagesListResponse> mlist,MyImageSelectionListener myImageSelectionListener) {
      this.list=mlist;
      this.myImageSelectionListener=myImageSelectionListener;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowImagesBinding binding;
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.row_images, parent, false);
        return new MyImageListAdapter.DataViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyImageListAdapter.DataViewHolder holder, int position) {
        GetMyImagesListResponse data = list.get(position);

        Glide.with(holder.mBinding.getRoot().getContext())
                .load(data.getImage())
                .override(600,600)
                .centerInside()
                .into(holder.mBinding.ivImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myImageSelectionListener.selectedImage(data);
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
}

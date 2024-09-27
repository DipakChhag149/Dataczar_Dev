package com.dataczar.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dataczar.R;
import com.dataczar.databinding.RowHomeDataBinding;
import com.dataczar.databinding.RowPostBinding;
import com.dataczar.main.activity.CreatePostActivity;
import com.dataczar.main.activity.NewWebviewActivity;
import com.dataczar.main.activity.WebviewActivity;
import com.dataczar.main.listener.PostItemListener;
import com.dataczar.main.model.GetHomePageResponse;
import com.dataczar.main.model.GetPostListResponse;
import com.dataczar.main.viewmodel.ClsCommon;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomePageListAdapter extends RecyclerView.Adapter<HomePageListAdapter.DataViewHolder> {

    private ArrayList<GetHomePageResponse> list;
    public HomePageListAdapter(ArrayList<GetHomePageResponse> mList) {
        this.list=mList;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowHomeDataBinding binding;
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.row_home_data, parent, false);
        return new HomePageListAdapter.DataViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomePageListAdapter.DataViewHolder holder, int position) {
        GetHomePageResponse data = list.get(position);

        holder.mBinding.tvTitle.setText(data.getTitle());
        holder.mBinding.tvDescription.setHtml(data.getContent());
        Glide.with(holder.mBinding.getRoot().getContext())
                .load(data.getImage())
                .placeholder(R.drawable.ic_no_image)
                .error(R.drawable.ic_no_image)
                .centerInside()
                .into(holder.mBinding.ivImage);
        String date=data.getCreatedAt();
        SimpleDateFormat dateFormatprev = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        try {
            d = dateFormatprev.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String changedDate = dateFormat.format(d);
        holder.mBinding.tvDate.setText("Posted "+changedDate);


        holder.mBinding.tvSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.mBinding.getRoot().getContext(), NewWebviewActivity.class);
                intent.putExtra("HOME_PAGE", new Gson().toJson(data));
                holder.mBinding.getRoot().getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        private RowHomeDataBinding mBinding;

        public DataViewHolder(RowHomeDataBinding mBinding) {
            super(mBinding.clMainView);
            this.mBinding = mBinding;
        }
    }


}

package com.dataczar.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dataczar.R;
import com.dataczar.databinding.RowPostBinding;
import com.dataczar.main.activity.CreatePostActivity;
import com.dataczar.main.listener.PostItemListener;
import com.dataczar.main.model.GetPostListResponse;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.DataViewHolder> {

    private ArrayList<GetPostListResponse.PostData> list;
    private PostItemListener postItemListener;

    public PostListAdapter(PostItemListener listener) {
        list=new ArrayList<>();
        this.postItemListener=listener;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowPostBinding binding;
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.row_post, parent, false);
        return new PostListAdapter.DataViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostListAdapter.DataViewHolder holder, int position) {
        GetPostListResponse.PostData postData = list.get(position);
        holder.mBinding.tvPostTitle.setText(postData.getTitle());
        holder.mBinding.tvStatus.setText(postData.getStatus());
        holder.mBinding.tvDescription.setHtml(postData.getContent());

        Glide.with(holder.mBinding.getRoot().getContext())
                .load(postData.getImage())
                .override(600,600)
                .placeholder(R.drawable.ic_no_image)
                .error(R.drawable.ic_no_image)
                .centerInside()
                .into(holder.mBinding.ivPost);
        String date=postData.getCreatedAt();
        SimpleDateFormat dateFormatprev = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        try {
            d = dateFormatprev.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String changedDate = dateFormat.format(d);
        holder.mBinding.tvDate.setText(changedDate);

        holder.mBinding.ivArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context=holder.mBinding.getRoot().getContext();
                context.startActivity(new Intent(context, CreatePostActivity.class).putExtra("Is_edit","1").putExtra("POST_DATA",new Gson().toJson(list.get(position))));
            }
        });

        holder.mBinding.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postItemListener.deletePost(postData,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        private RowPostBinding mBinding;

        public DataViewHolder(RowPostBinding mBinding) {
            super(mBinding.clMainView);
            this.mBinding = mBinding;
        }
    }

    public void add(GetPostListResponse.PostData data) {
        list.add(data);
        notifyItemInserted(list.size() - 1);
    }
    public void delete(GetPostListResponse.PostData data,int position) {
        list.remove(data);
        notifyItemRemoved(position);
    }

    public void clearData() {
        list.clear();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<GetPostListResponse.PostData> dataArrayList) {
        for (GetPostListResponse.PostData result : dataArrayList) {
            add(result);
        }
    }
}

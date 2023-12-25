package com.dataczar.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dataczar.R;
import com.dataczar.databinding.RowEducationContentBinding;
import com.dataczar.databinding.RowImagesBinding;
import com.dataczar.main.model.GetEBooksListResponse;

import java.util.ArrayList;

public class OtherBookListAdapter extends RecyclerView.Adapter<OtherBookListAdapter.DataViewHolder> {

    private ArrayList<GetEBooksListResponse.Ebook> list;
    private EbookSelectionListener mListener;

    public OtherBookListAdapter(ArrayList<GetEBooksListResponse.Ebook> mList, EbookSelectionListener listener) {
      this.list=mList;
      this.mListener = listener;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowEducationContentBinding binding;
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.row_education_content, parent, false);
        return new OtherBookListAdapter.DataViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OtherBookListAdapter.DataViewHolder holder, int position) {
        GetEBooksListResponse.Ebook data = list.get(position);

        Glide.with(holder.mBinding.getRoot().getContext())
                .load(data.getImage())
                .override(600,600)
                .thumbnail(Glide.with(holder.mBinding.getRoot().getContext()).load(R.drawable.loader))
                .into(holder.mBinding.ivImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.openEbooks(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        private RowEducationContentBinding mBinding;

        public DataViewHolder(RowEducationContentBinding mBinding) {
            super(mBinding.clMainView);
            this.mBinding = mBinding;
        }
    }

    public interface EbookSelectionListener {
        void openEbooks(GetEBooksListResponse.Ebook ebook);
    }
}

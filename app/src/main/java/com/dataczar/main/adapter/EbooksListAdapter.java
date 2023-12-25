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
import com.dataczar.databinding.RowImagesEducationBinding;
import com.dataczar.main.listener.MyImageSelectionListener;
import com.dataczar.main.model.GetEBooksListResponse;
import com.dataczar.main.model.GetFreeImageListResponse;
import com.dataczar.main.model.GetMyImagesListResponse;

import java.util.ArrayList;

public class EbooksListAdapter extends RecyclerView.Adapter<EbooksListAdapter.DataViewHolder> {

    private ArrayList<GetEBooksListResponse.Ebook> list;
    private EbookSelectionListener mListener;

    public EbooksListAdapter(ArrayList<GetEBooksListResponse.Ebook> mList,EbookSelectionListener listener) {
      this.list=mList;
      this.mListener = listener;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowImagesEducationBinding binding;
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.row_images_education, parent, false);
        return new EbooksListAdapter.DataViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EbooksListAdapter.DataViewHolder holder, int position) {
        GetEBooksListResponse.Ebook data = list.get(position);

        String strImageURL = data.getImage();
        if (!strImageURL.contains("http")){
            strImageURL = "https:"+data.getImage();
        }
        Glide.with(holder.mBinding.getRoot().getContext())
                .load(strImageURL)
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
        private RowImagesEducationBinding mBinding;

        public DataViewHolder(RowImagesEducationBinding mBinding) {
            super(mBinding.clMainView);
            this.mBinding = mBinding;
        }
    }

    public interface EbookSelectionListener {
        void openEbooks(GetEBooksListResponse.Ebook ebook);
    }
}

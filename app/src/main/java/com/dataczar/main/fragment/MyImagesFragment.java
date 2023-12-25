package com.dataczar.main.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.dataczar.R;
import com.dataczar.databinding.MyImagesFragmentBinding;
import com.dataczar.main.adapter.MyImageListAdapter;
import com.dataczar.main.listener.MyImageSelectionListener;
import com.dataczar.main.model.GetFreeImageListResponse;
import com.dataczar.main.model.GetMyImagesListResponse;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyImagesFragment extends BottomSheetDialogFragment {

    RequestQueue requestQueue;
    private MyImagesFragmentBinding mBinding;
    private BottomSheetDialog bottomSheetDialog;
    private Context mContext;
    private String imageResponse;
    private MyImageSelectionListener mListener;

    public MyImagesFragment(Context context, String response, MyImageSelectionListener myImageSelectionListener) {
        this.mContext = context;
        this.imageResponse = response;
        this.mListener = myImageSelectionListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = MyImagesFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<GetMyImagesListResponse> getMyImagesListResponse = new Gson().fromJson(imageResponse, new TypeToken<ArrayList<GetMyImagesListResponse>>() {
        }.getType());
        if (getMyImagesListResponse != null && getMyImagesListResponse.size() != 0) {
            MyImageListAdapter myImageListAdapter = new MyImageListAdapter(getMyImagesListResponse, new MyImageSelectionListener() {
                @Override
                public void selectedImage(GetMyImagesListResponse imageData) {
                    mListener.selectedImage(imageData);
                    dismiss();
                }

                @Override
                public void selectedFreeImage(GetFreeImageListResponse.ImageData imageData) {
                    mListener.selectedFreeImage(imageData);
                    dismiss();
                }
            });
            mBinding.rvImages.setAdapter(myImageListAdapter);
            mBinding.rvImages.setVisibility(View.VISIBLE);
            mBinding.tvNoData.setVisibility(View.GONE);
        } else {
            mBinding.rvImages.setVisibility(View.GONE);
            mBinding.tvNoData.setVisibility(View.VISIBLE);
        }

        mBinding.ivClose.setOnClickListener(v -> dismiss());
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog mDialog = new BottomSheetDialog(getContext(), getTheme());
        bottomSheetDialog = mDialog;

        mDialog.setOnShowListener(dialog -> {
            mDialog.getBehavior().addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull @NotNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        mDialog.dismiss();
                    }
                }

                @Override
                public void onSlide(@NonNull @NotNull View bottomSheet, float slideOffset) {

                }
            });

            mDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        return mDialog;
    }
}
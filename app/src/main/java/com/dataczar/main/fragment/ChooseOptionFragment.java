package com.dataczar.main.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dataczar.R;
import com.dataczar.databinding.FragmentChooseOptionBinding;
import com.dataczar.main.listener.ChooseOptionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

public class ChooseOptionFragment extends BottomSheetDialogFragment {

    private FragmentChooseOptionBinding mBinding;
    private BottomSheetDialog bottomSheetDialog;
    private ChooseOptionListener mListener;

    public ChooseOptionFragment(ChooseOptionListener listener) {
        this.mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = FragmentChooseOptionBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.btnClose.setOnClickListener(v -> dismiss());

        mBinding.tvMyImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.selectOption("1");
                dismiss();
            }
        });

        mBinding.tvFreeImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.selectOption("2");
                dismiss();
            }
        });

        mBinding.tvPhotoLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.selectOption("3");
                dismiss();
            }
        });

        mBinding.tvTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.selectOption("4");
                dismiss();
            }
        });


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

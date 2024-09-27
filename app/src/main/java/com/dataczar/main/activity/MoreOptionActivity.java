package com.dataczar.main.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dataczar.R;
import com.dataczar.databinding.ActivityMoreOptionBinding;
import com.dataczar.main.viewmodel.ClsCommon;

public class MoreOptionActivity extends AppCompatActivity {

    private ActivityMoreOptionBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_more_option);

        mBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBinding.llLegal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iv = new Intent(MoreOptionActivity.this, WebviewLP.class);
                iv.putExtra(ClsCommon.WEBSITE, ClsCommon.LEGAL);
                startActivity(iv);
            }
        });


        mBinding.llDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iv = new Intent(MoreOptionActivity.this, DeleteAccountActivity.class);
                startActivity(iv);
            }
        });
    }
}
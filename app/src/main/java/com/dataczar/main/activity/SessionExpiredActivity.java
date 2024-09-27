package com.dataczar.main.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dataczar.R;
import com.dataczar.databinding.ActivitySessionExpiredBinding;
import com.dataczar.main.utils.AppUtils;

public class SessionExpiredActivity extends AppCompatActivity {

    private ActivitySessionExpiredBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_session_expired);

        mBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.clearData(SessionExpiredActivity.this);
                Intent iv = new Intent(SessionExpiredActivity.this, LoginActivity.class);
                iv.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                iv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(iv);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
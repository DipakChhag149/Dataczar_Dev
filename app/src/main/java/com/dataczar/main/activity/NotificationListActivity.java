package com.dataczar.main.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.dataczar.R;
import com.dataczar.databinding.ActivityNotificationListBinding;
import com.dataczar.main.fragment.NotificationFragment;

public class NotificationListActivity extends AppCompatActivity {

    private ActivityNotificationListBinding mBinding;
    FragmentTransaction fragmentTransaction = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_notification_list);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.llFargment, new NotificationFragment(this));
        fragmentTransaction.commit();
        mBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
package com.dataczar.main.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;

import com.dataczar.R;
import com.dataczar.databinding.ActivityRegistrationBinding;
import com.dataczar.main.model.RegisterFailResponse;
import com.dataczar.main.model.RegisterResponse;
import com.dataczar.main.viewmodel.ClsCommon;
import com.dataczar.main.viewmodel.network.NetworkUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegistrationActivity extends AppCompatActivity {

    private ActivityRegistrationBinding mBinding;
    ClsCommon clsCommon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        init();
    }

    private void init() {
        mBinding.ivBack.setOnClickListener(view -> finish());
        mBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBinding.edtName.getText().toString().isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "Please enter name", Toast.LENGTH_LONG).show();
                    return;
                }
                if (mBinding.edtEmail.getText().toString().isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "Please enter email", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!isValidEmail(mBinding.edtEmail.getText().toString())){
                    Toast.makeText(RegistrationActivity.this, "Please enter valid email", Toast.LENGTH_LONG).show();
                    return;
                }
                if (mBinding.edtPassword.getText().toString().isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "Please enter password", Toast.LENGTH_LONG).show();
                    return;
                }

                if (mBinding.edtConfirmPassword.getText().toString().isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "Please enter confirm password", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!mBinding.edtPassword.getText().toString().toLowerCase().equalsIgnoreCase(mBinding.edtConfirmPassword.getText().toString().toLowerCase())) {
                    Toast.makeText(RegistrationActivity.this, "Confirm password not match, Please check", Toast.LENGTH_LONG).show();
                    return;
                }

                if (NetworkUtil.isNetworkConnected(RegistrationActivity.this)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                registerUser();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            // new RegisterUser(RegistrationActivity.this, name,email,password,confirmPassword).execute();
                        }
                    });
                } else {
                    Toast.makeText(RegistrationActivity.this, "Please check network connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBinding.tvtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    void registerUser() throws IOException {
        String name = mBinding.edtName.getText().toString();
        String email = mBinding.edtEmail.getText().toString();
        String password = mBinding.edtPassword.getText().toString();
        String confirmPassword = mBinding.edtConfirmPassword.getText().toString();
        ProgressDialog pd;

        pd = new ProgressDialog(this, R.style.ProgressDialog);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (!pd.isShowing()) pd.show();
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("name", name)
                .add("password", password)
                .add("password_confirmation", confirmPassword)
                .build();
        Request request = new Request.Builder()
                .url(WSMethods.USER_REGISTER)
                .post(formBody)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                e.printStackTrace();
                Log.e("RES Fail",""+call.toString());
                if (pd.isShowing()) pd.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (pd.isShowing()) pd.dismiss();
                final String myResponse = response.body().string();
                Log.e("RES Donesss",""+myResponse);

                RegistrationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response != null && !response.equals("")) {
                            try {
                                JSONObject jsonResponse = new JSONObject(myResponse);
                                JSONObject jsonObject = jsonResponse.getJSONObject("response");
                                if(jsonObject.getBoolean("success")){
                                    RegisterResponse registerResponse = new Gson().fromJson(myResponse, RegisterResponse.class);
                                    if (registerResponse != null) {
                                        if (registerResponse.getResponse().getSuccess()){
                                            showSuccessDialog();
                                        }
                                    }
                                }else {
                                    RegisterFailResponse registerResponse = new Gson().fromJson(myResponse, RegisterFailResponse.class);
                                    if (registerResponse!=null){
                                        showFailDialog(registerResponse.getResponse().getMessages().getEmail().get(0));
                                    }
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                        } else {
                            Toast.makeText(RegistrationActivity.this, " Can't Connect to server.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }



    private void showSuccessDialog() {
        final Dialog dialog = new Dialog(RegistrationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.success_dialog);
        //dialog.getWindow().setFlags(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        AppCompatTextView tvMessage = dialog.findViewById(R.id.tvMessage);

        tvMessage.setText("You register successfully");
        AppCompatTextView tvOk = dialog.findViewById(R.id.tvOk);
        tvOk.setText("Thanks");
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onBackPressed();
            }
        });

        dialog.show();
    }

    private void showFailDialog(String msg) {
        final Dialog dialog = new Dialog(RegistrationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.success_dialog);
        AppCompatTextView tvTitle = dialog.findViewById(R.id.tvTitle);
        tvTitle.setText("Oops");
        AppCompatTextView tvMessage = dialog.findViewById(R.id.tvMessage);

        tvMessage.setText(msg);
        AppCompatTextView tvOk = dialog.findViewById(R.id.tvOk);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}
package com.dataczar.main.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.dataczar.BuildConfig;
import com.dataczar.R;
import com.dataczar.databinding.ActivityCreatePostBinding;
import com.dataczar.main.fragment.ChooseOptionFragment;
import com.dataczar.main.fragment.MyImagesFragment;
import com.dataczar.main.fragment.SearchImagesFragment;
import com.dataczar.main.listener.ChooseOptionListener;
import com.dataczar.main.listener.MyImageSelectionListener;
import com.dataczar.main.model.GetFreeImageListResponse;
import com.dataczar.main.model.GetMyImagesListResponse;
import com.dataczar.main.model.GetPostListResponse;
import com.dataczar.main.model.GetSignImageResponse;
import com.dataczar.main.utils.CustomHorizontalProgressBar;
import com.dataczar.main.utils.StringUtils;
import com.dataczar.main.viewmodel.ClsCommon;
import com.dataczar.main.viewmodel.network.NetworkUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.EasyImageConfig;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CreatePostActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    private ActivityCreatePostBinding mBinding;
    private File selectedImageFile;
    private String selectedImageURL = "";
    private String isEdit = "0";
    private GetPostListResponse.PostData postData;
    CustomHorizontalProgressBar horizontalProgress;
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_post);
        init();
    }

    private void init() {
        horizontalProgress = mBinding.getRoot().findViewById(R.id.horizontalProgress);

        mBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        isEdit = getIntent().getStringExtra("Is_edit");
        if (isEdit.equalsIgnoreCase("1")) {
            String strData = getIntent().getStringExtra("POST_DATA");
            postData = new Gson().fromJson(strData, GetPostListResponse.PostData.class);
            setData();
            mBinding.btnCreate.setText("Update Post");
        }
        requestQueue = Volley.newRequestQueue(this);

        mBinding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCreatePost();
            }
        });
        mBinding.btnChoose.setOnClickListener(v -> {
            ChooseOptionFragment chooseOptionBottomSheetFragment = new ChooseOptionFragment(new ChooseOptionListener() {
                @Override
                public void selectOption(String type) {
                    if (type.equalsIgnoreCase("1")) {
                        //My Images
                        if (NetworkUtil.isNetworkConnected(CreatePostActivity.this)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new getMyImageList(CreatePostActivity.this).execute();
                                }
                            });
                        } else {
                            Toast.makeText(CreatePostActivity.this, "Please check network connection", Toast.LENGTH_SHORT).show();
                        }


                    }

                    if (type.equalsIgnoreCase("2")) {
                        //Free Images
                        SearchImagesFragment searchImagesFragment = new SearchImagesFragment(CreatePostActivity.this, new MyImageSelectionListener() {
                            @Override
                            public void selectedImage(GetMyImagesListResponse imageData) {
                                selectedImageURL = imageData.getImage();
                                Glide.with(CreatePostActivity.this)
                                        .load(imageData.getImage())
                                        .into(mBinding.ivPostImage);
                            }

                            @Override
                            public void selectedFreeImage(GetFreeImageListResponse.ImageData imageData) {
                                selectedImageURL = imageData.getImage();
                                Glide.with(CreatePostActivity.this)
                                        .load(imageData.getImage())
                                        .into(mBinding.ivPostImage);
                            }

                        });
                        searchImagesFragment.show(getSupportFragmentManager(), "Search Images");
                    }

                    if (type.equalsIgnoreCase("3")) {
                        //Photo Library
                        selectImages();
                    }

                    if (type.equalsIgnoreCase("4")) {
                        // Take Photo
                        captureImages();
                    }
                }
            });
            chooseOptionBottomSheetFragment.show(getSupportFragmentManager(), "Choose");

        });


    }

    private void setData() {
        if (postData != null) {
            mBinding.edtTitle.setText(postData.getTitle());
            mBinding.edtContent.setText(postData.getContent());

            selectedImageURL = postData.getImage();
            Glide.with(CreatePostActivity.this)
                    .load(postData.getImage())
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(mBinding.ivPostImage);
        }
    }

    /**
     * Ask for gallery and camera permission in version 6 or above
     */
    private void askForPermission(int code) {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EasyImageConfig.REQ_TAKE_PICTURE:
                EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
                    @Override
                    public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                    }

                    @Override
                    public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                        File compressedImageFile = null;
                        try {
                            compressedImageFile = new Compressor(CreatePostActivity.this).setQuality(90).compressToFile(imageFile);
                            selectedImageFile = compressedImageFile;
                            selectedImageURL = selectedImageFile.getAbsolutePath();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Glide.with(CreatePostActivity.this)
                                .load(imageFile.getAbsolutePath())
                                .into(mBinding.ivPostImage);
                    }
                });
                break;

            case EasyImageConfig.REQ_PICK_PICTURE_FROM_GALLERY:
                EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
                    @Override
                    public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                    }

                    @Override
                    public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {

                        File compressedImageFile = null;
                        try {
                            compressedImageFile = new Compressor(CreatePostActivity.this).compressToFile(imageFile);
                            selectedImageFile = compressedImageFile;
                            selectedImageURL = selectedImageFile.getAbsolutePath();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Glide.with(CreatePostActivity.this)
                                .load(imageFile.getAbsolutePath())
                                .into(mBinding.ivPostImage);
                    }
                });
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case EasyImageConfig.REQ_TAKE_PICTURE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    EasyImage.openCamera(this, 1);
                } else {
                    Toast.makeText(this, "Please Grant Camera and Storage permission to continue", Toast.LENGTH_SHORT).show();
                }
            }

            case EasyImageConfig.REQ_PICK_PICTURE_FROM_GALLERY: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    EasyImage.openGallery(this, 1);
                } else {
                    Toast.makeText(this, "Please Grant Camera and Storage permission to continue", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void captureImages() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            askForPermission(EasyImageConfig.REQ_TAKE_PICTURE);
        } else {
            EasyImage.openCamera(this, 1);
        }
    }

    private void selectImages() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            askForPermission(EasyImageConfig.REQ_PICK_PICTURE_FROM_GALLERY);
        } else {
            EasyImage.openGallery(this, 1);
        }
    }

    private void submitCreatePost() {
        if (mBinding.edtTitle.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(CreatePostActivity.this, "Please enter title", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mBinding.edtContent.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(CreatePostActivity.this, "Please enter content", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedImageURL.equalsIgnoreCase("")) {
            Toast.makeText(CreatePostActivity.this, "Please choose Image", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedImageURL.contains("https")) {
            SharedPreferences prefs = getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
            String accountId = prefs.getString(ClsCommon.WEBSITE_ID, "");
            String title = mBinding.edtTitle.getText().toString();
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            String publish_time = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
            String publish_date = df.format(c);
            String category = "Option1";
            String content = mBinding.edtContent.getText().toString();
            String strURL = "";
            if (isEdit.equalsIgnoreCase("1")) {
                String strPostId = String.valueOf(postData.getId());
                strURL = "https://connect.dataczar.com/api/websites/" + accountId + "/posts/" + strPostId + "/update?title=" + title + "&publish_date=" + publish_date + "&publish_time=" + publish_time + "&category=" + category + "&status=live&content=" + content + "&editor=&image=" + selectedImageURL;
            } else {
                strURL = "https://connect.dataczar.com/api/websites/" + accountId + "/posts/create?title=" + title + "&publish_date=" + publish_date + "&publish_time=" + publish_time + "&category=" + category + "&status=live&content=" + content + "&editor=&image=" + selectedImageURL;
            }

            if (NetworkUtil.isNetworkConnected(CreatePostActivity.this)) {
                String finalStrURL = strURL;
                runOnUiThread(() -> {
                    if (isEdit.equalsIgnoreCase("1")) {
                        new updatePost(CreatePostActivity.this, finalStrURL).execute();
                    } else {
                        new createPost(CreatePostActivity.this, finalStrURL).execute();
                    }
                });
            } else {
                Toast.makeText(CreatePostActivity.this, "Please check network connection", Toast.LENGTH_SHORT).show();
            }
        } else {
            uploadImage();
        }

    }


    private void uploadImage() {
        SharedPreferences prefs = getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
        String accountId = prefs.getString(ClsCommon.ACCOUNT_ID, "");
        String strOne = getAlphaNumericString(4);
        String strTwo = getAlphaNumericString(4);
        String fileName = "IMG_" + strOne + "_" + strTwo;
        String strURL = "https://connect.dataczar.com/api/upload/signed?content_type=image/jpeg&folder=photos/" + accountId + "&name=" + fileName + ".jpg&sig=a2d09c7d76fced01f8be4b1f4cce8bec&pub=true;";

        Log.e("URL", "" + strURL);
        if (NetworkUtil.isNetworkConnected(CreatePostActivity.this)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new createSignForUpload(CreatePostActivity.this, strURL).execute();
                }
            });
        } else {
            Toast.makeText(CreatePostActivity.this, "Please check network connection", Toast.LENGTH_SHORT).show();
        }
    }

    private String getAlphaNumericString(int n) {

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }


    private void uploadImageData(GetSignImageResponse response) {
        String strURL = response.getAttributes().getAction();
        Log.e("URL", "" + strURL);
        horizontalProgress.setVisibility(View.VISIBLE);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        builder.addFormDataPart("key", response.getAttributes().getKey());
        builder.addFormDataPart("X-Amz-Credential", response.getAdditionalData().getXAmzCredential());
        builder.addFormDataPart("X-Amz-Algorithm", response.getAdditionalData().getXAmzAlgorithm());
        builder.addFormDataPart("X-Amz-Date", response.getAdditionalData().getXAmzDate());
        builder.addFormDataPart("X-Amz-Signature", response.getAdditionalData().getXAmzSignature());
        builder.addFormDataPart("Policy", response.getAdditionalData().getPolicy());
        builder.addFormDataPart("acl", "public-read");
        builder.addFormDataPart("Content-Type", "image/jpeg");

        addFileIntoBuilder(builder, selectedImageFile.getAbsolutePath());

        RequestBody requestBody = builder.build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .addHeader("Content-Type", "multipart/form-data")
                .url(strURL)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("Response Fail: ", "" + e.getMessage());
                horizontalProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                String responseCode = "";
                String responseMessage = "";
                JSONObject jsonObject = new JSONObject();
                horizontalProgress.setVisibility(View.GONE);
                try {
                    String responseJson = response.body().string();
                    Log.e("RES_CODE", "" + response.code());
                    Log.e("RESPONSE", responseJson);
                    jsonObject = new JSONObject(responseJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void addFileIntoBuilder(MultipartBody.Builder builder, String filePath) {
        File file = getFileFromPath(filePath);
        if (file != null && file.exists()) {
            MediaType MEDIA_TYPE = MediaType.parse(getMimeType(file.getAbsolutePath()));
            builder.addFormDataPart("file", "logo.png", RequestBody.create(MEDIA_TYPE, file));
        }
    }


    private File getFileFromPath(String imagePath) {
        if (!StringUtils.isNotEmpty(imagePath)) {
            return null;
        }
        return new File(imagePath);
    }

    private void showSuccessDialog() {
        final Dialog dialog = new Dialog(CreatePostActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.success_dialog);
        //dialog.getWindow().setFlags(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        AppCompatTextView tvMessage = dialog.findViewById(R.id.tvMessage);
        if (isEdit.equalsIgnoreCase("1")) {
            tvMessage.setText("Post is update successfully");
        } else {
            tvMessage.setText("Post is created successfully");
        }
        AppCompatTextView tvOk = dialog.findViewById(R.id.tvOk);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("create_post_update");
                intent.putExtra("message", "Create Post");
                LocalBroadcastManager.getInstance(CreatePostActivity.this).sendBroadcast(intent);
                dialog.dismiss();
                onBackPressed();
            }
        });

        dialog.show();
    }

    class getMyImageList extends AsyncTask<String, Void, Boolean> {

        public getMyImageList(Context context) {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            horizontalProgress.setVisibility(View.VISIBLE);

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, WSMethods.GET_MY_IMAGE_LIST,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (BuildConfig.DEBUG) {
                                Log.e("Response", "" + response);
                            }

                            horizontalProgress.setVisibility(View.GONE);
                            if (response != null && !response.isEmpty()) {
                                MyImagesFragment myImagesFragment = new MyImagesFragment(CreatePostActivity.this, response, new MyImageSelectionListener() {
                                    @Override
                                    public void selectedImage(GetMyImagesListResponse imageData) {
                                        selectedImageURL = imageData.getImage();
                                        Glide.with(CreatePostActivity.this)
                                                .load(imageData.getImage())
                                                .into(mBinding.ivPostImage);
                                    }

                                    @Override
                                    public void selectedFreeImage(GetFreeImageListResponse.ImageData imageData) {
                                        selectedImageURL = imageData.getImage();
                                        Glide.with(CreatePostActivity.this)
                                                .load(imageData.getImage())
                                                .into(mBinding.ivPostImage);
                                    }
                                });
                                myImagesFragment.show(getSupportFragmentManager(), "My Images");
                            } else {

                                Toast.makeText(CreatePostActivity.this, " Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    horizontalProgress.setVisibility(View.GONE);

                    Toast.makeText(CreatePostActivity.this, "Response Error: " + error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, getCookie());
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }

        public String getCookie() {
            SharedPreferences prefs = CreatePostActivity.this.getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
            String Cookie = prefs.getString(ClsCommon.COOKIE, "");
            return Cookie;
        }
    }

    class createSignForUpload extends AsyncTask<String, Void, Boolean> {
        private String url;

        public createSignForUpload(Context context, String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            horizontalProgress.setVisibility(View.VISIBLE);

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (BuildConfig.DEBUG) {
                                Log.e("Response", "" + response);
                            }

                            horizontalProgress.setVisibility(View.GONE);
                            if (response != null && !response.isEmpty()) {
                                GetSignImageResponse signImageResponse = new Gson().fromJson(response, GetSignImageResponse.class);
                                if (signImageResponse != null) {
                                    if (NetworkUtil.isNetworkConnected(CreatePostActivity.this)) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                uploadImageData(signImageResponse);
                                            }
                                        });
                                    } else {
                                        Toast.makeText(CreatePostActivity.this, "Please check network connection", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(CreatePostActivity.this, " Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    horizontalProgress.setVisibility(View.GONE);

                    Toast.makeText(CreatePostActivity.this, "Response Error: " + error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, getCookie());
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }

        public String getCookie() {
            SharedPreferences prefs = getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
            String Cookie = prefs.getString(ClsCommon.COOKIE, "");
            return Cookie;
        }
    }

    class createPost extends AsyncTask<String, Void, Boolean> {

        private String url;

        public createPost(Context context, String url) {

            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            horizontalProgress.setVisibility(View.VISIBLE);

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (BuildConfig.DEBUG) {
                                Log.e("Response", "" + response);
                            }

                            horizontalProgress.setVisibility(View.GONE);
                            if (response != null && !response.isEmpty()) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.has("website")) {
                                        showSuccessDialog();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(CreatePostActivity.this, " Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    horizontalProgress.setVisibility(View.GONE);

                    Toast.makeText(CreatePostActivity.this, "Response Error: " + error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, getCookie());
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }

        public String getCookie() {
            SharedPreferences prefs = getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
            String Cookie = prefs.getString(ClsCommon.COOKIE, "");
            return Cookie;
        }
    }


    class updatePost extends AsyncTask<String, Void, Boolean> {
        private String url;

        public updatePost(Context context, String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            horizontalProgress.setVisibility(View.VISIBLE);

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (BuildConfig.DEBUG) {
                                Log.e("Response", "" + response);
                            }

                            horizontalProgress.setVisibility(View.GONE);
                            if (response != null && !response.isEmpty()) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.has("website")) {
                                        showSuccessDialog();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(CreatePostActivity.this, " Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    horizontalProgress.setVisibility(View.GONE);
                    Toast.makeText(CreatePostActivity.this, "Response Error: " + error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, getCookie());
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }

        public String getCookie() {
            SharedPreferences prefs = getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
            String Cookie = prefs.getString(ClsCommon.COOKIE, "");
            return Cookie;
        }
    }
}
package com.dataczar.main.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dataczar.BuildConfig;
import com.dataczar.R;
import com.dataczar.databinding.FragmentSearchImageBinding;
import com.dataczar.main.adapter.SearchImageListAdapter;
import com.dataczar.main.listener.MyImageSelectionListener;
import com.dataczar.main.model.GetFreeImageListResponse;
import com.dataczar.main.model.GetMyImagesListResponse;
import com.dataczar.main.utils.PaginationScrollListener;
import com.dataczar.main.viewmodel.ClsCommon;
import com.dataczar.main.viewmodel.network.NetworkUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SearchImagesFragment extends BottomSheetDialogFragment {

    RequestQueue requestQueue;
    private FragmentSearchImageBinding mBinding;
    private BottomSheetDialog bottomSheetDialog;
    private Context mContext;
    private MyImageSelectionListener mListener;
    private int pageNumber = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private GridLayoutManager layoutManager;
    private SearchImageListAdapter myImageListAdapter;

    public SearchImagesFragment(Context context, MyImageSelectionListener mListener) {
        this.mContext = context;
        this.mListener = mListener;
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = FragmentSearchImageBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestQueue = Volley.newRequestQueue(getContext());
        mBinding.ivClose.setOnClickListener(v -> dismiss());
        if (getContext().getResources().getBoolean(R.bool.is_tablet)) {
            layoutManager = new GridLayoutManager(getContext(),4);
        }else {
            layoutManager = new GridLayoutManager(getContext(),3);
        }
        myImageListAdapter = new SearchImageListAdapter(new MyImageSelectionListener() {
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
        mBinding.rvImages.setLayoutManager(layoutManager);
        mBinding.rvImages.setAdapter(myImageListAdapter);
        mBinding.rvImages.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                pageNumber += 1;
                isLoading = true;
                loadNextPage();

            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        mBinding.ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.edtSearch.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Please enter search text", Toast.LENGTH_SHORT).show();
                } else {
                    if (NetworkUtil.isNetworkConnected(getContext())) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myImageListAdapter.clearData();
                                hideKeyboardFrom(getContext(), mBinding.getRoot());
                                String url="https://connect.dataczar.com/api/pixabay/search?search="+mBinding.edtSearch.getText().toString()+"&page="+pageNumber;
                                new getImageList(getContext(),url).execute();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
                    }
                }
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

    class getImageList extends AsyncTask<String, Void, Boolean> {
        ProgressDialog pd;
        String strURL;

        public getImageList(Context context,String url) {
            pd = new ProgressDialog(context, R.style.ProgressDialog);
            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            strURL=url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setCancelable(false);
            if (!pd.isShowing())
                pd.show();

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, strURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (BuildConfig.DEBUG) {
                                Log.e("Response", "" + response);
                            }

                            if (pd.isShowing())
                                pd.dismiss();
                            if (response != null && !response.isEmpty()) {
                                GetFreeImageListResponse getFreeImageListResponse=new Gson().fromJson(response,GetFreeImageListResponse.class);

                                if (getFreeImageListResponse.getImages() != null && getFreeImageListResponse.getImages().size() != 0) {
                                    myImageListAdapter.addAll(getFreeImageListResponse.getImages());
                                    isLastPage = false;
                                    isLoading = false;

                                } else {
                                    isLastPage = true;
                                    isLoading = false;
                                    mBinding.rvImages.setVisibility(View.GONE);
                                    mBinding.tvNoData.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(getContext(), " Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (pd != null && pd.isShowing())
                        pd.dismiss();

                    Toast.makeText(getContext(), "Response Error: " + error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
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
            SharedPreferences prefs = getContext().getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
            String Cookie = prefs.getString(ClsCommon.COOKIE, "");
            return Cookie;
        }
    }



    class getImageListSecond extends AsyncTask<String, Void, Boolean> {
        ProgressDialog pd;
        String strURL;

        public getImageListSecond(Context context,String url) {
            pd = new ProgressDialog(context, R.style.ProgressDialog);
            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            strURL=url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setCancelable(false);
            if (!pd.isShowing())
                pd.show();

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, strURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (BuildConfig.DEBUG) {
                                Log.e("Response", "" + response);
                            }

                            if (pd.isShowing())
                                pd.dismiss();
                            if (response != null && !response.isEmpty()) {
                                GetFreeImageListResponse getFreeImageListResponse=new Gson().fromJson(response,GetFreeImageListResponse.class);

                                if (getFreeImageListResponse.getImages() != null && getFreeImageListResponse.getImages().size() != 0) {
                                    myImageListAdapter.addAll(getFreeImageListResponse.getImages());

                                    isLastPage = false;
                                    isLoading = false;

                                } else {
                                    isLastPage = true;
                                    isLoading = false;
                                }
                            } else {
                                Toast.makeText(getContext(), " Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (pd != null && pd.isShowing())
                        pd.dismiss();

                    Toast.makeText(getContext(), "Response Error: " + error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
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
            SharedPreferences prefs = getContext().getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
            String Cookie = prefs.getString(ClsCommon.COOKIE, "");
            return Cookie;
        }
    }

    private void loadNextPage() {
        hideKeyboardFrom(getContext(), mBinding.getRoot());
        String url="https://connect.dataczar.com/api/pixabay/search?search="+mBinding.edtSearch.getText().toString()+"&page="+pageNumber;
        if (NetworkUtil.isNetworkConnected(getContext())) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new getImageListSecond(getContext(),url).execute();
                }
            });
        } else {
            Toast.makeText(getContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
        }
    }
}
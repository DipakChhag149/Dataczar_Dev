package com.dataczar.main.fragment;

import static com.dataczar.main.utils.AppUtils.getCookie;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dataczar.BuildConfig;
import com.dataczar.R;
import com.dataczar.databinding.FragmentAddPostNewBinding;
import com.dataczar.main.activity.CreatePostActivity;
import com.dataczar.main.activity.Dashboard;
import com.dataczar.main.adapter.PostListAdapter;
import com.dataczar.main.listener.PostItemListener;
import com.dataczar.main.model.GetPostListResponse;
import com.dataczar.main.utils.AppUtils;
import com.dataczar.main.utils.CustomHorizontalProgressBar;
import com.dataczar.main.utils.PaginationScrollListener;
import com.dataczar.main.viewmodel.ClsCommon;
import com.dataczar.main.viewmodel.network.NetworkUtil;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddPostNewFragment extends Fragment {
    RequestQueue requestQueue;
    ClsCommon clsCommon;
    private FragmentAddPostNewBinding mBinding;
    private String url = "";
    private int pageNumber = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private LinearLayoutManager layoutManager;
    private PostListAdapter postListAdapter;
    CustomHorizontalProgressBar horizontalProgress;
    public AddPostNewFragment(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_post_new, container, false);
        init();
        return mBinding.getRoot();
    }


    private void init() {
        clsCommon = new ClsCommon(requireContext());

        String websiteId = AppUtils.getStringValue(requireContext(),ClsCommon.WEBSITE_ID);

        url = "https://connect.dataczar.com/api/websites/" + websiteId + "/posts-api?page=" + pageNumber;
        horizontalProgress = mBinding.getRoot().findViewById(R.id.horizontalProgress);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        postListAdapter = new PostListAdapter(new PostItemListener() {
            @Override
            public void deletePost(GetPostListResponse.PostData data,int position) {
               showDeleteDialog(data,position);

            }
        });
        mBinding.rvPost.setLayoutManager(layoutManager);
        mBinding.rvPost.setAdapter(postListAdapter);
        mBinding.rvPost.addOnScrollListener(new PaginationScrollListener(layoutManager) {
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

        loadFirstPage();

        mBinding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CreatePostActivity.class).putExtra("Is_edit","0"));
            }
        });

        ((Dashboard) getActivity()).setFragmentRefreshListener(new Dashboard.FragmentRefreshListener() {
            @Override
            public void onRefresh() {
                pageNumber = 1;
                isLoading = false;
                isLastPage = false;
                if (postListAdapter != null) {
                    postListAdapter.clearData();
                }
                String websiteId = AppUtils.getStringValue(requireContext(),ClsCommon.WEBSITE_ID);
                url = "https://connect.dataczar.com/api/websites/" + websiteId + "/posts-api?page=" + pageNumber;
                loadFirstPage();
            }
        });
    }

    private void loadFirstPage() {
        if (NetworkUtil.isNetworkConnected(getContext())) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new getPostList(getContext()).execute();
                }
            });
        } else {
            Toast.makeText(getContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadNextPage() {
        if (NetworkUtil.isNetworkConnected(getContext())) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new getPostList(getContext()).execute();
                }
            });
        } else {
            Toast.makeText(getContext(), "Please check network connection", Toast.LENGTH_SHORT).show();
        }
    }

    class getPostList extends AsyncTask<String, Void, Boolean> {

        public getPostList(Context context) {
            horizontalProgress.setVisibility(View.VISIBLE);
//            pd = new ProgressDialog(context, R.style.ProgressDialog);
//            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pd.setCancelable(false);
//            if (!pd.isShowing())
//                pd.show();
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
                                Log.e("URL", "" + url);
                                Log.e("Response", "" + response);
                            }
                            horizontalProgress.setVisibility(View.INVISIBLE);
//
//                            if (pd.isShowing())
//                                pd.dismiss();

                            if (response != null && !response.isEmpty() && !response.contains("<!DOCTYPE html>")) {
                                GetPostListResponse getPostListResponse = new Gson().fromJson(response, GetPostListResponse.class);
                                if (getPostListResponse != null) {
                                    if (getPostListResponse.getPosts().getData() != null && getPostListResponse.getPosts().getData().size() != 0) {
                                        postListAdapter.addAll(getPostListResponse.getPosts().getData());
                                    }

                                    if (getPostListResponse.getPosts().getLastPage() == pageNumber) {
                                        isLastPage = true;
                                        isLoading = false;
                                    } else {
                                        url = getPostListResponse.getPosts().getNextPageUrl();
                                        isLastPage = false;
                                        isLoading = false;

                                    }
                                } else {
                                    Toast.makeText(getContext(), " Can't Connect to server.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getContext(), " Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    if (pd != null && pd.isShowing())
//                        pd.dismiss();
                    horizontalProgress.setVisibility(View.INVISIBLE);

                    Toast.makeText(getContext(), "Response Error: " + error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, getCookie(requireContext()));
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }
    }

    private void showDeleteDialog(GetPostListResponse.PostData data,int position) {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.delete_dialog);
        //dialog.getWindow().setFlags(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        AppCompatTextView tvNo = dialog.findViewById(R.id.tvNo);
        AppCompatTextView tvYes = dialog.findViewById(R.id.tvYes);
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              dialog.dismiss();
            }
        });

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if(!clsCommon.checkConnection())
                {
                    clsCommon.showSnackBar(false, mBinding.getRoot());
                }else {
                    String websiteId = AppUtils.getStringValue(requireContext(),ClsCommon.WEBSITE_ID);
                    String url = "https://connect.dataczar.com/api/websites/"+websiteId+"/posts/"+data.getId()+"/delete";
                    new deletePost(url,position,data).execute();
                }
            }
        });

        dialog.show();
    }

    class deletePost extends AsyncTask<String, Void, Boolean> {
        private String url;
        private int position;
        private GetPostListResponse.PostData deleteData;

        public deletePost(String url,int position,GetPostListResponse.PostData data) {
            this.url = url;
            this.position=position;
            this.deleteData=data;
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
            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
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
                                    if (jsonObject.has("status")) {
                                        if (jsonObject.getString("status").equals("success")){
                                            postListAdapter.delete(deleteData,position);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(requireContext(), " Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    horizontalProgress.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Response Error: " + error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, getCookie(requireContext()));
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }
    }

}

package com.dataczar.main.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
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
        SharedPreferences prefs = getActivity().getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
        String websiteId = prefs.getString(ClsCommon.WEBSITE_ID, "");

        url = "https://connect.dataczar.com/api/websites/" + websiteId + "/posts-api?page=" + pageNumber;
        horizontalProgress = mBinding.getRoot().findViewById(R.id.horizontalProgress);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        postListAdapter = new PostListAdapter(new PostItemListener() {
            @Override
            public void deletePost(GetPostListResponse.PostData data,int position) {
                SharedPreferences prefs = getActivity().getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
                String websiteId = prefs.getString(ClsCommon.WEBSITE_ID, "");
                String url = "https://connect.dataczar.com/api/websites/"+websiteId+"/posts/"+data.getId()+"/delete";
                new deletePost(url,position,data).execute();
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
                SharedPreferences prefs = getActivity().getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
                String websiteId = prefs.getString(ClsCommon.WEBSITE_ID, "");
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

    public String getCookie() {
        SharedPreferences prefs = getActivity().getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
        String Cookie = prefs.getString(ClsCommon.COOKIE, "");
        return Cookie;
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

                            if (response != null && !response.isEmpty()) {
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
                    params.put(ClsCommon.COOKIE, getCookie());
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }
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
                                           /* pageNumber = 1;
                                            isLoading = false;
                                            isLastPage = false;
                                            if (postListAdapter != null) {
                                                postListAdapter.clearData();
                                            }
                                            SharedPreferences prefs = getActivity().getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
                                            String websiteId = prefs.getString(ClsCommon.WEBSITE_ID, "");
                                            url = "https://connect.dataczar.com/api/websites/" + websiteId + "/posts-api?page=" + pageNumber;
                                            loadFirstPage();*/
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
                    params.put(ClsCommon.COOKIE, getCookie());
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }

        public String getCookie() {
            SharedPreferences prefs =getActivity().getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
            String Cookie = prefs.getString(ClsCommon.COOKIE, "");
            return Cookie;
        }
    }

}

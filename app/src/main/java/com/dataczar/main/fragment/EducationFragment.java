package com.dataczar.main.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dataczar.R;
import com.dataczar.databinding.FragmentEducationBinding;
import com.dataczar.main.activity.EBookDetailsActivity;
import com.dataczar.main.activity.WSMethods;
import com.dataczar.main.adapter.EbooksListAdapter;
import com.dataczar.main.model.GetEBooksListResponse;
import com.dataczar.main.viewmodel.ClsCommon;
import com.google.android.material.badge.BadgeDrawable;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EducationFragment extends Fragment {

    private FragmentEducationBinding mBinding;
    RequestQueue requestQueue;
    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = FragmentEducationBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestQueue = Volley.newRequestQueue(requireContext());
        new getEbookListAPICall().execute();
    }



    class getEbookListAPICall extends AsyncTask<String, Void, Boolean> {
        public getEbookListAPICall() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBinding.horizontalProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Log.d("Method Call", "getHomeData");
            StringRequest stringRequest = new StringRequest(Request.Method.GET, WSMethods.GET_EBOOKS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mBinding.horizontalProgress.setVisibility(View.INVISIBLE);

                            if (response != null && !response.isEmpty()) {
                                try {
                                    Log.e("EBOOK",""+response);
                                    GetEBooksListResponse eBooksListResponse=new Gson().fromJson(response,GetEBooksListResponse.class);

                                    if (eBooksListResponse!=null){
                                        if (eBooksListResponse.getMyEbooks()!=null && eBooksListResponse.getMyEbooks().size()!=0){
                                            mBinding.tvMyContent.setVisibility(View.VISIBLE);
                                            EbooksListAdapter myBookAdapter = new EbooksListAdapter(eBooksListResponse.getMyEbooks(),new EbooksListAdapter.EbookSelectionListener(){
                                                @Override
                                                public void openEbooks(GetEBooksListResponse.Ebook ebook) {
                                                    Intent intent=new Intent(requireContext(), EBookDetailsActivity.class);
                                                    intent.putExtra("IS_MY",true);
                                                    intent.putExtra("BOOK_DATA_LIST",response);
                                                    intent.putExtra("BOOK_DATA",new Gson().toJson(ebook));
                                                    startActivity(intent);
                                                }
                                            });
                                            mBinding.rvMyContent.setAdapter(myBookAdapter);
                                        }else {
                                            mBinding.tvMyContent.setVisibility(View.GONE);
                                        }

                                        if (eBooksListResponse.getEbooks()!=null && eBooksListResponse.getEbooks().size()!=0){
                                            mBinding.tvAllContent.setVisibility(View.VISIBLE);
                                            EbooksListAdapter eBookAdapter = new EbooksListAdapter(eBooksListResponse.getEbooks(),new EbooksListAdapter.EbookSelectionListener(){
                                                @Override
                                                public void openEbooks(GetEBooksListResponse.Ebook ebook) {
                                                    Intent intent=new Intent(requireContext(), EBookDetailsActivity.class);
                                                    intent.putExtra("IS_MY",false);
                                                    intent.putExtra("BOOK_DATA_LIST",response);
                                                    intent.putExtra("BOOK_DATA",new Gson().toJson(ebook));
                                                    startActivity(intent);
                                                }
                                            });
                                            mBinding.rvAllContent.setAdapter(eBookAdapter);
                                        }else {
                                            mBinding.tvAllContent.setVisibility(View.GONE);
                                        }
                                    }else {
                                        mBinding.tvMyContent.setVisibility(View.GONE);
                                        mBinding.tvAllContent.setVisibility(View.GONE);
                                    }
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }
                            } else {
                                Toast.makeText(requireContext(), " Can't Connect to server.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mBinding.horizontalProgress.setVisibility(View.INVISIBLE);

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
    }

    public String getCookie() {
        SharedPreferences prefs = getActivity().getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
        return prefs.getString(ClsCommon.COOKIE, "");
    }
}

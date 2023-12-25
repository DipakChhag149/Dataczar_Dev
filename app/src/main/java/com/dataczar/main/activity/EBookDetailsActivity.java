package com.dataczar.main.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.dataczar.R;
import com.dataczar.databinding.ActivityEbookDetailsBinding;
import com.dataczar.main.adapter.EbooksListAdapter;
import com.dataczar.main.adapter.OtherBookListAdapter;
import com.dataczar.main.model.GetEBooksListResponse;
import com.dataczar.main.viewmodel.ClsCommon;
import com.google.gson.Gson;

import java.util.ArrayList;

public class EBookDetailsActivity extends AppCompatActivity {

    private ActivityEbookDetailsBinding mBinding;
    private boolean isMyBook = false;
    private GetEBooksListResponse booksListResponse;
    private GetEBooksListResponse.Ebook bookData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_ebook_details);
        init();
    }

    private void init(){
        mBinding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        isMyBook = getIntent().getBooleanExtra("IS_MY",false);
        String strDataList = getIntent().getStringExtra("BOOK_DATA_LIST");
        String strData = getIntent().getStringExtra("BOOK_DATA");
        booksListResponse=new Gson().fromJson(strDataList,GetEBooksListResponse.class);
        bookData=new Gson().fromJson(strData,GetEBooksListResponse.Ebook.class);

        setBookData(bookData);
        setOtherBook();
    }

    private void setBookData(GetEBooksListResponse.Ebook ebook){
        Glide.with(this)
                .load(ebook.getImage())
                .centerInside()
                .into(mBinding.ivBook);

        mBinding.tvName.setText(ebook.getDescription());
        mBinding.tvDescription.setText(ebook.getDescription());
        if (!ebook.getPurchased()){
            mBinding.btnBuy.setText("BUY | $"+ebook.getDisplayPrice());

            mBinding.btnBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(EBookDetailsActivity.this,WebviewActivity.class);
                    intent.putExtra(ClsCommon.URL, ebook.getRoute());
                    intent.putExtra(ClsCommon.WEBSITE, "");
                    startActivity(intent);
                }
            });
        }else {
            mBinding.btnBuy.setText("Read");

            mBinding.btnBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ebook.getPath()));
                    startActivity(browserIntent);
                }
            });
        }
    }

    private void setOtherBook(){
        ArrayList<GetEBooksListResponse.Ebook> tempList = new ArrayList<>();
        if (isMyBook){
            tempList = booksListResponse.getEbooks();
            tempList.remove(bookData);
        }else {
            tempList = booksListResponse.getEbooks();
            tempList.remove(bookData);
        }
        OtherBookListAdapter otherBookAdapter = new OtherBookListAdapter(tempList,new OtherBookListAdapter.EbookSelectionListener(){
            @Override
            public void openEbooks(GetEBooksListResponse.Ebook ebook) {
                setBookData(ebook);
            }
        });
        mBinding.rvOtherBook.setAdapter(otherBookAdapter);
    }
}
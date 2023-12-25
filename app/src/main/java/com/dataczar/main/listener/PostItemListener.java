package com.dataczar.main.listener;

import com.dataczar.main.model.GetPostListResponse;

public interface PostItemListener {
    void deletePost(GetPostListResponse.PostData data,int position);
}

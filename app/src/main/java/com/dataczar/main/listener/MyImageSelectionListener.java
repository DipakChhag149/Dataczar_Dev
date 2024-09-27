package com.dataczar.main.listener;

import com.dataczar.main.model.GetFreeImageListResponse;
import com.dataczar.main.model.GetMyImagesListResponse;

public interface MyImageSelectionListener {
    void selectedImage(GetMyImagesListResponse imageData);

    void selectedFreeImage(GetFreeImageListResponse.ImageData imageData);
}

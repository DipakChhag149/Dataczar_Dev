package com.dataczar.main.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetFreeImageListResponse {

    @SerializedName("images")
    @Expose
    private ArrayList<ImageData> images;
    @SerializedName("image_count")
    @Expose
    private Integer imageCount;

    public ArrayList<ImageData> getImages() {
        return images;
    }

    public void setImages(ArrayList<ImageData> images) {
        this.images = images;
    }

    public Integer getImageCount() {
        return imageCount;
    }

    public void setImageCount(Integer imageCount) {
        this.imageCount = imageCount;
    }

    public class ImageData {

        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("large_preview")
        @Expose
        private String largePreview;
        @SerializedName("preview")
        @Expose
        private String preview;


        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getLargePreview() {
            return largePreview;
        }

        public void setLargePreview(String largePreview) {
            this.largePreview = largePreview;
        }

        public String getPreview() {
            return preview;
        }

        public void setPreview(String preview) {
            this.preview = preview;
        }

    }

}

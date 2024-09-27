package com.dataczar.main.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetMyImagesListResponse {
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("modified")
    @Expose
    private Integer modified;
    @SerializedName("extension")
    @Expose
    private String extension;
    @SerializedName("filename")
    @Expose
    private String filename;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("created")
    @Expose
    private String created;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getModified() {
        return modified;
    }

    public void setModified(Integer modified) {
        this.modified = modified;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}

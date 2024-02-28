package com.dataczar.main.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetInfoResponse {
    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("response")
    @Expose
    private ResponseData response;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ResponseData getResponse() {
        return response;
    }

    public void setResponse(ResponseData response) {
        this.response = response;
    }

    public class ResponseData {

        @SerializedName("success")
        @Expose
        private Boolean success;

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

    }
}

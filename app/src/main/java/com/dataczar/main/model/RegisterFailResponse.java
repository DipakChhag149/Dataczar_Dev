package com.dataczar.main.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RegisterFailResponse {
    @SerializedName("response")
    @Expose
    private ResponseFailData response;

    public ResponseFailData getResponse() {
        return response;
    }

    public void setResponse(ResponseFailData response) {
        this.response = response;
    }

    public class ResponseFailData {

        @SerializedName("success")
        @Expose
        private Boolean success;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("messages")
        @Expose
        private Messages messages;

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Messages getMessages() {
            return messages;
        }

        public void setMessages(Messages messages) {
            this.messages = messages;
        }

    }

    public class Messages {

        @SerializedName("email")
        @Expose
        private List<String> email;

        public List<String> getEmail() {
            return email;
        }

        public void setEmail(List<String> email) {
            this.email = email;
        }

    }
}

package com.dataczar.main.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetSignImageResponse {
    @SerializedName("attributes")
    @Expose
    private Attributes attributes;
    @SerializedName("additionalData")
    @Expose
    private AdditionalData additionalData;
    @SerializedName("name")
    @Expose
    private String name;

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public AdditionalData getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(AdditionalData additionalData) {
        this.additionalData = additionalData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public class Attributes {

        @SerializedName("action")
        @Expose
        private String action;
        @SerializedName("method")
        @Expose
        private String method;
        @SerializedName("enctype")
        @Expose
        private String enctype;
        @SerializedName("key")
        @Expose
        private String key;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getEnctype() {
            return enctype;
        }

        public void setEnctype(String enctype) {
            this.enctype = enctype;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

    }

    public class AdditionalData {

        @SerializedName("key")
        @Expose
        private String key;
        @SerializedName("X-Amz-Credential")
        @Expose
        private String xAmzCredential;
        @SerializedName("X-Amz-Algorithm")
        @Expose
        private String xAmzAlgorithm;
        @SerializedName("X-Amz-Date")
        @Expose
        private String xAmzDate;
        @SerializedName("Policy")
        @Expose
        private String policy;
        @SerializedName("X-Amz-Signature")
        @Expose
        private String xAmzSignature;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getXAmzCredential() {
            return xAmzCredential;
        }

        public void setXAmzCredential(String xAmzCredential) {
            this.xAmzCredential = xAmzCredential;
        }

        public String getXAmzAlgorithm() {
            return xAmzAlgorithm;
        }

        public void setXAmzAlgorithm(String xAmzAlgorithm) {
            this.xAmzAlgorithm = xAmzAlgorithm;
        }

        public String getXAmzDate() {
            return xAmzDate;
        }

        public void setXAmzDate(String xAmzDate) {
            this.xAmzDate = xAmzDate;
        }

        public String getPolicy() {
            return policy;
        }

        public void setPolicy(String policy) {
            this.policy = policy;
        }

        public String getXAmzSignature() {
            return xAmzSignature;
        }

        public void setXAmzSignature(String xAmzSignature) {
            this.xAmzSignature = xAmzSignature;
        }

    }
}

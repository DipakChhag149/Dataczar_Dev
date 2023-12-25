package com.dataczar.main.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetEBooksListResponse {
    @SerializedName("ebooks")
    @Expose
    private ArrayList<Ebook> ebooks;
    @SerializedName("my_ebooks")
    @Expose
    private ArrayList<Ebook> myEbooks;

    public ArrayList<Ebook> getEbooks() {
        return ebooks;
    }

    public void setEbooks(ArrayList<Ebook> ebooks) {
        this.ebooks = ebooks;
    }

    public ArrayList<Ebook> getMyEbooks() {
        return myEbooks;
    }

    public void setMyEbooks(ArrayList<Ebook> myEbooks) {
        this.myEbooks = myEbooks;
    }


    public class Ebook {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("pkgcd")
        @Expose
        private String pkgcd;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("price")
        @Expose
        private String price;
        @SerializedName("display_price")
        @Expose
        private String displayPrice;
        @SerializedName("path")
        @Expose
        private String path;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("plan_id")
        @Expose
        private Object planId;
        @SerializedName("plan_trial_days")
        @Expose
        private Object planTrialDays;
        @SerializedName("settings")
        @Expose
        private Settings settings;
        @SerializedName("course_id")
        @Expose
        private Object courseId;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("purchased")
        @Expose
        private Boolean purchased;
        @SerializedName("route")
        @Expose
        private String route;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getPkgcd() {
            return pkgcd;
        }

        public void setPkgcd(String pkgcd) {
            this.pkgcd = pkgcd;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getDisplayPrice() {
            return displayPrice;
        }

        public void setDisplayPrice(String displayPrice) {
            this.displayPrice = displayPrice;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Object getPlanId() {
            return planId;
        }

        public void setPlanId(Object planId) {
            this.planId = planId;
        }

        public Object getPlanTrialDays() {
            return planTrialDays;
        }

        public void setPlanTrialDays(Object planTrialDays) {
            this.planTrialDays = planTrialDays;
        }

        public Settings getSettings() {
            return settings;
        }

        public void setSettings(Settings settings) {
            this.settings = settings;
        }

        public Object getCourseId() {
            return courseId;
        }

        public void setCourseId(Object courseId) {
            this.courseId = courseId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Boolean getPurchased() {
            return purchased;
        }

        public void setPurchased(Boolean purchased) {
            this.purchased = purchased;
        }

        public String getRoute() {
            return route;
        }

        public void setRoute(String route) {
            this.route = route;
        }

    }


    public class Settings {

        @SerializedName("credit_balance")
        @Expose
        private Object creditBalance;
        @SerializedName("qty_account_limit")
        @Expose
        private Object qtyAccountLimit;
        @SerializedName("regular_price")
        @Expose
        private String regularPrice;

        public Object getCreditBalance() {
            return creditBalance;
        }

        public void setCreditBalance(Object creditBalance) {
            this.creditBalance = creditBalance;
        }

        public Object getQtyAccountLimit() {
            return qtyAccountLimit;
        }

        public void setQtyAccountLimit(Object qtyAccountLimit) {
            this.qtyAccountLimit = qtyAccountLimit;
        }

        public String getRegularPrice() {
            return regularPrice;
        }

        public void setRegularPrice(String regularPrice) {
            this.regularPrice = regularPrice;
        }

    }
}

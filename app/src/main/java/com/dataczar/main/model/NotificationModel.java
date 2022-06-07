/*
 * Copyright (c)  to Samrt Sense . Ai on 2022.
 */

package com.dataczar.main.model;

import java.io.Serializable;

public class NotificationModel implements Serializable {
    public String id;
    public String notifiable_id;
    public String title;
    public String msg;
    public String isStatus;
    public String time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNotifiable_id() {
        return notifiable_id;
    }

    public void setNotifiable_id(String notifiable_id) {
        this.notifiable_id = notifiable_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getIsStatus() {
        return isStatus;
    }

    public void setIsStatus(String isStatus) {
        this.isStatus = isStatus;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

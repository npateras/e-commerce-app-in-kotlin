package com.unipi.p17172.emarket.models;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class NotificationModel {

    private String id, title, body, iconUrl;


    public NotificationModel() {} // Needed for Firebase

    public NotificationModel(String id, String title, String body, String iconUrl) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.iconUrl = iconUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}

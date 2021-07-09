package com.hcdc.xncovid;

import android.app.Application;

public class MyApplication extends Application {

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        id = id;
    }

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String domain;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
    private String Form;

    public String getForm() {
        return Form;
    }

    public void setForm(String form) {
        Form = form;
    }
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

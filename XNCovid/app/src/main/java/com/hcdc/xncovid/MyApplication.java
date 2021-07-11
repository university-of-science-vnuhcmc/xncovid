package com.hcdc.xncovid;

import android.app.Application;

import com.hcdc.xncovid.model.Session;
import com.hcdc.xncovid.model.UserInfo;

public class MyApplication extends Application {

    private UserInfo userInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
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

    private Session session;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}

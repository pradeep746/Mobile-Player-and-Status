package com.pradeep.mobileacess.model;

public class UserSmsDataParameterForm {
    private String mUserName;
    private String body;

    public UserSmsDataParameterForm(String mUserName, String body) {
        this.mUserName = mUserName;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public String getUserName() {
        return mUserName;
    }

}

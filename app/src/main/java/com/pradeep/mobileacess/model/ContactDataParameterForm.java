package com.pradeep.mobileacess.model;

import java.util.ArrayList;
import java.util.List;

public class ContactDataParameterForm {
    private String mUserName;
    private List<String> mUserNumerList;
    private String thumbNail;
    public ContactDataParameterForm(String name, String thumbNail) {
        mUserName = name;
        mUserNumerList =  new ArrayList<>();
        this.thumbNail = thumbNail;
    }

    public List<String> getUserNumerList() {
        return mUserNumerList;
    }

    public String getNumber(int index){
        return mUserNumerList.get(index);
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserNumerList(String number) {
        number = number.replaceAll("[^a-zA-Z0-9\\s]", "");
        number = number.replaceAll(" ","");
        if(number.length() == 12) {
            number = number.substring(2, number.length());
        }
        if(mUserNumerList.contains(number)) {
            return;
        }
        mUserNumerList.add(number);
    }

    public String getThumbNail() {
        return thumbNail;
    }
}

package com.pradeep.mobileacess.model;

import java.util.ArrayList;

public class SmsSendParameterForm {
    public String UserName;
    public String UserNumber;
    ArrayList <smsSendDataParameterForm> listMessage = new ArrayList<>();

    public ArrayList getAllDetails() {
        return listMessage;
    }

    public String getUserName() {
        return UserName;
    }

    public String getUserNumber() {
        return UserNumber;
    }

    public void setAllDetails(smsSendDataParameterForm data) {
        listMessage.add(data);
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setUserNumber(String userNumber) {
        UserNumber = userNumber;
    }
}
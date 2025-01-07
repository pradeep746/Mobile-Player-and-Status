package com.pradeep.mobileacess.dbaccess;

import android.content.Context;
import android.os.Parcel;
import android.util.Log;

import com.pradeep.mobileacess.dbaccess.Database.UserDatabase;
import com.pradeep.mobileacess.dbaccess.Entities.User;

public class LoginAccess {
    String TAG = "LoginAccess";
    private static LoginAccess sInstance = null;
    private UserDatabase mUserData ;
    private LoginAccess(Context context){
        Log.e(TAG,"login");
        mUserData = UserDatabase.getInstance(context);
    }

    public static LoginAccess getInstance(Context context) {
        synchronized (LoginAccess.class) {
            if (sInstance == null) {
                sInstance = new LoginAccess(context);
            }
        }
        return sInstance;
    }
    public User login(String user,String pass) {
        Log.v(TAG, "fetch User Data");
        try {
            return mUserData.userDao().getLogin(user,pass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public User fetchUser(int id) {
        Log.v(TAG, "fetch User Data");
        try {
            return mUserData.userDao().getUser(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public int registerUser(User user) {
        try {
            mUserData.userDao().register(user);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}


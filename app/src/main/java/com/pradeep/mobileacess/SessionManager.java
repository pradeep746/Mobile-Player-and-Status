package com.pradeep.mobileacess;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Created by pradeep on 28-11-2018.
 */

public class SessionManager {

    private static String TAG = "SessionManager";
    private SharedPreferences pref;
    private static SessionManager mSessionManager;
    private Editor editor;
    private Context _context;
    private static final String PREF_NAME = "VideoPlayerLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_IS_SESSION_ID = "SessionId";
    private static final String KEY_IS_REGISTER = "isRegister";
    private static final String KEY_USER_IMAGE_URL = "Image_url";
    private static final String KEY_USER_NAME = "User_name";


    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }
    public static SessionManager getInstance(Context context) {
        if(mSessionManager == null){
            mSessionManager = new SessionManager(context);
        }
        return mSessionManager;
    }

    public void setLogin(boolean isLoggedIn,  Integer sessionId) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putInt(KEY_IS_SESSION_ID, sessionId);
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    public void setRegister(boolean isRegister,String image_url,String name) {
        editor.putBoolean(KEY_IS_REGISTER, isRegister);
        editor.putString(KEY_USER_IMAGE_URL, image_url);
        editor.putString(KEY_USER_NAME,name);
        editor.commit();
        Log.d(TAG, "User register session modified!");
    }

    public boolean isRegister(){
        return pref.getBoolean(KEY_IS_REGISTER, false);
    }

    public int getSessionID(){
        return pref.getInt(KEY_IS_SESSION_ID, -1);
    }

    public String getUserImage(){
        return pref.getString(KEY_USER_IMAGE_URL,"");
    }
    public String getUserName(){
        return pref.getString(KEY_USER_NAME,"");
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

}

package com.example.operatormanagement.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class PrefManager {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = MyApplication.context.getApplicationInfo().name;
    private static final String KEY_KEY = "key";
    private static final String KEY_USER_CODE = "userCode";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_COUNT_NOTIFICATION = "countNotification";
    private static final String KEY_COUNT_REQUEST = "countRquest";
    private static final String KEY_OPERATOR_LIST = "operatorList";
    private static final String KEY_REQUEST_LIST = "requestList";
    private static final String KEY_SHIFT_LIST = "shiftList";
    private static final String KEY_OPERATOR_NAME = "operatorName";
    private static final String KEY_SEND_REQUEST_LIST = "sendRequestList";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setKey(String key) {
        editor.putString(KEY_KEY, key);
        editor.commit();
    }

    public String getKey() {
        return pref.getString(KEY_KEY, "");
    }

    public void setUserCode(int userCode) {
        editor.putInt(KEY_USER_CODE, userCode);
        editor.commit();
    }

    public int getUserCode() {
        return pref.getInt(KEY_USER_CODE, 0);
    }

    public void isLoggedIn(boolean login) {
        editor.putBoolean(KEY_IS_LOGGED_IN, login);
        editor.commit();
    }

    public boolean getLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGED_IN,false);
    }

    public void setCountNotification(int count) {
        editor.putInt(KEY_COUNT_NOTIFICATION, count);
        editor.commit();
    }

    public int getCountNotification() {
        return pref.getInt(KEY_COUNT_NOTIFICATION, 0);
    }

    public void setOperatorList(String operatorList){
        editor.putString(KEY_OPERATOR_LIST,operatorList);
        editor.commit();
    }

    public String getOperatorList(){
        return pref.getString(KEY_OPERATOR_LIST,null);
    }

    public void setShiftList(String shiftList){
        editor.putString(KEY_SHIFT_LIST,shiftList);
        editor.commit();
    }

    public String getShiftList(){
        return pref.getString(KEY_SHIFT_LIST,null);
    }

    public void setOperatorName(String operatorName){
        editor.putString(KEY_OPERATOR_NAME,operatorName);
        editor.commit();
    }

    public String getOperatorName(){
        return pref.getString(KEY_OPERATOR_NAME,null);
    }

    public void setCountRequest(int count) {
        editor.putInt(KEY_COUNT_REQUEST, count);
        editor.commit();
    }

    public int getCountRequest() {
        return pref.getInt(KEY_COUNT_REQUEST, 0);
    }

    public void setRequestList(String requestList){
        editor.putString(KEY_REQUEST_LIST,requestList);
        editor.commit();
    }

    public String getRequestList(){
        return pref.getString(KEY_REQUEST_LIST,null);
    }

    public void setSendRequestList(String requestList){
        editor.putString(KEY_SEND_REQUEST_LIST,requestList);
        editor.commit();
    }

    public String getSendRequestList(){
        return pref.getString(KEY_SEND_REQUEST_LIST,null);
    }
}

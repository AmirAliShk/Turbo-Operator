package com.example.operatormanagement.app;

import com.example.operatormanagement.BuildConfig;


public class EndPoints {

//    http://172.16.2.201:1881/api/operator/*****
//    http://api.parsian.ir:1881/api/operator/

    public static final String IP = (BuildConfig.DEBUG)
            ? "http://172.16.2.210"
            : "http://turbotaxi.ir";

    public static final String APIPort = (BuildConfig.DEBUG) ? "1881" : "1881";

    public static final String WEBSERVICE_PATH = IP + ":" + APIPort + "/api/operator/";

    /***
     * base webservice
     **/
    public static final String GET_APP_INFO = WEBSERVICE_PATH + "getAppInfo";
    public static final String ANSWER_SHIFT_REPLACEMENT_REQUEST = WEBSERVICE_PATH + "answerShiftReplacementRequest";
    public static final String GET_MESSAGES = WEBSERVICE_PATH + "getMessages";
    public static final String GET_NEWS = WEBSERVICE_PATH + "getNews";
    public static final String GET_SHIFT_REPLACEMENT_REQUESTS = WEBSERVICE_PATH + "getShiftReplacementRequests";
    public static final String GET_SHIFTS = WEBSERVICE_PATH + "getShifts";
    public static final String LOGIN = WEBSERVICE_PATH + "login";
    public static final String SEND_MESSAGES = WEBSERVICE_PATH + "sendMessages";
    public static final String SET_NEWS_SEEN = WEBSERVICE_PATH + "setNewsSeen";
    public static final String SHIFT_REPLACEMENT_REQUEST = WEBSERVICE_PATH + "shiftReplacementRequest";
    public static final String GET_SHIFT_OPERATOR = WEBSERVICE_PATH + "getShiftOperators";
    public static final String CANCEL_REPLACEMENT_REQUEST = WEBSERVICE_PATH + "cancelReplacementRequest ";

}

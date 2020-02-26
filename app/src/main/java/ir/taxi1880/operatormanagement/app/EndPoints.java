package ir.taxi1880.operatormanagement.app;

import ir.taxi1880.operatormanagement.BuildConfig;


public class EndPoints {

//    http://172.16.2.201:1881/api/operator/*****
//    http://api.parsian.ir:1881/api/operator/
//    http://172.16.2.210:1885/api/findway/citylatinname/address

  public static final String IP = (BuildConfig.DEBUG)
          ? "http://turbotaxi.ir"
          : "http://turbotaxi.ir";

  public static final String PUSH_ADDRESS = "http://turbotaxi.ir:6060";

  public static final String APIPort = (BuildConfig.DEBUG) ? "1881" : "1881";

  public static final String WEBSERVICE_PATH = IP + ":" + APIPort + "/api/operator/";

  /******************************** Base Api *********************************/
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

  /******************************** Trip Register Api *********************************/

  public static final String TRIP_IP = (BuildConfig.DEBUG)
          ? "http://172.16.2.210"
          : "http://turbotaxi.ir";

  public static final String TRIP_APIPort = (BuildConfig.DEBUG) ? "1885" : "1885";

  public static final String TRIP_WEBSERVICE_PATH = TRIP_IP + ":" + TRIP_APIPort + "/api/";

  public static final String FIND_WAY = TRIP_WEBSERVICE_PATH + "findway";


}

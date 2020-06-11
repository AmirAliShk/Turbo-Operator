package ir.taxi1880.operatormanagement.app;

import ir.taxi1880.operatormanagement.BuildConfig;


public class EndPoints {

/*TODO : check apis and ports before release*/

//    http://172.16.2.201:1881/api/operator/*****
//    http://api.parsian.ir:1881/api/operator/
//    http://172.16.2.210:1885/api/findway/citylatinname/address

  public static final String IP = (BuildConfig.DEBUG)
//          ? "http://172.16.2.203"
          ? "http://turbotaxi.ir"
          : "http://turbotaxi.ir";
//          : "http://172.16.2.203";


  public static final String FIND_WAY_IP = (BuildConfig.DEBUG)
          ? "http://turbotaxi.ir"
//          ? "http://172.16.2.210"
          : "http://turbotaxi.ir";
//          : "http://172.16.2.210";

  public static final String PUSH_ADDRESS =   (BuildConfig.DEBUG)
          ? "http://turbotaxi.ir:6060"
//          ? "http://172.16.2.212:6060"
//          : "http://172.16.2.212:6060";
          : "http://turbotaxi.ir:6060";

  public static final String APIPort = (BuildConfig.DEBUG) ? "1881" : "1881";
  public static final String TRIP_APIPort = (BuildConfig.DEBUG) ? "1881" : "2323";
//  public static final String TRIP_APIPort = (BuildConfig.DEBUG) ? "2323" : "2323";
  public static final String FIND_WAY_APIPort = (BuildConfig.DEBUG) ? "1885" : "1885";

  public static final String WEBSERVICE_PATH = IP + ":" + APIPort + "/api/operator/";
  public static final String TRIP_WEBSERVICE_PATH = IP + ":" + TRIP_APIPort + "/api/operator/v2/trip/";
  public static final String FIND_WAY_WEBSERVICE_PATH = FIND_WAY_IP + ":" + FIND_WAY_APIPort + "/api/";
  public static final String ACCOUNT_WEBSERVICE_PATH = IP + ":" + TRIP_APIPort + "/api/operator/v2/";
  public static final String SCORE_WEBSERVICE_PATH = IP + ":" + TRIP_APIPort + "/api/operator/v2/score/";

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

  public static final String FIND_WAY = FIND_WAY_WEBSERVICE_PATH + "findway";

  public static final String PASSENGER_INFO = TRIP_WEBSERVICE_PATH + "passengerInfo";
  public static final String PASSENGER_ADDRESS = TRIP_WEBSERVICE_PATH + "passengerAddress";
  public static final String CHECK_STATION = TRIP_WEBSERVICE_PATH + "checkStation";
  public static final String STATION_INFO = TRIP_WEBSERVICE_PATH + "stationInfo";
  public static final String INSERT = TRIP_WEBSERVICE_PATH + "insert";
  public static final String ACTIVATE = TRIP_WEBSERVICE_PATH + "activate";
  public static final String DEACTIVATE = TRIP_WEBSERVICE_PATH + "deActivate";
  public static final String HIRETYPES  = TRIP_WEBSERVICE_PATH + "hireTypes";
  public static final String HIRE = TRIP_WEBSERVICE_PATH + "hire";

  /******************************** Account Api *********************************/

  public static final String BALANCE = ACCOUNT_WEBSERVICE_PATH + "balance";
  public static final String UPDATE_PROFILE = ACCOUNT_WEBSERVICE_PATH + "updateProfile";
  public static final String PAYMENT = ACCOUNT_WEBSERVICE_PATH + "payment";

  /******************************** Score Api *********************************/

  public static final String SCORE = ACCOUNT_WEBSERVICE_PATH + "score";
  public static final String BESTS = SCORE_WEBSERVICE_PATH + "bests";
  public static final String REWARDS = SCORE_WEBSERVICE_PATH + "rewards";
  public static final String SINGLE = SCORE_WEBSERVICE_PATH + "single";

}

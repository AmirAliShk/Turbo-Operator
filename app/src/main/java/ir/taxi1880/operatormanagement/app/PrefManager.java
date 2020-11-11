package ir.taxi1880.operatormanagement.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;


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
  private static final String KEY_USER_NAME = "userName";
  private static final String KEY_PASSWORD = "password";
  private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
  private static final String KEY_COUNT_NOTIFICATION = "countNotification";
  private static final String KEY_COUNT_REQUEST = "countRquest";
  private static final String KEY_OPERATOR_LIST = "operatorList";
  private static final String KEY_REQUEST_LIST = "requestList";
  private static final String KEY_SHIFT_LIST = "shiftList";
  private static final String KEY_OPERATOR_NAME = "operatorName";
  private static final String KEY_SEND_REQUEST_LIST = "sendRequestList";
  private static final String SIP_SERVER = "sipServer";
  private static final String SIP_NUMBER = "sipNumber";
  private static final String SIP_PASSWORD = "sipPassword";
  private static final String PUSH_TOKEN = "pushToken";
  private static final String PUSH_ID = "pushID";
  private static final String SHEBA = "sheba";
  private static final String CARD_NUMBER = "cardNumber";
  private static final String ACCOUNT_NUMBER = "accountNumber";
  private static final String BALANCE = "balance";
  private static final String ACTIVEMAINACTIVITY = "activeMainActivity";
  private static final String CONNECTEDCALL = "connectedCall";
  private static final String INCOMINGCALL = "incomingCall";
  private static final String SERVICE_TYPE = "typeService";
  private static final String QUEUE_LIST = "queueList";
  private static final String QUEUE = "queue";
  private static final String CITY = "city";
  private static final String ACCESS_INSERT_SERVICE = "accessInsertService";
  private static final String ACCESS_STATION_DETERMINATION_PAGE = "accessStationDeterminationPage";
  private static final String ACTIVATE_STATUS = "activateStatus";
  private static final String START_GETTING_ADDRESS = "startGettingAddress";
  private static final String VOIP_ID = "voipId";
  private static final String LAST_NOTIFICATION = "lastNotification";
  private static final String LAST_CALL_NUMBER = "lastCallNumber";
  private static final String KEY_APP_STATUS = "AppStatus";
  private static final String CUSTOMER_SUPPORT = "customerSupport";
  private static final String COMPLAINT_TYPE = "ComplaintType";
  private static final String OBJECTS_TYPE = "objectsType";
  private static final String REASONS_LOCK = "ReasonsLock";

  public PrefManager(Context context) {
    this._context = context;
    pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    editor = pref.edit();
  }

  public void setAppRun(boolean v) {
    editor.putBoolean(KEY_APP_STATUS, v);
    editor.commit();
  }

  public boolean isAppRun() {
    return pref.getBoolean(KEY_APP_STATUS, false);
  }

  public String getComplaint() {
    return pref.getString(COMPLAINT_TYPE, "");
  }

  public void setComplaint(String complaint) {
    editor.putString(COMPLAINT_TYPE, complaint);
    editor.commit();
  }

  public String getObjectsType() {
    return pref.getString(OBJECTS_TYPE, "");
  }

  public void setObjectsType(String objects) {
    editor.putString(OBJECTS_TYPE, objects);
    editor.commit();
  }

  public String getReasonsLock() {
    return pref.getString(REASONS_LOCK, "");
  }

  public void setReasonsLock(String reasonLock) {
    editor.putString(REASONS_LOCK, reasonLock);
    editor.commit();
  }

  public String getVoipId() {
    return pref.getString(VOIP_ID, "");
  }

  public void setVoipId(String voipId) {
    editor.putString(VOIP_ID, voipId);
    editor.commit();
  }

  public String getLastNotification() {
    return pref.getString(LAST_NOTIFICATION, null);
  }

  public void setLastNotification(String v) {
    editor.putString(LAST_NOTIFICATION, v);
    editor.commit();
  }

  public String getQueue() {
    return pref.getString(QUEUE, "");
  }

  public void setQueue(String queue) {
    editor.putString(QUEUE, queue);
    editor.commit();
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

  public void setActivateStatus(boolean activateStatus) {
    editor.putBoolean(ACTIVATE_STATUS, activateStatus);
    editor.commit();
  }

  public boolean getActivateStatus() {
    return pref.getBoolean(ACTIVATE_STATUS, false);
  }

  public void setCustomerSupport(int customerSupport) {
    editor.putInt(CUSTOMER_SUPPORT, customerSupport);
    editor.commit();
  }

  public int getCustomerSupport() {
    return pref.getInt(CUSTOMER_SUPPORT, 0);
  }

  public void setStartGettingAddress(boolean getAddress) {
    editor.putBoolean(START_GETTING_ADDRESS, getAddress);
    editor.commit();
  }

  public boolean isStartGettingAddress() {
    return pref.getBoolean(START_GETTING_ADDRESS, false);
  }

  public void setBalance(int balance) {
    editor.putInt(BALANCE, balance);
    editor.commit();
  }

  public int getBalance() {
    return pref.getInt(BALANCE, 0);
  }

  public void setSipNumber(int sipNumber) {
    editor.putInt(SIP_NUMBER, sipNumber);
    editor.commit();
  }

  public int getSipNumber() {
    return pref.getInt(SIP_NUMBER, 0);
  }

  public void setAccessInsertService(int accessInsertService) {
    editor.putInt(ACCESS_INSERT_SERVICE, accessInsertService);
    editor.commit();
  }

  public int getAccessInsertService() {
    return pref.getInt(ACCESS_INSERT_SERVICE, -1);
  }

  public void setAccessStationDeterminationPage(int accessStationDeterminationPage) {
    editor.putInt(ACCESS_STATION_DETERMINATION_PAGE, accessStationDeterminationPage);
    editor.commit();
  }

  public int getAccessStationDeterminationPage() {
    return pref.getInt(ACCESS_STATION_DETERMINATION_PAGE, -1);
  }

  public void setSipServer(String sipServer) {
    editor.putString(SIP_SERVER, sipServer);
    editor.commit();
  }

  public String getSipServer() {
    return pref.getString(SIP_SERVER, "");
  }

  public void setSipPassword(String sipPassword) {
    editor.putString(SIP_PASSWORD, sipPassword);
    editor.commit();
  }

  public String getSipPassword() {
    return pref.getString(SIP_PASSWORD, "");
  }

  public void setPushToken(String v) {
    editor.putString(PUSH_TOKEN, v);
    editor.commit();
  }

  public String getPushToken() {
    return pref.getString(PUSH_TOKEN, "");
  }

  public void setPushId(int v) {
    editor.putInt(PUSH_ID, v);
    editor.commit();
  }

  public int getPushId() {
    return pref.getInt(PUSH_ID, 5);
  }

  public void setSheba(String sheba) {
    editor.putString(SHEBA, sheba);
    editor.commit();
  }

  public String getSheba() {
    return pref.getString(SHEBA, "");
  }

  public void setCardNumber(String cardNumber) {
    editor.putString(CARD_NUMBER, cardNumber);
    editor.commit();
  }

  public String getCardNumber() {
    return pref.getString(CARD_NUMBER, "");
  }

  public void setAccountNumber(String accountNumber) {
    editor.putString(ACCOUNT_NUMBER, accountNumber);
    editor.commit();
  }

  public String getAccountNumber() {
    return pref.getString(ACCOUNT_NUMBER, "");
  }

  public void setServiceType(String serviceType) {
    editor.putString(SERVICE_TYPE, serviceType);
    editor.commit();
  }

  public String getServiceType() {
    return pref.getString(SERVICE_TYPE, "");
  }

  public void setQueueList(String queue) {
    editor.putString(QUEUE_LIST, queue);
    editor.commit();
  }

  public String getQueueList() {
    return pref.getString(QUEUE_LIST, "");
  }

  public void setCity(String city) {
    Log.d("LOG", "setCity: "+city);
    editor.putString(CITY, city);
    editor.commit();
  }

  public String getCity() {
    return pref.getString(CITY, "");
  }

  public void setPassword(String pass) {
    editor.putString(KEY_PASSWORD, pass);
    editor.commit();
  }

  public String getPassword() {
    return pref.getString(KEY_PASSWORD, "0");
  }

  public void setUserName(String userName) {
    editor.putString(KEY_USER_NAME, userName);
    editor.commit();
  }

  public String getUserName() {
    return pref.getString(KEY_USER_NAME, "0");
  }

  public void isLoggedIn(boolean login) {
    editor.putBoolean(KEY_IS_LOGGED_IN, login);
    editor.commit();
  }

  public boolean getLoggedIn() {
    return pref.getBoolean(KEY_IS_LOGGED_IN, false);
  }

  public void setCountNotification(int count) {
    editor.putInt(KEY_COUNT_NOTIFICATION, count);
    editor.commit();
  }

  public int getCountNotification() {
    return pref.getInt(KEY_COUNT_NOTIFICATION, 0);
  }

  public void setOperatorList(String operatorList) {
    editor.putString(KEY_OPERATOR_LIST, operatorList);
    editor.commit();
  }

  public String getOperatorList() {
    return pref.getString(KEY_OPERATOR_LIST, null);
  }

  public void setShiftList(String shiftList) {
    editor.putString(KEY_SHIFT_LIST, shiftList);
    editor.commit();
  }

  public String getShiftList() {
    return pref.getString(KEY_SHIFT_LIST, null);
  }

  public void setOperatorName(String operatorName) {
    editor.putString(KEY_OPERATOR_NAME, operatorName);
    editor.commit();
  }

  public String getOperatorName() {
    return pref.getString(KEY_OPERATOR_NAME, null);
  }

  public void setCountRequest(int count) {
    editor.putInt(KEY_COUNT_REQUEST, count);
    editor.commit();
  }

  public int getCountRequest() {
    return pref.getInt(KEY_COUNT_REQUEST, 0);
  }

  public void setRequestList(String requestList) {
    editor.putString(KEY_REQUEST_LIST, requestList);
    editor.commit();
  }

  public String getRequestList() {
    return pref.getString(KEY_REQUEST_LIST, null);
  }

  public void setSendRequestList(String requestList) {
    editor.putString(KEY_SEND_REQUEST_LIST, requestList);
    editor.commit();
  }

  public String getSendRequestList() {
    return pref.getString(KEY_SEND_REQUEST_LIST, null);
  }

  public void setLastCall(String callNumber) {
    editor.putString(LAST_CALL_NUMBER, callNumber);
    editor.commit();
  }

  public String getLastCall() {
    return pref.getString(LAST_CALL_NUMBER, "null");
  }

  public boolean getConnectedCall() {
    return pref.getBoolean(CONNECTEDCALL, false);
  }

  public void setConnectedCall(Boolean connected) {
    editor.putBoolean(CONNECTEDCALL, connected);
    editor.commit();
  }
  public boolean isCallIncoming() {
    return pref.getBoolean(INCOMINGCALL, false);
  }

  public void setCallIncoming(Boolean incoming) {
    editor.putBoolean(INCOMINGCALL, incoming);
    editor.commit();
  }
}

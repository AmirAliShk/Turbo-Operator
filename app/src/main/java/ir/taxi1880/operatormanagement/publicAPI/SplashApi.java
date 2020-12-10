package ir.taxi1880.operatormanagement.publicAPI;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ir.taxi1880.operatormanagement.app.Constant;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dataBase.DataBase;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.fragment.LoginFragment;
import ir.taxi1880.operatormanagement.helper.AppVersionHelper;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.ScreenHelper;
import ir.taxi1880.operatormanagement.model.CityModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

import static ir.taxi1880.operatormanagement.app.MyApplication.context;

public class SplashApi {
    public final String TAG = SplashApi.class.getSimpleName();
    DataBase dataBase;

    public interface SplashInterface {
        void isFinishContract(boolean finished);

        void update(String updateUrl, int isForce, int updateAvailable);

        void isBlock(boolean isBlock);

        void changePass(boolean isChangePass);

        void continueProcessing(boolean continueProcess);
    }

    SplashInterface splashInterface;

    public void getAppInfo(SplashInterface splashInterface) {
        this.splashInterface = splashInterface;
        JSONObject deviceInfo = new JSONObject();
        try {
            @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(MyApplication.currentActivity.getContentResolver(), Settings.Secure.ANDROID_ID);
            deviceInfo.put("MODEL", Build.MODEL);
            deviceInfo.put("HARDWARE", Build.HARDWARE);
            deviceInfo.put("BRAND", Build.BRAND);
            deviceInfo.put("DISPLAY", Build.DISPLAY);
            deviceInfo.put("BOARD", Build.BOARD);
            deviceInfo.put("SDK_INT", Build.VERSION.SDK_INT);
            deviceInfo.put("BOOTLOADER", Build.BOOTLOADER);
            deviceInfo.put("DEVICE", Build.DEVICE);
            deviceInfo.put("DISPLAY_HEIGHT", ScreenHelper.getRealDeviceSizeInPixels(MyApplication.currentActivity).getHeight());
            deviceInfo.put("DISPLAY_WIDTH", ScreenHelper.getRealDeviceSizeInPixels(MyApplication.currentActivity).getWidth());
            deviceInfo.put("DISPLAY_SIZE", ScreenHelper.getScreenSize(MyApplication.currentActivity));
            deviceInfo.put("ANDROID_ID", android_id);

            RequestHelper.builder(EndPoints.GET_APP_INFO)
//                    .addHeader("Authorization",MyApplication.prefManager.getAuthorization())
//                    .addHeader("id_token",MyApplication.prefManager.getIdToken())
                    .addParam("versionCode", new AppVersionHelper(context).getVerionCode())
                    .addParam("operatorId", MyApplication.prefManager.getUserCode())
                    .addParam("userName", MyApplication.prefManager.getUserName())
                    .addParam("password", MyApplication.prefManager.getPassword())
                    .addParam("deviceInfo", deviceInfo)
                    .listener(onAppInfo)
                    .post();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    RequestHelper.Callback onAppInfo = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    dataBase = new DataBase(context);
                    JSONObject object = new JSONObject(args[0].toString());
                    int block = object.getInt("isBlock");
                    int updateAvailable = object.getInt("updateAvailable");
                    int forceUpdate = object.getInt("forceUpdate");
                    String updateUrl = object.getString("updateUrl");
                    int changePass = object.getInt("changePassword");
                    int countRequest = object.getInt("countRequest");
                    int sipNumber = object.getInt("sipNumber");
                    String sipServer = object.getString("sipServer");
                    String sipPassword = object.getString("sipPassword");
                    String sheba = object.getString("sheba");
                    String cardNumber = object.getString("cardNumber");
                    String accountNumber = object.getString("accountNumber");
                    int accessInsertService = object.getInt("accessInsertService");
                    int accessStationDeterminationPage = object.getInt("accessStationDeterminationPage");
                    int balance = object.getInt("balance");
                    String typeService = object.getString("typeService");
                    String queue = object.getString("queue");
                    String city = object.getString("city");
                    int pushId = object.getInt("pushId");
                    String pushToken = object.getString("pushToken");
                    String complaintType = object.getString("ComplaintType");
                    String objectsType = object.getString("objectsType");
                    String ReasonsLock = object.getString("ReasonsLock");
                    int activeInQueue = object.getInt("activeInQueue");
                    int isFinishContract = object.getInt("isFinishContract");
                    int customerSupport = object.getInt("customerSupport");
                    MyApplication.prefManager.setCustomerSupport(customerSupport);

                    MyApplication.prefManager.setComplaint(complaintType);
                    MyApplication.prefManager.setObjectsType(objectsType);
                    MyApplication.prefManager.setReasonsLock(ReasonsLock);

                    //insert all city into dataBase
                    JSONArray cityArr = new JSONArray(city);
                    for (int c = 0; c < cityArr.length(); c++) {
                        JSONObject cityObj = cityArr.getJSONObject(c);
                        CityModel cityModel = new CityModel();
                        cityModel.setId(cityObj.getInt("cityid"));
                        cityModel.setCity(cityObj.getString("cityname"));
                        cityModel.setCityLatin(cityObj.getString("latinName"));
                        dataBase.insertCity(cityModel);
                    }

                    if (block == 1) {
                        splashInterface.isBlock(true);
                        return;
                    }

                    if (changePass == 1) {
                        splashInterface.changePass(true);
                        return;
                    }

                    if (updateAvailable == 1) {
                        splashInterface.update(updateUrl, forceUpdate, updateAvailable);
                        return;
                    }

                    if (isFinishContract == 1) {
                        splashInterface.isFinishContract(true);
                        //TODO it is correct??
                        return;
                    }

                    MyApplication.prefManager.setSipServer(sipServer);
                    MyApplication.prefManager.setSipNumber(sipNumber);
                    MyApplication.prefManager.setSipPassword(sipPassword);

                    splashInterface.continueProcessing(true);

                    if (sipNumber != MyApplication.prefManager.getSipNumber() ||
                            !sipPassword.equals(MyApplication.prefManager.getSipPassword()) ||
                            !sipServer.equals(MyApplication.prefManager.getSipServer())) {
                        if (sipNumber != 0) {
                            MyApplication.configureAccount();
                        }
                    }

                    MyApplication.prefManager.setActivateStatus(activeInQueue == 1);

                    JSONArray shiftArr = object.getJSONArray("shifs");
                    MyApplication.prefManager.setShiftList(shiftArr.toString());

                    MyApplication.prefManager.setCountNotification(object.getInt("countNotification"));
                    MyApplication.prefManager.setCountRequest(object.getInt("countRequest"));

                    MyApplication.prefManager.setPushId(pushId);
                    MyApplication.prefManager.setPushToken(pushToken);
                    MyApplication.prefManager.setSheba(sheba);
                    MyApplication.prefManager.setCardNumber(cardNumber);
                    MyApplication.prefManager.setAccountNumber(accountNumber);
                    MyApplication.prefManager.setBalance(balance);
                    MyApplication.prefManager.setServiceType(typeService);
                    MyApplication.prefManager.setQueueList(queue);
                    MyApplication.prefManager.setCity(city);
                    MyApplication.prefManager.setAccessInsertService(accessInsertService);
                    MyApplication.prefManager.setAccessStationDeterminationPage(accessStationDeterminationPage);

                    NotificationManager notificationManager = (NotificationManager) MyApplication.currentActivity.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(Constant.USER_STATUS_NOTIFICATION_ID);

                } catch (JSONException e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, "SplashActivity class, onAppInfo onResponse method");
                }

            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {

        }
    };

}

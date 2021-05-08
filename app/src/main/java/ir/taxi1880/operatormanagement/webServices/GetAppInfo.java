package ir.taxi1880.operatormanagement.webServices;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.Constant;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dataBase.DataBase;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.fragment.LoginFragment;
import ir.taxi1880.operatormanagement.helper.AppVersionHelper;
import ir.taxi1880.operatormanagement.helper.ContinueProcessing;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.helper.ScreenHelper;
import ir.taxi1880.operatormanagement.helper.ServiceHelper;
import ir.taxi1880.operatormanagement.model.CityModel;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;
import ir.taxi1880.operatormanagement.services.LinphoneService;

import static ir.taxi1880.operatormanagement.app.MyApplication.context;

public class GetAppInfo {
    DataBase dataBase;

    public void callAppInfoAPI() {
        try {
            if (MyApplication.prefManager.getRefreshToken().equals("")) {
                FragmentHelper
                        .toFragment(MyApplication.currentActivity, new LoginFragment())
                        .setStatusBarColor(MyApplication.currentActivity.getResources().getColor(R.color.colorPrimaryDark))
                        .setAddToBackStack(false)
                        .add();
            } else {
                JSONObject deviceInfo = new JSONObject();
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
                        .addParam("versionCode", new AppVersionHelper(context).getVerionCode())
                        .addParam("deviceInfo", deviceInfo)
                        .listener(onAppInfo)
                        .post();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    RequestHelper.Callback onAppInfo = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    int block = object.getInt("isBlock");
                    int accessDriverSupport = object.getInt("accessDriverSupport");
                    int updateAvailable = object.getInt("updateAvailable");
                    int forceUpdate = object.getInt("forceUpdate");
                    String updateUrl = object.getString("updateUrl");
                    int changePass = object.getInt("changePassword");
                    int countRequest = object.getInt("countRequest");
                    int sipNumber = object.getInt("sipNumber");
                    String sipServer = object.getString("sipServer");
                    String sipPassword = object.getString("sipPassword");
                    String sheba = object.getString("sheba");
                    int userId = object.getInt("userId");
                    String cardNumber = object.getString("cardNumber");
                    String accountNumber = object.getString("accountNumber");
                    String monthScore = object.getString("monthScore");
                    String dayScore = object.getString("dayScore");
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
                    String serviceCountToday = object.getString("serviceCountToday");
                    String serviceCountMonth = object.getString("serviceCountMonth");
                    int activeInQueue = object.getInt("activeInQueue");
                    int customerSupport = object.getInt("customerSupport");
                    int accessComplaint = object.getInt("accessComplaint");
                    String name = object.getString("name");
                    String family = object.getString("family");

                    MyApplication.prefManager.setOperatorName(name + " " + family);
                    MyApplication.prefManager.setCustomerSupport(customerSupport);
                    MyApplication.prefManager.setAccessComplaint(accessComplaint);

                    MyApplication.prefManager.setAccessDriverSupport(accessDriverSupport);
                    MyApplication.prefManager.setUserCode(userId);
                    MyApplication.prefManager.setComplaint(complaintType);
                    MyApplication.prefManager.setObjectsType(objectsType);
                    MyApplication.prefManager.setReasonsLock(ReasonsLock);
                    MyApplication.prefManager.setDailyScore(dayScore);
                    MyApplication.prefManager.setMonthScore(monthScore);
                    MyApplication.prefManager.setServiceCountMonth(serviceCountMonth);
                    MyApplication.prefManager.setServiceCountToday(serviceCountToday);

                    //insert all city into dataBase
                    JSONArray cityArr = new JSONArray(city);
                    dataBase = new DataBase(context);
                    for (int c = 0; c < cityArr.length(); c++) {
                        JSONObject cityObj = cityArr.getJSONObject(c);
                        CityModel cityModel = new CityModel();
                        cityModel.setId(cityObj.getInt("cityid"));
                        cityModel.setCity(cityObj.getString("cityname"));
                        cityModel.setCityLatin(cityObj.getString("latinName"));
                        dataBase.insertCity(cityModel);
                    }

                    if (block == 1) {
                        new GeneralDialog()
                                .title("هشدار")
                                .message("اکانت شما توسط سیستم مسدود شده است")
                                .firstButton("خروج از برنامه", () -> MyApplication.currentActivity.finish())
                                .show();
                        return;
                    }

                    if (changePass == 1) {
                        FragmentHelper
                                .toFragment(MyApplication.currentActivity, new LoginFragment())
                                .setStatusBarColor(MyApplication.currentActivity.getResources().getColor(R.color.colorPrimaryDark))
                                .setAddToBackStack(false)
                                .replace();
                        return;
                    }

                    MyApplication.prefManager.setSipServer(sipServer);
                    MyApplication.prefManager.setSipNumber(sipNumber);
                    MyApplication.prefManager.setSipPassword(sipPassword);
                    if (updateAvailable == 1) {
                        updatePart(forceUpdate, updateUrl);
                        return;
                    }

                    startVoipService();

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
            MyApplication.handler.post(() -> {
            });
        }
    };

    private void updatePart(int isForce, final String url) {
        GeneralDialog generalDialog = new GeneralDialog();
        if (isForce == 1) {
            generalDialog.title("به روز رسانی");
            generalDialog.cancelable(false);
            generalDialog.message("برای برنامه نسخه جدیدی موجود است لطفا برنامه را به روز رسانی کنید");
            generalDialog.firstButton("به روز رسانی", () -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                MyApplication.currentActivity.startActivity(i);
                MyApplication.currentActivity.finish();
            });
            generalDialog.secondButton("بستن برنامه", () -> MyApplication.currentActivity.finish());
            generalDialog.show();
        } else {
            generalDialog.title("به روز رسانی");
            generalDialog.cancelable(false);
            generalDialog.message("برای برنامه نسخه جدیدی موجود است در صورت تمایل میتوانید برنامه را به روز رسانی کنید");
            generalDialog.firstButton("به روز رسانی", () -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                MyApplication.currentActivity.startActivity(i);
                MyApplication.currentActivity.finish();
            });
            generalDialog.secondButton("فعلا نه", () -> startVoipService());
            generalDialog.show();
        }
    }

    // This thread will periodically check if the Service is ready, and then call onServiceReady
    public class ServiceWaitThread extends Thread {
        public void run() {
            while (!LinphoneService.isReady()) {
                try {
                    sleep(30);
                } catch (InterruptedException e) {
                    AvaCrashReporter.send(e, "SplashActivity class, ServiceWaitThread onResponse method");
                    throw new RuntimeException("waiting thread sleep() has been interrupted");
                }
            }
            // As we're in a thread, we can't do UI stuff in it, must post a runnable in UI thread
            MyApplication.handler.post(
                    () -> ContinueProcessing.runMainActivity());
        }
    }

    public void startVoipService() {
        if (LinphoneService.isReady()) {
            ContinueProcessing.runMainActivity();
        } else {
            // If it's not, let's start it
            ServiceHelper.start(context, LinphoneService.class);
            // And wait for it to be ready, so we can safely use it afterwards
            new ServiceWaitThread().start();
        }
    }
}

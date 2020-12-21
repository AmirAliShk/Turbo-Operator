package ir.taxi1880.operatormanagement.okHttp;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.publicAPI.RefreshToken;
import ir.taxi1880.operatormanagement.dialog.ErrorDialog;
import ir.taxi1880.operatormanagement.dialog.GeneralDialog;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;
import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

/***
 * Created by Amirreza Erfanian on 2018/01/12.
 * implementation 'com.squareup.okhttp3:okhttp:3.10.0'
 */
public class RequestHelper implements okhttp3.Callback {

    public static final String TAG = RequestHelper.class.getSimpleName();
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String DELETE = "DELETE";
    public static final String PUT = "PUT";
    public static final String HEAD = "HEAD";
    public static final String PATCH = "PATCH";

    private static RequestHelper instance;
    private String url = null;
    private String tokenUrl = null;
    private String path = null;
    private Callback listener = null;
    private JSONObject params = null;
    private ArrayList<String> paths = null;
    private boolean errorHandling = true;
    private boolean hideNetworkError = false;
    private Request req = null;
    private Request tokenReq = null;
    private Object[] object;
    private boolean ignore422 = false;
    private boolean doNotSendHeader = false;
    private Headers.Builder headers = new Headers.Builder();

    public static abstract class Callback {
        public void onReloadPress(boolean v) {
        }

        public void onFailure(Runnable reCall, Exception e) {
        }

        public abstract void onResponse(Runnable reCall, Object... args);
    }

    public RequestHelper addHeader(String name, String value) {
        this.headers.add(name, value);
        return this;
    }

    public RequestHelper hideNetworkError(boolean hideNetworkError) {
        this.hideNetworkError = hideNetworkError;
        return this;
    }

    public RequestHelper setErrorHandling(Boolean v) {
        this.errorHandling = v;
        return this;
    }

    public RequestHelper returnInResponse(Object... object) {
        this.object = object;
        return this;
    }

    public RequestHelper addParam(String key, Object value) {
        if (params == null) {
            params = new JSONObject();
        }
        try {
            params.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "RequestHelper class, addParam method ");
        }
        return this;
    }

    public RequestHelper addParam(String key, String value) {
        if (params == null) {
            params = new JSONObject();
        }
        try {
            value = StringHelper.toEnglishDigits(value);
            params.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "RequestHelper class, addParam method ");
        }
        return this;
    }

    public RequestHelper addPath(String value) {
        if (paths == null)
            paths = new ArrayList<>();
        paths.add(value);
        return this;
    }

    public RequestHelper ignore422Error(boolean ignore) {
        this.ignore422 = ignore;
        return this;
    }

    public RequestHelper doNotSendHeader(boolean doNotSendHeader) {
        this.doNotSendHeader = doNotSendHeader;
        return this;
    }

    public RequestHelper listener(Callback listener) {
        this.listener = listener;
        return this;
    }

    public RequestHelper readTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public RequestHelper connectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public RequestHelper writeTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }

    public static RequestHelper builder(String url) {
        instance = new RequestHelper();
        instance.url = url;
        return instance;
    }

    public static RequestHelper tokenBuilder(String url) {
        instance = new RequestHelper();
        instance.tokenUrl = url;
        return instance;
    }

    private String getUrl() {
        String url = this.url;
        if (path != null) {
            String address = EndPoints.IP;
            url = "http://" + address + this.path;
        }

        this.url = url;
        return url;
    }

    private String getTokenUrl() {
        String url = this.tokenUrl;
        if (path != null) {
            String address = EndPoints.IP;
            url = "http://" + address + this.path;
        }

        this.tokenUrl = url;
        return url;
    }

    public static RequestHelper loadBalancingBuilder(String path) {
        instance = new RequestHelper();
        instance.path = path;
        return instance;
    }

    public void get() {
        url = getUrl();
        if (url == null) return;

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (params != null) {
            Iterator<String> iter = params.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    String value = params.getString(key);
                    urlBuilder.addQueryParameter(key, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                    AvaCrashReporter.send(e, "RequestHelper class, get method ");
                }
            }
        }
        if (paths != null) {
            for (String ob : paths) {
                if (ob == null) continue;
                urlBuilder.addPathSegment(ob);
            }
        }
        String url = urlBuilder.build().toString();

        if (doNotSendHeader) {
            req = new Request.Builder()
                    .url(url)
                    .build();
        } else {
            req = new Request.Builder()
                    .addHeader("Authorization", MyApplication.prefManager.getAuthorization())
                    .addHeader("id_token", MyApplication.prefManager.getIdToken())
                    .url(url)
                    .build();
        }

        request();

    }

    public void post() {
        url = getUrl();
        if (url == null) return;

        RequestBody body = RequestBody.create(JSON, params.toString());

        if (doNotSendHeader) {
            req = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
        } else {
            req = new Request.Builder()
                    .addHeader("Authorization", MyApplication.prefManager.getAuthorization())
                    .addHeader("id_token", MyApplication.prefManager.getIdToken())
                    .url(url)
                    .post(body)
                    .build();
        }

        request();

    }

    public void postToken() {
        tokenUrl = getTokenUrl();
        if (tokenUrl == null) return;

        RequestBody body = RequestBody.create(JSON, params.toString());

        tokenReq = new Request.Builder()
                .url(tokenUrl)
                .post(body)
                .build();

        tokenRequest();

    }

    public void put() {
        url = getUrl();
        if (url == null) return;

        RequestBody body = RequestBody.create(JSON, params.toString());
        if (doNotSendHeader) {
            req = new Request.Builder()
                    .url(url)
                    .put(body)
                    .build();
        } else {
            req = new Request.Builder()
                    .addHeader("Authorization", MyApplication.prefManager.getAuthorization())
                    .addHeader("id_token", MyApplication.prefManager.getIdToken())
                    .url(url)
                    .put(body)
                    .build();
        }
        request();

    }

    public void delete() {
        url = getUrl();
        if (url == null) return;

        RequestBody body = RequestBody.create(JSON, params.toString());
        //        if (doNotSendHeader) {
        req = new Request.Builder()
                .url(url)
                .delete(body)
                .build();
        //        } else {
//            req = new Request.Builder()
//                    .addHeader("Authorization", MyApplication.prefManager.getAuthorization())
//                    .addHeader("id_token", MyApplication.prefManager.getIdToken())
//                    .url(url)
//                    .delete(body)
//                    .build();
//        }
        request();
    }

    private void tokenRequest() {
        try {
            log("request url : " + tokenReq.url().toString());
            log("params : " + params);
            log("paths : " + path);
            OkHttpClient.Builder builder = new OkHttpClient
                    .Builder()
                    .proxy(Proxy.NO_PROXY);

            OkHttpClient okHttpClient = builder.connectTimeout(connectionTimeout, TimeUnit.SECONDS)
                    .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                    .readTimeout(readTimeout, TimeUnit.SECONDS)
                    .build();

            tokenCall = okHttpClient.newCall(tokenReq);
            tokenCall.enqueue(this);

        } catch (final Exception e) {
            requestFailed(REQUEST_CRASH, e);
            AvaCrashReporter.send(e, "RequestHelper class, request method ");
        }
    }

    public  OkHttpClient okHttpClient;
    OkHttpClient.Builder builder;
    private void request() {
        try {
            log("request url : " + req.url().toString());
            log("params : " + params);
            log("paths : " + path);
            log("header req.Authorization : " + req.headers().get("Authorization"));
            log("header req.id_token : " + req.headers().get("id_token"));
            log("header headers.Authorization : " + headers.get("Authorization"));
            log("header headers.id_token : " + headers.get("id_token"));
            builder = new OkHttpClient
                    .Builder()
                    .proxy(Proxy.NO_PROXY);

            okHttpClient = builder.connectTimeout(connectionTimeout, TimeUnit.SECONDS)
                    .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                    .readTimeout(readTimeout, TimeUnit.SECONDS)
                    .addInterceptor(new AuthTokenRefreshInterceptor())
                    .build();

            call = okHttpClient.newCall(req);
            call.enqueue(this);

        } catch (final Exception e) {
            requestFailed(REQUEST_CRASH, e);
            AvaCrashReporter.send(e, "RequestHelper class, request method ");
        }
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private int connectionTimeout = 20;
    private int writeTimeout = 20;
    private int readTimeout = 25;
    private Object object1 = null;
    private Object object2 = null;

    public static final int INTERNET_CONNECTION_EXCEPTION = -1;
    public static final int REQUEST_CRASH = -2;

    @Override
    public void onFailure(Call call, final IOException e) {
        this.call = call;
        this.tokenCall = call;
        log("request failed :  The requested URL can't be Reached The service took too long to respond.");
        if (listener != null)
            requestFailed(INTERNET_CONNECTION_EXCEPTION, e);
    }

    @Override
    public void onResponse(Call call, final Response response) {
        this.call = call;
        this.tokenCall = call;
        if (listener != null) {
            final String bodyStr;
            try {
                bodyStr = parseXML(response.body().string());
                log("request result : " + bodyStr);

                if (!response.isSuccessful()) {
                    if (response.code() == 401 || response.code() == 402 || response.code() == 403) {
                        okHttpClient=builder.addInterceptor(new AuthTokenRefreshInterceptor()).build();
//                        new RefreshToken().refreshToken(success -> {
//                            if (success) {
//                                showError("عدم دسترسی به اینترنت لطفا پس از بررسی ارتباط دستگاه خود به اینترنت و اطمینان از ارتباط، مجدد تلاش نمایید.");
//                            }});
                    } else {
                        requestFailed(response.code(), new Exception(response.message() + bodyStr));
                    }
                } else {
                    if (object == null)
                        object = new Object[0];
                    requestSuccess(bodyStr);
                }
            } catch (final IOException e) {
                requestFailed(response.code(), e);
                if (listener != null)
                    listener.onFailure(runnable, e);
                AvaCrashReporter.send(e, "RequestHelper class, onResponse method ");

            }
        }
    }

    /**
     * manage log
     *
     * @param v
     */
    private void log(String v) {
        Log.i(TAG, "====> " + v);
    }

    /**
     * this function extract response from XML result
     *
     * @param str = response from EndPoints
     * @return
     */
    public static String parseXML(String str) {
        if (str == null) {
            return null;
        }

        str = str.replace("\n", "");
        str = str.replace("\r", "");
        Pattern pattern = Pattern.compile("\\<.*?\\>");
        Matcher matcher = pattern.matcher(str);

        if (matcher.matches()) {
            return matcher.replaceAll("").trim();
        }

        return str;
    }

    Call call;
    Call tokenCall;
    Runnable runnable = () -> request();

    private void requestSuccess(Object res) {
        if (listener != null) {
            Object[] resTemp = new Object[object.length + 1];
            resTemp[0] = res;
            for (int i = 0; i < object.length; i++) {
                resTemp[i + 1] = object[i];
            }
            listener.onResponse(runnable, resTemp);
        }
    }

    private void reloadPress(boolean v) {
        if (listener != null) {
            listener.onReloadPress(v);
        }
    }

    private void requestFailed(int code, Exception e) {

        if (listener != null)
            listener.onFailure(runnable, e);
        Log.e(TAG, "requestFailed: ", e);
        switch (code) {
            case -1:
//        DBIO.setFail(MyApplication.context, url);
                showError("عدم دسترسی به اینترنت لطفا پس از بررسی ارتباط دستگاه خود به اینترنت و اطمینان از ارتباط، مجدد تلاش نمایید.");
                break;
            case -3:
                showError("آدرس وارد شده نا معتبر میباشد لطفا با پشتیبانی تماس حاصل نمایید");
                break;
            case 400:
                showError("خطای 400 : مشکلی در ارسال داده به وجود آمده است لطفا پس از چند لحظه مجدد تلاش نمایید در صورت عدم برطرف شدن، لطفا با پشتیبانی تماس حاصل نمایید.");
                break;
            case 401:
//        DBIO.setFail(MyApplication.context, url);
                showError("خطای 401 : عدم دسترسی به شبکه لطفا با پشتیبانی تماس حاصل نمایید.");
                break;
            case 403:
                showError("خطای 403 : عدم مجوز دسترسی به شبکه لطفا با پشتیبانی تماس حاصل نمایید.");
                break;
            case 404:
//        DBIO.setFail(MyApplication.context, url);
                showError("خطای 404 : برای چنین درخواستی پاسخی وجود ندارد لطفا با پشتیبانی تماس حاصل نمایید.");
                break;
            case 422://error entity
                if (ignore422) {
                    showMessage();
                } else {
                    showError("خطای 422 : متاسفانه اطلاعات ارسالی ناقص است لطفا با پشتیبانی تماس بگیرد");
                }
                break;
            case 500:
                showError("خطای 500 : مشکلی در پردازش داده به وجود آمده است لطفا پس از چند لحظه مجدد تلاش نمایید در صورت عدم برطرف شدن، لطفا با پشتیبانی تماس حاصل نمایید.");
                break;
            default:
                showError("خطای " + code + " : خطایی تعریف نشده در سیستم به وجود آمده لطفا با پشتیبانی تماس حاصل نمایید.");
                break;
        }
    }

    private void showMessage() {
        MyApplication.handler.post(() -> {
            //TODO correct this in next version
//    Unprocessable Entity{"message":"Unprocessable Entity","data":[{"field":"stationCode","message":"کد ایستگاه صحیح نیست"}],"success":false}
//      try {
//        JSONObject dataObj = new JSONObject(error);
//        boolean success = dataObj.getBoolean("success");
//        JSONArray dataArr = dataObj.getJSONArray("data");
//        String message = "";
//        if (!success) {
//          for (int i = 0; i < dataArr.length(); i++) {
//            JSONObject object = dataArr.getJSONObject(i);
//            message = message + object.getString("message") + "\n";
//          }

            new GeneralDialog()
                    .title("هشدار")
                    .message("اطلاعات صحیح نمیباشد")
                    .cancelable(false)
                    .firstButton("باشه", null)
                    .show();
//        }

//      } catch (JSONException e) {
//        e.printStackTrace();
//      }
        });
    }

    private static ErrorDialog errorDialog;

    public void showError(final String message) {
        if (!errorHandling) return;
        try {
            MyApplication.handler.post(() -> {
//                dismiss();
//                show(message);
                if (hideNetworkError)
                    return;
                if (errorDialog == null) {
                    errorDialog = new ErrorDialog();
                    errorDialog.titleText("خطایی رخ داده است");
                    errorDialog.messageText(message);
                    errorDialog.cancelable(false);
                    errorDialog.closeBtnRunnable("بستن", () -> errorDialog.dismiss());
                    errorDialog.tryAgainBtnRunnable("تلاش مجدد", new Runnable() {
                        @Override
                        public void run() {
                            runnable.run();
                        }
                    });
                }
                ErrorDialog.dismiss();
                errorDialog.show();
            });
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "RequestHelper class, showError method ");
        }
        Log.d(TAG, "showError: " + message);
    }

    private Dialog dialog;

    public void show(String message) {

        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_error);

        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(wlp);

        dialog.setCancelable(false);

        TextView txtMessage = (TextView) dialog.findViewById(R.id.txtMessage);
        txtMessage.setText(message);

        TextView title = (TextView) dialog.findViewById(R.id.txtTitle);

        title.setText("خطایی رخ داده");

        Button btnClose = dialog.findViewById(R.id.btnClose);
        Button btnTryAgain = dialog.findViewById(R.id.btnTryAgain);


        btnClose.setOnClickListener(v -> {
            if (dialog != null) {
                dialog.dismiss();
                reloadPress(false);
            }
        });

        btnTryAgain.setOnClickListener(v -> {
//      if (runnable != null)
//        vfRetry.setDisplayedChild(1);
            dialog.dismiss();
            runnable.run();
            reloadPress(true);

//      MyApplication.handler.postDelayed(new Runnable() {
//        @Override
//        public void run() {
//
//          runnable.run();
//          if (dialog != null)
//            dialog.dismiss();
//          dialog = null;
//        }
//      }, 2000);
        });


        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "RequestHelper class, show method ");
        }
    }

    private void dismiss() {
        try {
            if (dialog != null)
                dialog.dismiss();
        } catch (Exception e) {
            Log.e(TAG, "dismiss: " + e.getMessage());
            AvaCrashReporter.send(e, "RequestHelper class, dismiss method ");
        }
        dialog = null;
    }

}

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
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/***
 * Created by Amirreza Erfanian on 2018/01/12.
 * implementation 'com.squareup.okhttp3:okhttp:3.10.0'
 */
public class RequestHelper implements Callback {

  public static final String TAG = RequestHelper.class.getSimpleName();
  public static final String POST = "POST";
  public static final String GET = "GET";
  public static final String DELETE = "DELETE";
  public static final String PUT = "PUT";
  public static final String HEAD = "HEAD";
  public static final String PATCH = "PATCH";

  private static RequestHelper instance;
  private String url = null;
  private String path = null;
  private Callback listener = null;
  private JSONObject params = null;
  private ArrayList<String> paths = null;
  private boolean errorHandling = true;
  private Request req;
  private Object[] object;

  public interface Callback {
    void onResponse(Runnable reCall, Object... args);

    void onFailure(Runnable reCall, Exception e);
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
      Log.i(TAG, "addParam: "+params);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return this;
  }

  public RequestHelper addPath(String value) {
    if (paths == null)
      paths = new ArrayList<>();
    paths.add(value);
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

  private String getUrl() {
    String url = this.url;
    if (path != null) {
      String address = EndPoints.IP;
      url = "http://" + address + this.path;
    }

    this.url = url;
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
        }
      }
    }
    if (paths != null) {
      for (String ob : paths) {
        urlBuilder.addPathSegment(ob);
      }
    }
    String url = urlBuilder.build().toString();
    req = new Request.Builder()
            .url(url)
            .build();

    request();

  }

  public void post() {
    url = getUrl();
    if (url == null) return;

    RequestBody body = RequestBody.create(JSON, params.toString());
    req = new Request.Builder()
            .url(url)
            .post(body)
            .build();

    request();

  }

  public void put() {
    url = getUrl();
    if (url == null) return;

    RequestBody body = RequestBody.create(JSON, params.toString());
    req = new Request.Builder()
            .url(url)
            .put(body)
            .build();
    request();

  }

  public void delete() {
    url = getUrl();
    if (url == null) return;

    RequestBody body = RequestBody.create(JSON, params.toString());
    req = new Request.Builder()
            .url(url)
            .delete(body)
            .build();
    request();
  }

  private void request() {

    try {
    log("request url : " + req.url().toString());
    log("request params : " + params);
    log("paths : "+paths);
      OkHttpClient.Builder builder = new OkHttpClient
              .Builder()
              .proxy(Proxy.NO_PROXY);

      OkHttpClient okHttpClient = builder.connectTimeout(connectionTimeout, TimeUnit.SECONDS)
              .writeTimeout(writeTimeout, TimeUnit.SECONDS)
              .readTimeout(readTimeout, TimeUnit.SECONDS)
              .build();

      call = okHttpClient.newCall(req);
      call.enqueue(this);

    } catch (final Exception e) {
      requestFailed(REQUEST_CRASH, e);
    }
  }

  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  private int connectionTimeout = 10;
  private int writeTimeout = 10;
  private int readTimeout = 20;
  private Object object1 = null;
  private Object object2 = null;

  public static final int INTERNET_CONNECTION_EXCEPTION = -1;
  public static final int REQUEST_CRASH = -2;

  @Override
  public void onFailure(Call call, final IOException e) {
    this.call = call;
    log("request failed :  The requested URL can't be Reached The service took too long to respond.");
    if (listener != null)
      requestFailed(INTERNET_CONNECTION_EXCEPTION, e);
  }

  @Override
  public void onResponse(Call call, final Response response) {
    this.call = call;
    if (listener != null) {
      final String bodyStr;
      try {
        bodyStr = parseXML(response.body().string());
        log("request result : " + bodyStr);

        if (!response.isSuccessful()) {
          requestFailed(response.code(), new Exception(response.message() + bodyStr));
        } else {
          if (object == null)
            object = new Object[0];
          requestSuccess(bodyStr);
        }

        //check response code true 200

//        MyApplication.handler.post(new Runnable() {
//          @Override
//          public void run() {
//        try {
//              JSONObject res = new JSONObject(bodyStr);
//              boolean status = res.getBoolean("success");
//              String message = res.getString("message");
//              if (status) {
//                if (response.isSuccessful()) {
//                  if (object == null)
//                    object = new Object[0];
//                  requestSuccess(bodyStr);
//                }
//              }
//              else {
//                requestFailed(response.code(), new Exception(response.message() + bodyStr));
//              }
//            } catch (IOException e) {
//              if (object==null) {
//                object = new Object[0];
//              }
//              if (response.isSuccessful())
//                requestSuccess(bodyStr);
//              else
//                requestFailed(response.code(), e);
//            }
//          }
//
//        });

      } catch (final IOException e) {
        requestFailed(response.code(), e);
        if (listener != null)
          listener.onFailure(runnable, e);

      }
    }
  }
//    }

  /**
   * manage log
   *
   * @param v
   */
  private void log(String v) {
    Log.d(TAG, "====> " + v);
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
  Runnable runnable = new Runnable() {
    @Override
    public void run() {
      request();
    }
  };

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
        showError("خطای 422 : متاسفانه اطلاعات ارسالی ناقص است لطفا با پشتیبانی تماس بگیرد");
        break;
      case 500:
        showError("خطای 500 : مشکلی در پردازش داده به وجود آمده است لطفا پس از چند لحظه مجدد تلاش نمایید در صورت عدم برطرف شدن، لطفا با پشتیبانی تماس حاصل نمایید.");
        break;
      default:
        showError("خطای " + code + " : خطایی تعریف نشده در سیستم به وجود آمده لطفا با پشتیبانی تماس حاصل نمایید.");
        break;
    }
  }

  private void showError(final String message) {
    if (!errorHandling) return;
    try {
//      if (MyApplication.prefManager.isAppRun()) {
      MyApplication.handler.post(() -> {
        dismiss();
        show(message);
      });
//      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    Log.d(TAG, "showError: " + message);
  }

  private Dialog dialog;

  public void show(String message) {
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
      if (dialog != null)
        dialog.dismiss();
    });

    btnTryAgain.setOnClickListener(v -> {
//      if (runnable != null)
//        vfRetry.setDisplayedChild(1);
      MyApplication.handler.postDelayed(new Runnable() {
        @Override
        public void run() {

          runnable.run();
          if (dialog != null)
            dialog.dismiss();
          dialog = null;
        }
      }, 2000);
    });

    dialog.show();
  }

  private void dismiss() {
    try {
      if (dialog != null)
        dialog.dismiss();
    } catch (Exception e) {
      Log.e(TAG, "dismiss: " + e.getMessage());
    }
    dialog = null;
  }

}

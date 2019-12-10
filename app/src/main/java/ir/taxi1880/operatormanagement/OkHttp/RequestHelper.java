package ir.taxi1880.operatormanagement.OkHttp;

import android.content.Context;
import android.util.Log;


import ir.taxi1880.operatormanagement.app.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/***
 * Created by Amirreza Erfanian on 2018/July/26.
 * currentVersion 1.0.1
 ***************** version changes *******************
 * v : 1.0.0 create project
 * v : 1.0.1 fix bug run reCall with returnValue
 *
 ****************** Readme *******************
 * add to your BuildGradle
 * implementation 'com.squareup.okhttp3:okhttp:3.10.0'
 */
public class RequestHelper implements Callback {


  public static final String TAG = RequestHelper.class.getSimpleName();
  private String url = null;
  private Callback listener = null;
  private JSONObject params = null;
  public static final int POST = 1;
  public static final int GET = 0;
  private int method = GET;
  private Boolean isKey = true;
  private static RequestHelper instance = null;
  private Context context;

  public interface Callback {
    void onResponse(Runnable reCall, Object... args);

    void onFailure(Runnable reCall, Exception e);
  }

  public RequestHelper isKey(Boolean isKey) {
    instance.isKey = isKey;
    return instance;
  }

  public RequestHelper method(int method) {
    instance.method = method;
    return instance;
  }

  public RequestHelper params(JSONObject params) {
    try {
      Log.i(TAG, "Key: " + instance.isKey);
      if (instance.isKey) {
        params.put("key", MyApplication.prefManager.getKey());
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    instance.params = params;
    return instance;
  }

  public RequestHelper listener(Callback listener) {
    instance.listener = listener;
    return instance;
  }

  public RequestHelper readTimeout(int readTimeout) {
    instance.readTimeout = readTimeout;
    return instance;
  }

  public RequestHelper connectionTimeout(int connectionTimeout) {
    instance.connectionTimeout = connectionTimeout;
    return instance;
  }

  public RequestHelper writeTimeout(int writeTimeout) {
    instance.writeTimeout = writeTimeout;
    return instance;
  }

  public static RequestHelper builder(String url) {
    instance = new RequestHelper();
    instance.url = url;

    return instance;
  }

  /**
   * only two params can you send to listener
   *
   * @param returnValueToListener
   */
  public void request(Object... returnValueToListener) {
    try {
      Log.d(TAG, "Request to : " + instance.url);
      Log.d(TAG, "params : ");
      Log.d(TAG, "                              " + instance.params.toString());

      if (returnValueToListener != null) {
        if (returnValueToListener.length > 0)
          object1 = returnValueToListener[0];
        if (returnValueToListener.length > 1)
          object2 = returnValueToListener[1];

      }
      if (instance.url == null) {
        Log.e(TAG, "ERROR : The 'URL' field is not filled");
        return;
      }
      if (instance.isKey == null) {
        isKey = true;
      }
      if (instance.params == null) params = new JSONObject();

      OkHttpClient client = null;
      Request request;

      OkHttpClient.Builder builder = new OkHttpClient
              .Builder()
              .proxy(Proxy.NO_PROXY);

      client = builder
              .connectTimeout(connectionTimeout, TimeUnit.SECONDS)
              .writeTimeout(writeTimeout, TimeUnit.SECONDS)
              .readTimeout(readTimeout, TimeUnit.SECONDS)
              .build();

      if (instance.method == GET) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(instance.url).newBuilder();
        Iterator<String> iter = instance.params.keys();
        while (iter.hasNext()) {
          String key = iter.next();
          try {
            String value = instance.params.getString(key);
            urlBuilder.addQueryParameter(key, value);
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
        String url = urlBuilder.build().toString();

        request = new Request.Builder()
                .url(url).build();
      } else {
        RequestBody body = RequestBody.create(JSON, params.toString());
        request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
      }

      client.newCall(request)
              .enqueue(this);
    } catch (final Exception e) {
      e.printStackTrace();
      new ResponseCodeHelper().responseCode(INTERNET_CONNECTION_EXCEPTION, new Runnable() {
        @Override
        public void run() {
          request(object1, object2);
        }
      });
      listener.onFailure(reCall, e);
    }
  }

  Runnable reCall = new Runnable() {
    @Override
    public void run() {
      request(object1, object2);
    }
  };

  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  private int connectionTimeout = 10;
  private int writeTimeout = 10;
  private int readTimeout = 20;
  private Object object1 = null;
  private Object object2 = null;

  public static final int INTERNET_CONNECTION_EXCEPTION = -1;

  @Override
  public void onFailure(Call call, final IOException e) {

    JSONObject error = new JSONObject();
    try {
      error.put("error", "onFailure: The requested URL can't be Reached The service took too long to respond.");
      error.put("responseCode", INTERNET_CONNECTION_EXCEPTION);
    } catch (JSONException e1) {
      e1.printStackTrace();
    }

    new ResponseCodeHelper().responseCode(INTERNET_CONNECTION_EXCEPTION, new Runnable() {
      @Override
      public void run() {
        request(object1, object2);
      }
    });

    Log.i(TAG, "Result => from : " + call.request().url());
    Log.e(TAG, "Result => onFailure: The requested URL can't be Reached The service took too long to respond.");

    if (listener != null)
      listener.onFailure(reCall, e);

  }

  @Override
  public void onResponse(Call call, final Response response) {
    final Object bodyStr;
    try {
      bodyStr = parse(response.body().string());
      if (!response.isSuccessful()) {
        new ResponseCodeHelper().responseCode(response.code(), new Runnable() {
          @Override
          public void run() {
            request(object1, object2);
          }
        });

        if (listener != null)
          listener.onFailure(reCall, new IOException("Unexpected code " + response));

        Log.i(TAG, "Result => from : " + call.request().url());
        Log.e(TAG, "Result => onFailure: " + response.code() + "  " + bodyStr);
        return;
      }

      Log.i(TAG, "Result => from : " + call.request().url());
      Log.i(TAG, "Result => onResponse : " + bodyStr);

      if (listener != null) {
        listener.onResponse(reCall, bodyStr, object1, object2);
      }

    } catch (final IOException e) {
      e.printStackTrace();

      if (listener != null)
        listener.onFailure(reCall, e);

    }
  }

  public static String parse(String str) {
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

}

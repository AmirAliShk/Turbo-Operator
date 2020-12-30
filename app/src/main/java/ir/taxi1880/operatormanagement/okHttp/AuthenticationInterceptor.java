package ir.taxi1880.operatormanagement.okHttp;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.ErrorDialog;
import ir.taxi1880.operatormanagement.fragment.LoginFragment;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class AuthenticationInterceptor implements Interceptor {
    private static final String TAG_THIS = AuthenticationInterceptor.class.getSimpleName();
    //--- HTTP Response codes relative constants
    private static final int RESPONSE_UNAUTHORIZED_401 = 401;
    private static final int RESPONSE_HTTP_RANK_2XX = 2;
    private static final int RESPONSE_HTTP_CLIENT_ERROR = 4;
    private static final int RESPONSE_HTTP_SERVER_ERROR = 5;
    Request request;
    Request.Builder builder;

    @Override
    public Response intercept(Chain chain) throws IOException {

        request = chain.request();                                          //<<< Original Request

        builder = request.newBuilder();                                     //Build new request

        String authorization = MyApplication.prefManager.getAuthorization();//Save token of this request for future
        String idToken = MyApplication.prefManager.getIdToken();            //Save token of this request for future
        setAuthHeader(builder, authorization, idToken);                     //Add Current Authentication Token..

        request = builder.build();                                          //Overwrite the original request

        Response response = chain.proceed(request);                         // Sends the request (Original w/ Auth.)

        if (response.code() == 401) {
            Log.w(TAG_THIS, "Request responses code: " + response.code());
            Log.w(TAG_THIS, "Request responses url: " + response.request().url());
            synchronized (this) {                                       // Gets all 401 in sync blocks,

                int code = refreshToken() / 100;                        //Refactor resp. cod ranking

                if (code != RESPONSE_HTTP_RANK_2XX) {                   // If refresh token failed
                    if (code == RESPONSE_HTTP_CLIENT_ERROR || code == RESPONSE_HTTP_SERVER_ERROR) {
                        logout();
//                        return response; //TODO return response is wrong,
                    }
                }

                // --- --- RETRYING ORIGINAL REQUEST --- --- RETRYING ORIGINAL REQUEST --- --------|
                if (code == 2) {                  // Checks new Auth. Token
                    setAuthHeader(builder, MyApplication.prefManager.getAuthorization(), MyApplication.prefManager.getIdToken());   // Add Current Auth. Token
                    request = builder.build();

                    Response responseRetry = chain.proceed(request);     // Sends request (w/ New Auth.)
                    Log.w(TAG_THIS, "Request responses new url: " + response.request().url());

                    return responseRetry;
                }
            }
        }

        return response;

    }

    private void setAuthHeader(Request.Builder builder, String authorization, String idToken) {
        builder.header("Authorization", authorization);
        builder.header("id_token", idToken);
    }

    private int refreshToken() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("token", MyApplication.prefManager.getRefreshToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonType, json.toString());
        Request request = new Request.Builder()
                .url(EndPoints.REFRESH_TOKEN)
                .post(body)
                .build();

        Response response;
        int code = 0;

        try {
            response = client.newCall(request).execute();

            Log.i(TAG_THIS, "refreshToken : input " + json.toString());
            Log.i(TAG_THIS, "refreshToken : url " + request.url().toString());

            if (response != null) {
                code = response.code();

                if (code == 200) {
                    try {
                        JSONObject jsonBody = new JSONObject(response.body().string());
                        boolean success = jsonBody.getBoolean("success");
                        String message = jsonBody.getString("message");

                        Log.i(TAG_THIS, "refreshToken : jsonBody " + jsonBody.toString());

                        if (success) {
                            JSONObject objData = jsonBody.getJSONObject("data");
                            String id_token = objData.getString("id_token");
                            String access_token = objData.getString("access_token");
                            MyApplication.prefManager.setAuthorization(access_token);
                            MyApplication.prefManager.setIdToken(id_token);
                        } else {
                            MyApplication.prefManager.setAuthorization(""); // TODO empty?
                            MyApplication.prefManager.setIdToken("");
                            logout();
//                            return 400; //TODO return 400?
                        }

                    } catch (JSONException e) {
                        e.getMessage();
                    }
                }

                response.body().close(); //ToDo check this line
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
    }

    private void logout() {
        MyApplication.handler.post(() -> {
            FragmentHelper
                    .toFragment(MyApplication.currentActivity, new LoginFragment())
                    .setAddToBackStack(false)
                    .replace();
        });
    }
}
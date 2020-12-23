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

        builder = request.newBuilder();                     //Build new request

        String authorization = MyApplication.prefManager.getAuthorization();//Save token of this request for future
        String idToken = MyApplication.prefManager.getIdToken();            //Save token of this request for future
        setAuthHeader(builder, authorization, idToken);                     //Add Current Authentication Token..

        request = builder.build();                                          //Overwrite the original request

        Response response = chain.proceed(request);                         // Sends the request (Original w/ Auth.)

        switch (response.code()) { //TODO remove toast...
            case -1:
                MyApplication.Toast("-1 response", Toast.LENGTH_SHORT);
                break;
            case -3:
                MyApplication.Toast("-3 response", Toast.LENGTH_SHORT);
                break;
            case 400:
                MyApplication.Toast("400 response", Toast.LENGTH_SHORT);
                break;
            case 401:
                Log.w(TAG_THIS, "Request responses code: " + response.code());
                Log.w(TAG_THIS, "Request responses url: " + response.request().url());
                synchronized (this) {                                       // Gets all 401 in sync blocks,

                    int code = refreshToken() / 100;                        //Refactor resp. cod ranking

                    if (code != RESPONSE_HTTP_RANK_2XX) {                   // If refresh token failed
                        if (code == RESPONSE_HTTP_CLIENT_ERROR || code == RESPONSE_HTTP_SERVER_ERROR) {
//                            logout();                                     // ToDo GoTo login screen or Shows auth error to user
                            return response;
                        }
                    }

                    // --- --- RETRYING ORIGINAL REQUEST --- --- RETRYING ORIGINAL REQUEST --- --------|
                    if (MyApplication.prefManager.getAuthorization() != null) {                  // Checks new Auth. Token
                        setAuthHeader(builder, MyApplication.prefManager.getAuthorization(), MyApplication.prefManager.getIdToken());   // Add Current Auth. Token
                        request = builder.build();                           // O/w the original request

                        //-----------------------------------------------------------------------------|
                        Response responseRetry = chain.proceed(request);     // Sends request (w/ New Auth.)
                        //-----------------------------------------------------------------------------|

                        return responseRetry;
                    }
                }
                break;
            case 403:
                MyApplication.Toast("403 response", Toast.LENGTH_SHORT);
                break;
            case 404:
                MyApplication.Toast("404 response", Toast.LENGTH_SHORT);
                Log.i("TAG", "intercept: 4000000000000000000000000000004");
                showError("4000000000000000004");
                break;
            case 422://error entity
                MyApplication.Toast("422 response", Toast.LENGTH_SHORT);
                break;
            case 500:
                MyApplication.Toast("500 response", Toast.LENGTH_SHORT);
                break;
            default:
                MyApplication.Toast("other response", Toast.LENGTH_SHORT);
                break;
        }
        return response;

    }

    // Sets/Adds the authentication header to current request builder.-----------------------------|
    private void setAuthHeader(Request.Builder builder, String authorization, String idToken) {
        builder.header("Authorization", authorization);
        builder.header("id_token", idToken);
    }

    // Refresh/renew Synchronously Authentication Token & refresh token----------------------------|
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

            if (response != null) {
                code = response.code();

                switch (code) {
                    case 200:
                        try {
                            JSONObject jsonBody = new JSONObject(response.body().string());
                            boolean success = jsonBody.getBoolean("success");
                            String message = jsonBody.getString("message");

                            Log.i(TAG_THIS, "refreshToken : url " + request.url().toString());
                            Log.i(TAG_THIS, "refreshToken : jsonBody " + jsonBody.toString());

                            if (success) {
                                JSONObject objData = jsonBody.getJSONObject("data");
                                String id_token = objData.getString("id_token");
                                String access_token = objData.getString("access_token");
                                MyApplication.prefManager.setAuthorization(access_token+"dsgdfg");
                                MyApplication.prefManager.setIdToken(id_token+"sdgdetrf");
                            } else {
                                logout();
                            }
                        } catch (JSONException e) {
                            e.getMessage();
                        }
                        break;

                    case 401:
                        MyApplication.Toast("401 refresh Token", Toast.LENGTH_SHORT);
                        break;

                    case 404:
                        MyApplication.Toast("404 refresh Token", Toast.LENGTH_SHORT);
                        break;
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

    private static ErrorDialog errorDialog;

    public void showError(final String message) {
        try {
            MyApplication.handler.post(() -> {
                if (errorDialog == null) {
                    errorDialog = new ErrorDialog();
                    errorDialog.titleText("خطایی رخ داده است");
                    errorDialog.messageText(message);
                    errorDialog.cancelable(false);
                    errorDialog.closeBtnRunnable("بستن", () -> errorDialog.dismiss());
                    errorDialog.tryAgainBtnRunnable("تلاش مجدد", new Runnable() {
                        @Override
                        public void run() {
//                            runnable.run();

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
    }

}
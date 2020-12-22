package ir.taxi1880.operatormanagement.okHttp;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ir.taxi1880.operatormanagement.activity.MainActivity;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.dialog.ErrorDialog;
import ir.taxi1880.operatormanagement.fragment.LoginFragment;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.publicAPI.RefreshToken;
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
    //--- My backend params
    private static final String BODY_PARAM_KEY_GRANT_TYPE = "grant_type";
    private static final String BODY_PARAM_VALUE_GRANT_TYPE = "refresh_token";
    private static final String BODY_PARAM_KEY_REFRESH_TOKEN = "refresh_token";


    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();                  //<<< Original Request

        //Build new request-----------------------------
        Request.Builder builder = request.newBuilder();

        String authorization = MyApplication.prefManager.getAuthorization();            //Save token of this request for future
        String idToken = MyApplication.prefManager.getIdToken();            //Save token of this request for future
        setAuthHeader(builder, authorization, idToken);                      //Add Current Authentication Token..

        request = builder.build();                          //Overwrite the original request

        Log.d(TAG_THIS,
                ">>> Sending Request >>>\n"
                        + "To: " + request.url() + "\n"
                        + "Headers:" + request.headers() + "\n"
                        + "Body: " + bodyToString(request));   //Shows the magic...

        //------------------------------------------------------------------------------------------
        Response response = chain.proceed(request);         // Sends the request (Original w/ Auth.)
        //------------------------------------------------------------------------------------------

        Log.d(TAG_THIS,
                "<<< Receiving Request response <<<\n"
                        + "To: " + response.request().url() + "\n"
                        + "Headers: " + response.headers() + "\n"
                        + "Code: " + response.code() + "\n"
                        + "Body: " + bodyToString(response.request()));  //Shows the magic...


        //------------------- 401 --- 401 --- UNAUTHORIZED --- 401 --- 401 -------------------------

        if (response.code() == RESPONSE_UNAUTHORIZED_401) { //If unauthorized (Token expired)...
            Log.w(TAG_THIS, "Request responses code: " + response.code());
            Log.w(TAG_THIS, "Request responses url: " + response.request().url());

            synchronized (this) {                           // Gets all 401 in sync blocks,
                // to avoid multiply token updates...

                String currentAuthorization = MyApplication.prefManager.getAuthorization();            //Save token of this request for future
                String currentIdToken = MyApplication.prefManager.getIdToken();            //Save token of this request for future

                //Compares current token with token that was stored before,
                // if it was not updated - do update..

                if (currentAuthorization != null && currentAuthorization.equals(authorization)) {

                    // --- REFRESHING TOKEN --- --- REFRESHING TOKEN --- --- REFRESHING TOKEN ------

                    int code = refreshToken() / 100;                    //Refactor resp. cod ranking

                    if (code != RESPONSE_HTTP_RANK_2XX) {                // If refresh token failed

                        if (code == RESPONSE_HTTP_CLIENT_ERROR           // If failed by error 4xx...
                                ||
                                code == RESPONSE_HTTP_SERVER_ERROR) {   // If failed by error 5xx...

                            logout();                                   // ToDo GoTo login screen
                            return response;                            // Todo Shows auth error to user
                        }
                    }   // <<--------------------------------------------New Auth. Token acquired --
                }   // <<-----------------------------------New Auth. Token acquired double check --


                // --- --- RETRYING ORIGINAL REQUEST --- --- RETRYING ORIGINAL REQUEST --- --------|

                if (MyApplication.prefManager.getAuthorization() != null) {                  // Checks new Auth. Token
                    setAuthHeader(builder, MyApplication.prefManager.getAuthorization(), MyApplication.prefManager.getIdToken());   // Add Current Auth. Token
                    request = builder.build();                          // O/w the original request

                    Log.d(TAG_THIS,
                            ">>> Retrying original Request >>>\n"
                                    + "To: " + request.url() + "\n"
                                    + "Headers:" + request.headers() + "\n"
                                    + "Body: " + bodyToString(request));  //Shows the magic...


                    //-----------------------------------------------------------------------------|
                    Response responseRetry = chain.proceed(request);// Sends request (w/ New Auth.)
                    //-----------------------------------------------------------------------------|


                    Log.d(TAG_THIS,
                            "<<< Receiving Retried Request response <<<\n"
                                    + "To: " + responseRetry.request().url() + "\n"
                                    + "Headers: " + responseRetry.headers() + "\n"
                                    + "Code: " + responseRetry.code() + "\n"
                                    + "Body: " + bodyToString(response.request()));  //Shows the magic.

                    return responseRetry;
                }
            }
        } else {
            //------------------- 200 --- 200 --- AUTHORIZED --- 200 --- 200 -----------------------
            Log.w(TAG_THIS, "Request responses code: " + response.code());
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
        Log.w(TAG_THIS, "Refreshing tokens... ;o");

        // Builds a client...
        OkHttpClient client = new OkHttpClient.Builder().build();

        // Builds a Request Body...for renewing token...
        MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        //---
        try {
            json.put("token", MyApplication.prefManager.getRefreshToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //---
        RequestBody body = RequestBody.create(jsonType, json.toString());
        // Builds a request with request body...
        Request request = new Request.Builder()
                .url(EndPoints.REFRESH_TOKEN)
                .post(body)                     //<<<--------------Adds body (Token renew by the way)
                .build();


        Response response = null;
        int code = 0;


        Log.d(TAG_THIS,
                ">>> Sending Refresh Token Request >>>\n"
                        + "To: " + request.url() + "\n"
                        + "Headers:" + request.headers() + "\n"
                        + "Body: " + bodyToString(request));  //Shows the magic...
        try {
            //--------------------------------------------------------------------------------------
            response = client.newCall(request).execute();       //Sends Refresh token request
            //--------------------------------------------------------------------------------------

            Log.d(TAG_THIS,
                    "<<< Receiving Refresh Token Request Response <<<\n"
                            + "To: " + response.request().url() + "\n"
                            + "Headers:" + response.headers() + "\n"
                            + "Code: " + response.code() + "\n"
                            + "Body: " + bodyToString(response.request()));  //Shows the magic...

            if (response != null) {
                code = response.code();
                Log.i(TAG_THIS, "Token Refresh responses code: " + code);

                switch (code) {
                    case 200:
                        // READS NEW TOKENS AND SAVES THEM -----------------------------------------
                        try {
                            //Log.i(TAG_ALIEN+TAG_THIS,"Decoding tokens start");
                            JSONObject jsonBody = new JSONObject(response.body().string());
                            boolean success = jsonBody.getBoolean("success");
                            String message = jsonBody.getString("message");

                            Log.i(TAG_THIS, "refreshToken : url " + request.url().toString());
                            Log.i(TAG_THIS, "refreshToken : jsonBody " + jsonBody.toString());

                            if (success) {
                                JSONObject objData = jsonBody.getJSONObject("data");
                                String id_token = objData.getString("id_token");
                                String access_token = objData.getString("access_token");
                                MyApplication.prefManager.setAuthorization(access_token);
                                MyApplication.prefManager.setIdToken(id_token);
                            } else {
                                logout();
                            }

                        } catch (JSONException e) {
                            Log.w(TAG_THIS, "Responses code " + code
                                    + " but error getting response body.\n"
                                    + e.getMessage());
                        }

                        break;
                }
//                response.body().close(); //ToDo check this line
            }

        } catch (IOException e) {
            Log.w(TAG_THIS, "Error while Sending Refresh Token Request\n" + e.getMessage());
            e.printStackTrace();
        }

        //Log.w(TAG_ALIEN,"Refresh Token request responses code? = "+code);
        return code;

    }

    private void logout() {
        FragmentHelper
                .toFragment(MyApplication.currentActivity, new LoginFragment())
                .setAddToBackStack(false)
                .replace();
    }

    //----------------------------------------------------------------------------------------------
    @Deprecated
    private static String bodyToString(final Request request) {
        /*
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            Log.w(TAG_ALIEN+TAG_THIS,"Error while trying to get body to string.");
            return "Null";
        }*/
        return "Nullix";
    }
}
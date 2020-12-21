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
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AuthTokenRefreshInterceptor implements Interceptor {
    static ErrorDialog errorDialog = null;
    Request request;

    @Override
    public Response intercept(Chain chain) throws IOException {

        request = chain.request();

        //Build new request
        Request.Builder builder = request.newBuilder();//if necessary, say to consume JSON

        String token = MyApplication.prefManager.getAuthorization(); //save token of this request for future

        setAuthHeader(builder); //write current token to request

        request = builder.build(); //overwrite old request
        Response response = chain.proceed(request); //perform request, here original request will be executed

        if (response.code() == 401) { //if unauthorized
//            synchronized (new RequestHelper().okHttpClient) { //perform all 401 in sync blocks, to avoid multiply token updates
            String currentToken = MyApplication.prefManager.getAuthorization(); //get currently stored token

            if (currentToken != null && currentToken.equals(token)) { //compare current token with token that was stored before, if it was not updated - do update
                new RefreshToken().refreshToken(success -> {
                    if (success) {
                        setAuthHeader(builder); //set auth token to updated
                        request = builder.build();
                        if (errorDialog == null) {
                            errorDialog = new ErrorDialog();
                            errorDialog.titleText("خطایی رخ داده است");
                            errorDialog.messageText("terrrrrrrrrrrrrrrrr");
                            errorDialog.cancelable(false);
                            errorDialog.closeBtnRunnable("بستن", () -> errorDialog.dismiss());
                            errorDialog.tryAgainBtnRunnable("تلاش مجدد", () -> {
                                Thread Thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            //TODO You are here!!
                                            // how I can call last Api again??
                                            chain.proceed(request); //repeat request with new token
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            });
                        }
                        ErrorDialog.dismiss();
                        errorDialog.show();

                    } else {
                        logout();
                    }
                });
            }

            if (MyApplication.prefManager.getAuthorization() != null) { //retry requires new auth token,
                //repeat request with new token
            }
//            }
        }

        return response;
    }


    private void setAuthHeader(Request.Builder builder) {//Add Auth token to each request if authorized
        builder.header("Authorization", MyApplication.prefManager.getAuthorization());
        builder.header("id_token", MyApplication.prefManager.getIdToken());
    }

    private void refreshToken() {
        new RefreshToken().refreshToken(success -> {
            if (success) {
                if (errorDialog == null) {
                    errorDialog = new ErrorDialog();
                    errorDialog.titleText("خطایی رخ داده است");
                    errorDialog.messageText("terrrrrrrrrrrrrrrrr");
                    errorDialog.cancelable(false);
                    errorDialog.closeBtnRunnable("بستن", () -> errorDialog.dismiss());
                    errorDialog.tryAgainBtnRunnable("تلاش مجدد", () -> {

                    });
                }
                ErrorDialog.dismiss();
                errorDialog.show();

            } else {
                logout();
            }
        });
        //Refresh token, synchronously, save it, and return result code
        //you might use retrofit here
    }

    private void logout() {
        FragmentHelper
                .toFragment(MyApplication.currentActivity, new LoginFragment())
                .setAddToBackStack(false)
                .replace();
        //logout your user
    }

}
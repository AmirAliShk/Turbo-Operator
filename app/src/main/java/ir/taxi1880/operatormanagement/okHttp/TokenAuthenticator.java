package ir.taxi1880.operatormanagement.okHttp;

import android.util.Log;

import java.io.IOException;

import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.publicAPI.RefreshToken;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {

    @Override
    public Request authenticate(Route route, Response response) throws IOException {

         new RefreshToken().refreshToken(success -> {
             if (success){
                 Log.i("TokenAuthenticator", "isRefreshed: succesSsSsSssSsSsSSSsssssSSSS");
             }
         });

        return response.request().newBuilder()
                .header("Authorization", MyApplication.prefManager.getAuthorization())
                .header("id_token", MyApplication.prefManager.getIdToken())
                .build();
    }
}

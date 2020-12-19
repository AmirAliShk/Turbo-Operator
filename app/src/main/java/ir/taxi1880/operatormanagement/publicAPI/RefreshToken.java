package ir.taxi1880.operatormanagement.publicAPI;

import org.json.JSONException;
import org.json.JSONObject;

import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.fragment.LoginFragment;
import ir.taxi1880.operatormanagement.helper.FragmentHelper;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class RefreshToken {

    public interface RefreshTokenInterface {
        void isRefreshed(boolean success);
    }

    RefreshTokenInterface refreshToken;

    public void refreshToken(RefreshTokenInterface refreshToken) {
        this.refreshToken = refreshToken;
        RequestHelper.builder(EndPoints.REFRESH_TOKEN)
                .addParam("token", MyApplication.prefManager.getRefreshToken())
                .listener(getRefreshToken)
                .post();
    }

    RequestHelper.Callback getRefreshToken = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject tokenObject = new JSONObject(args[0].toString());
                    boolean success = tokenObject.getBoolean("success");
                    String message = tokenObject.getString("message");

                    if (success) {
                        JSONObject objData = tokenObject.getJSONObject("data");
                        String id_token = objData.getString("id_token");
                        String access_token = objData.getString("access_token");
                        MyApplication.prefManager.setAuthorization(access_token);
                        MyApplication.prefManager.setIdToken(id_token);
                        refreshToken.isRefreshed(true);
                    } else {
                        refreshToken.isRefreshed(false);
                        FragmentHelper
                                .toFragment(MyApplication.currentActivity, new LoginFragment())
                                .setAddToBackStack(false)
                                .replace();
                        //TODO what to do?
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            super.onFailure(reCall, e);
        }
    };

}

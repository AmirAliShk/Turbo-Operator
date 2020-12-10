package ir.taxi1880.operatormanagement.publicAPI;

import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class RefreshToken {

    public interface RefreshTokenInterface {
        void isRefreshed(boolean success);
    }

    RefreshTokenInterface refreshToken;

    public void refreshToken(RefreshTokenInterface refreshToken) {
        this.refreshToken = refreshToken;
        RequestHelper.builder(EndPoints.REFRESH_TOKEN)
                .listener(getRefreshToken)
                .get();
    }

    RequestHelper.Callback getRefreshToken = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            boolean success = true;
            if (success) {
                refreshToken.isRefreshed(true);
                //save new value of token_id into prefManager
                //save new value of token_access into prefManager
            }
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            super.onFailure(reCall, e);
        }
    };

}

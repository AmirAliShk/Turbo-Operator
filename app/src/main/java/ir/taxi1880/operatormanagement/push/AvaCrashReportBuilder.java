package ir.taxi1880.operatormanagement.push;

import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.AppVersionHelper;
import ir.taxi1880.operatormanagement.okHttp.RequestHelper;

public class AvaCrashReportBuilder {
// sample: new AvaCrashReportBuilder().isCatch(true).setFunctionName("splash getAppInfoResponse").setInputParams(args[0].toString()).send(e);

    public final String TAG = AvaCrashReportBuilder.class.getSimpleName();
    boolean isCatch = true;
    //TODO change project id for each application
    int projectId = 5;
    String functionName = "";
    String inputParams = "";

    public AvaCrashReportBuilder isCatch(boolean isCatch) {
        this.isCatch = isCatch;
        return this;
    }

    public AvaCrashReportBuilder setProjectId(int projectId) {
        this.projectId = projectId;
        return this;
    }

    public AvaCrashReportBuilder setFunctionName(String functionName) {
        this.functionName = functionName;
        return this;
    }

    public AvaCrashReportBuilder setInputParams(String inputParams) {
        this.inputParams = inputParams;
        return this;
    }

    public void send(Exception exception) {

        JSONObject customData = new JSONObject();
        try {
            customData.put("LineCode", MyApplication.prefManager.getUserCode());
            customData.put("IS_CATCH", isCatch);
            customData.put("projectId", projectId);
            customData.put("CATCH_ID", functionName);
            customData.put("CATCH_INPUT_PARAMS", inputParams);
            customData.put("CATCH_LINE_NUMBER", AvaSocket.getSocketParams());
            RequestHelper.builder("http://turbotaxi.ir:6061/api/crashReport")
                    .addParam("APP_VERSION_CODE", new AppVersionHelper(MyApplication.context).getVerionCode())
                    .addParam("APP_VERSION_NAME", new AppVersionHelper(MyApplication.context).getVerionName())
                    .addParam("PACKAGE_NAME", MyApplication.context.getPackageName())
                    .addParam("PHONE_MODEL", Build.MODEL)
                    .addParam("BRAND", Build.BRAND)
                    .addParam("ANDROID_VERSION", Build.VERSION.RELEASE)
                    .addParam("TOTAL_MEM_SIZE", "")
                    .addParam("AVAILABLE_MEM_SIZE", "")
                    .addParam("IS_SILENT", "")
                    .addParam("CUSTOM_DATA", customData)
                    .addParam("STACK_TRACE", Log.getStackTraceString(exception))
                    .addParam("INITIAL_CONFIGURATION", "")
                    .addParam("USER_APP_START_DATE", "")
                    .addParam("USER_CRASH_DATE", "")
                    .addParam("DEVICE_FEATURES", "")
                    .post();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}

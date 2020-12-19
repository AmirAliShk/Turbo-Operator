package ir.taxi1880.operatormanagement.helper;

import android.content.Intent;

import ir.taxi1880.operatormanagement.activity.MainActivity;
import ir.taxi1880.operatormanagement.app.MyApplication;

public class ContinueProcessing {

    public static void runMainActivity() {
        MyApplication.handler.post(() -> {
            MyApplication.currentActivity.startActivity(new Intent(MyApplication.currentActivity, MainActivity.class));
            MyApplication.currentActivity.finish();
        });

    }
}

package ir.taxi1880.operatormanagement.helper;

import android.content.Intent;

import ir.taxi1880.operatormanagement.activity.MainActivity;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.fragment.ContractFragment;
import ir.taxi1880.operatormanagement.fragment.LoginFragment;

public class ContinueProcessing {

    public static void runMainActivity() {
            if (MyApplication.currentActivity.toString().contains(MainActivity.TAG)) {
                FragmentHelper
                        .taskFragment(MyApplication.currentActivity, LoginFragment.TAG)
                        .remove();
                return;
            }
        MyApplication.handler.post(() -> {
            MyApplication.currentActivity.startActivity(new Intent(MyApplication.currentActivity, MainActivity.class));
            MyApplication.currentActivity.finish();
        });

    }
}

package ir.taxi1880.operatormanagement.helper;

import android.content.Intent;

import ir.taxi1880.operatormanagement.activity.MainActivity;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.fragment.CheckVerificationFragment;
import ir.taxi1880.operatormanagement.fragment.LoginFragment;

import static ir.taxi1880.operatormanagement.app.MyApplication.avaStart;

public class ContinueProcessing {

    public static void runMainActivity() {
        //if this not checked, the program will be closed when login fragment open from mainActivity

            if (MyApplication.currentActivity.toString().contains(MainActivity.TAG)) {
                FragmentHelper
                        .taskFragment(MyApplication.currentActivity, LoginFragment.TAG)
                        .remove();
                FragmentHelper
                        .taskFragment(MyApplication.currentActivity, CheckVerificationFragment.TAG)
                        .remove();
                return;
            }

        if (MyApplication.prefManager.getUserCode() != 0) {
            avaStart();
        }

        MyApplication.handler.post(() -> {
            MyApplication.currentActivity.startActivity(new Intent(MyApplication.currentActivity, MainActivity.class));
            MyApplication.currentActivity.finish();
        });

    }
}

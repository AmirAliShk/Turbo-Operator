package com.example.operatormanagement.OkHttp;

import com.example.operatormanagement.app.MyApplication;
import com.example.operatormanagement.dialog.ErrorDialog;

public class ResponseCodeHelper {

  private static final String TAG = ResponseCodeHelper.class.getSimpleName();

  public void responseCode(final int code, final Runnable runnable) {
    switch (code) {
      case -1:
        showError(runnable, "عدم دسترسی به اینترنت لطفا پس از بررسی ارتباط دستگاه خود به اینترنت و اطمینان از ارتباط، مجدد تلاش نمایید.");
        break;
      case -3:
        showError(runnable, "آدرس وارد شده نا معتبر میباشد لطفا با پشتیبانی تماس حاصل نمایید");
        break;
      case 400:
        showError(runnable, "خطای 400 : مشکلی در ارسال داده به وجود آمده است لطفا پس از چند لحظه مجدد تلاش نمایید در صورت عدم برطرف شدن، لطفا با پشتیبانی تماس حاصل نمایید.");
        break;
      case 401:
        showError(runnable, "خطای 401 : عدم دسترسی به شبکه لطفا با پشتیبانی تماس حاصل نمایید.");
        break;
      case 403:
        showError(runnable, "خطای 403 : عدم دسترسی به شبکه لطفا با پشتیبانی تماس حاصل نمایید.");
        break;
      case 404:
        showError(runnable, "خطای 404 : برای چنین درخواستی پاسخی وجود ندارد لطفا با پشتیبانی تماس حاصل نمایید.");
        break;
      case 500:
        showError(runnable, "خطای 500 : مشکلی در پردازش داده به وجود آمده است لطفا پس از چند لحظه مجدد تلاش نمایید در صورت عدم برطرف شدن، لطفا با پشتیبانی تماس حاصل نمایید.");
        break;
      default:
        showError(runnable, "خطای " + code + " : خطایی تعریف نشده در سیستم به وجود آمده لطفا با پشتیبانی تماس حاصل نمایید.");
        break;
    }
  }

  private static ErrorDialog errorDialog;

  private void showError(final Runnable runnable, final String message) {
    try {
      if (new AppStatusHelper().appIsRun(MyApplication.context)) {
        MyApplication.handler.post(() -> {


          if (errorDialog == null) {
            errorDialog = new ErrorDialog();
            errorDialog.titleText("خطایی رخ داده است");
            errorDialog.messageText(message);
            errorDialog.cancelable(false);
            errorDialog.closeBtnRunnable("بستن", () -> errorDialog.dismiss());
            errorDialog.tryAgainBtnRunnable("تلاش مجدد", () -> runnable.run());

          }

          ErrorDialog.dismiss();
          errorDialog.show();


//            AlertDialog.Builder builder;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//              builder = new AlertDialog.Builder(MyApplication.currentActivity);
//            } else {
//              builder = new AlertDialog.Builder(MyApplication.currentActivity);
//            }

//            builder.setTitle("خطایی رخ داده است")
//                    .setCancelable(false)
//                    .setMessage(message)
//                    .setPositiveButton("تلاش مجدد", new DialogInterface.OnClickListener() {
//                      public void onClick(DialogInterface dialog, int which) {
//                        runnable.run();
//                      }
//                    })
//                    .setNegativeButton("انصراف", new DialogInterface.OnClickListener() {
//                      public void onClick(DialogInterface dialog, int which) {
//                        // do nothing
//                      }
//                    })
//                    .show();
        });
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

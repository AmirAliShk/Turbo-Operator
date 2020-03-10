package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;


import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.DateHelper;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.StringHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;

public class ReserveDialog {

  private static final String TAG = ReserveDialog.class.getSimpleName();

  public interface Listener {
    void onClose(boolean b);
  }

  private Date selectedDate = DateHelper.getCurrentGregorianDate();
  static Dialog dialog;
  private static final String DATEPICKER = "DatePickerDialog";
  private static final String TIMEPICKER = "TimePickerDialog";
  private DatePickerDialog datePickerDialog;
  private TimePickerDialog timePickerDialog;

  public void show() {
    dialog = new Dialog(MyApplication.currentActivity);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
    dialog.setContentView(R.layout.dialog_reserve);
    TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
    wlp.gravity = Gravity.CENTER;
    wlp.windowAnimations = R.style.ExpandAnimation;
    dialog.getWindow().setAttributes(wlp);
    dialog.setCancelable(true);

    LinearLayout llDate = dialog.findViewById(R.id.llDate);
    LinearLayout llTime = dialog.findViewById(R.id.llTime);
    ImageView imgClose = dialog.findViewById(R.id.imgClose);
    TextView txtTime = dialog.findViewById(R.id.txtTime);
    TextView txtDate = dialog.findViewById(R.id.txtDate);
    Button btnSubmit = dialog.findViewById(R.id.btnSubmit);

    txtDate.setText(StringHelper.toPersianDigits(DateHelper.strPersianSeven(selectedDate)));
    txtTime.setText(StringHelper.toPersianDigits(DateHelper.strPersianFour1(selectedDate)));

    txtTime.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        PersianCalendar persianCalendar = new PersianCalendar();
        timePickerDialog = TimePickerDialog.newInstance(
                (view, hourOfDay, minute) -> {
                  String m_Time = String.format(new Locale("en_US"), "%02d:%02d", hourOfDay, minute);
                  txtTime.setText(StringHelper.toPersianDigits(m_Time));
                }, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true);
        timePickerDialog.show(MyApplication.currentActivity.getFragmentManager(), TIMEPICKER);

      }
    });

    txtDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        PersianCalendar persianCalendar = new PersianCalendar();
        datePickerDialog = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
                  DateHelper.YearMonthDate jalaliDate = new DateHelper.YearMonthDate(year, monthOfYear + 1, dayOfMonth, 23, 59, 0);
                  selectedDate = DateHelper.jalaliToGregorian(jalaliDate);
                  Date currentDate = DateHelper.getCurrentGregorianDate();
                  if (selectedDate.getTime() <= currentDate.getTime()) {
                    MyApplication.Toast("نباید از تاریخ امروز کمتر انتخاب کنی", Toast.LENGTH_SHORT);
                    txtDate.setText(StringHelper.toPersianDigits(DateHelper.strPersianSeven(currentDate)));
                  } else {
                    txtDate.setText(StringHelper.toPersianDigits(DateHelper.strPersianSeven(selectedDate)));
                  }
                },
                persianCalendar.getPersianYear(),
                persianCalendar.getPersianMonth(),
                persianCalendar.getPersianDay()
        );
        datePickerDialog.show(MyApplication.currentActivity.getFragmentManager(), DATEPICKER);
      }
    });

    btnSubmit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dismiss();
      }
    });

    imgClose.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dismiss();
      }
    });

    dialog.show();
  }

  private static void dismiss() {
    try {
      if (dialog != null) {
        dialog.dismiss();
        KeyBoardHelper.hideKeyboard();
      }
    } catch (Exception e) {
      Log.e("TAG", "dismiss: " + e.getMessage());
    }
    dialog = null;
  }

}

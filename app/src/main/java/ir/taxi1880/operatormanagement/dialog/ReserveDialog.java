package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

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
    View blrView;

    public void show() {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        dialog.setContentView(R.layout.dialog_reserve);
        TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);
        dialog.setCancelable(true);

        LinearLayout llDate = dialog.findViewById(R.id.llDate);
        LinearLayout llTime = dialog.findViewById(R.id.llTime);
        LinearLayout llReserveTrip = dialog.findViewById(R.id.llReserveTrip);
        ImageView imgClose = dialog.findViewById(R.id.imgClose);
        TextView txtTime = dialog.findViewById(R.id.txtTime);
        TextView txtDate = dialog.findViewById(R.id.txtDate);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        blrView = dialog.findViewById(R.id.blrView);

        txtDate.setText(StringHelper.toPersianDigits(DateHelper.strPersianSeven(selectedDate)));
        txtTime.setText(StringHelper.toPersianDigits(DateHelper.strPersianFour1(selectedDate)));

        llReserveTrip.setOnClickListener(view -> {return;});
        blrView.setOnClickListener(view -> dismiss());

        txtTime.setOnClickListener(v -> {
            PersianCalendar persianCalendar = new PersianCalendar();
            timePickerDialog = TimePickerDialog.newInstance(
                    (view, hourOfDay, minute) -> {
                        String m_Time = String.format(new Locale("en_US"), "%02d:%02d", hourOfDay, minute);
                        txtTime.setText(StringHelper.toPersianDigits(m_Time));
                    }, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true);
            timePickerDialog.show(MyApplication.currentActivity.getFragmentManager(), TIMEPICKER);

        });

        txtDate.setOnClickListener(v -> {
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
        });

        btnSubmit.setOnClickListener(view -> dismiss());

        imgClose.setOnClickListener(view -> dismiss());

        dialog.show();
    }

    private static void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
                KeyBoardHelper.hideKeyboard();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, dismiss method");
        }
        dialog = null;
    }
}
package ir.taxi1880.operatormanagement.helper;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import ir.taxi1880.operatormanagement.app.MyApplication;

public class CheckEmptyView {

  private int length = 0;
  private boolean emptyEditText = false;
  private boolean emptyReadioButton = false;
  private String text = "";
  private int radioButtonId = -1;
  private EditText editText;

  public CheckEmptyView setLength(int length, EditText editText) {
    this.length = length;
    this.editText = editText;
    return this;
  }

  public CheckEmptyView checkEmptyEditText(boolean empty) {
    this.emptyEditText = empty;
    return this;
  }

  public CheckEmptyView checkEmptyRadioButton(boolean empty) {
    this.emptyReadioButton = empty;
    return this;
  }

  public CheckEmptyView setText(String text) {
    this.text = text;
    return this;
  }

  public CheckEmptyView setCheck(int id) {
    this.radioButtonId = id;
    return this;
  }

  public boolean checkView(View v) {
    try {

      if (v instanceof ViewGroup) {
        ViewGroup vg = (ViewGroup) v;
        for (int i = 0; i < vg.getChildCount(); i++) {
          View child = vg.getChildAt(i);
          checkView(child);
        }
      }

      if (v instanceof RadioGroup) {
        RadioGroup radioGroup = ((RadioGroup) v);
        if (this.emptyReadioButton) {
          if (radioGroup.getCheckedRadioButtonId() == -1) {
            MyApplication.Toast("لطفا تمام گزینه ها را انتخاب کنید", Toast.LENGTH_SHORT);
            return false;
          } else {
            return true;
          }
        }
        return false;
      }

      if (v instanceof EditText) {
        EditText editText = ((EditText) v);
        if (this.emptyEditText) {
          if (editText.getText().toString().isEmpty()) {
            MyApplication.Toast("لطفا تمام فیلد های ثبت نام را کامل کنید", Toast.LENGTH_SHORT);
            return false;
          } else {
            return true;
          }
        }
        return false;
      }

    } catch (Exception e) {
      e.printStackTrace();
      // ignore
    }

    return true;
  }

  public void setValue(View v) {
    try {

      if (v instanceof ViewGroup) {
        ViewGroup vg = (ViewGroup) v;
        for (int i = 0; i < vg.getChildCount(); i++) {
          View child = vg.getChildAt(i);
          setValue(child);
        }
      }

      if (v instanceof RadioGroup) {
        RadioGroup radioGroup = ((RadioGroup) v);
        if (this.radioButtonId != -1) {
         radioGroup.check(radioButtonId);
        }
      }

      if (v instanceof EditText) {
        EditText editText = ((EditText) v);
        if (!this.text.equals("")) {
          editText.setText(text);
        }
      }

      if (v instanceof TextView) {
        TextView textView = ((TextView) v);
        if (!this.text.equals("")) {
          textView.setText(text);
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      // ignore
    }
  }

}
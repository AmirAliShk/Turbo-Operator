package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.AddressAdapter;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.AddressModel;

import static ir.taxi1880.operatormanagement.fragment.TripRegisterFragment.hideKeyboard;
import static ir.taxi1880.operatormanagement.fragment.TripRegisterFragment.openKeyBoaredAuto;

public class SearchLocationDialog {

  private static final String TAG = SearchLocationDialog.class.getSimpleName();

  public interface Listener {
    void description(String address);

//    void selectedAddress(boolean b);
  }

  private ArrayList<AddressModel> addressModels;
  private AddressAdapter addressAdapter;
  private ListView listPlace;

  private Listener listener;
  private static Dialog dialog;

  public void show(Listener listener, String title) {
    dialog = new Dialog(MyApplication.currentActivity);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
    dialog.setContentView(R.layout.dialog_search_location);
    TypefaceUtil.overrideFonts(dialog.getWindow().getDecorView());
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
    wlp.gravity = Gravity.CENTER;
    wlp.windowAnimations = R.style.ExpandAnimation;
    dialog.getWindow().setAttributes(wlp);
    dialog.setCancelable(true);
    this.listener = listener;

    listPlace = dialog.findViewById(R.id.listPlace);

    addressModels = address();

    openKeyBoaredAuto();

    EditText edtSearchPlace = dialog.findViewById(R.id.edtSearchPlace);
    TextView txtTitle = dialog.findViewById(R.id.txtTitle);
    ListView listPlace = dialog.findViewById(R.id.listPlace);

    edtSearchPlace.requestFocus();

    edtSearchPlace.setHint(title);
    txtTitle.setText(title);

    listPlace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listener.description(addressModels.get(position).getAddress());
//        listener.selectedAddress(true);
        dismiss();
      }
    });

    dialog.show();

  }

  private static void dismiss() {
    try {
      if (dialog != null) {
        dialog.dismiss();
        hideKeyboard(MyApplication.currentActivity);
      }
    } catch (Exception e) {
      Log.e("TAG", "dismiss: " + e.getMessage());
    }
    dialog = null;
  }

  private String city = "[{\"name\":\"مشهد\"},{\"name\":\"نیشابور\"},{\"name\":\"تربت حیدریه\"},{\"name\":\"تربت جام\"},{\"name\":\"گناباد\"}," +
          "{\"name\":\"کاشمر\"},{\"name\":\"تایباد\"}]";

  private ArrayList<AddressModel> address() {
    addressModels = new ArrayList<>();
    try {
      JSONArray arr = new JSONArray(city);
      for (int i = 0; i < arr.length(); i++) {
        JSONObject object = arr.getJSONObject(i);
        AddressModel addressModel = new AddressModel();
        addressModel.setAddress(object.getString("name"));
        addressModels.add(addressModel);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    addressAdapter = new AddressAdapter(addressModels, MyApplication.context);
    listPlace.setAdapter(addressAdapter);
    return addressModels;
  }


}

package ir.taxi1880.operatormanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.OkHttp.RequestHelper;
import ir.taxi1880.operatormanagement.R;
import ir.taxi1880.operatormanagement.adapter.StationAdapter;
import ir.taxi1880.operatormanagement.app.EndPoints;
import ir.taxi1880.operatormanagement.app.MyApplication;
import ir.taxi1880.operatormanagement.helper.KeyBoardHelper;
import ir.taxi1880.operatormanagement.helper.TypefaceUtil;
import ir.taxi1880.operatormanagement.model.StationModel;

public class SearchLocationDialog {

  private static final String TAG = SearchLocationDialog.class.getSimpleName();

  public interface Listener {
    void description(String address);

//    void selectedAddress(boolean b);
  }

  private ArrayList<StationModel> stationModels;
  private StationAdapter addressAdapter;
  private ListView listPlace;
  private ViewFlipper vfLocation;

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
    vfLocation = dialog.findViewById(R.id.vfLocation);


    EditText edtSearch = dialog.findViewById(R.id.edtSearch);
    TextView txtTitle = dialog.findViewById(R.id.txtTitle);
    ImageView imgSearch = dialog.findViewById(R.id.imgSearch);

    edtSearch.requestFocus();

    edtSearch.setHint(title);
    txtTitle.setText(title);

    listPlace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listener.description(stationModels.get(position).getCode());
//        listener.selectedAddress(true);
        dismiss();
      }
    });

    imgSearch.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (edtSearch.getText().toString().isEmpty()) {
          MyApplication.Toast("لطفا نام منطقه را وارد نمایید", Toast.LENGTH_SHORT);
          return;
        }
        findWay("mashhad", edtSearch.getText().toString());
      }
    });

    edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_NEXT) {
          findWay("mashhad", edtSearch.getText().toString());
          Toast.makeText(MyApplication.context, "جست و جوی ایستگاه", Toast.LENGTH_LONG).show();
          return true;
        } else if (i == EditorInfo.IME_ACTION_DONE) {
          findWay("mashhad", edtSearch.getText().toString());
          Toast.makeText(MyApplication.context, "جست و جوی ایستگاه", Toast.LENGTH_LONG).show();
          return true;
        }
        return false;
      }
    });
    MyApplication.handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        KeyBoardHelper.showKeyboard(MyApplication.context);

      }
    }, 200);

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

  private void findWay(String cityLName, String address) {
    vfLocation.setDisplayedChild(1);
    JSONObject params = new JSONObject();
//    try {
//      params.put("citylatinname", );
//      params.put("address", );
//
//      Log.i(TAG, "findWay: "+params);

    RequestHelper.builder(EndPoints.FIND_WAY + "/" + cityLName + "/" + address)
            .params(params)
            .method(RequestHelper.GET)
            .listener(onFindWay)
            .request();
//
//    } catch (JSONException e) {
//      e.printStackTrace();
//    }

  }

  RequestHelper.Callback onFindWay = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            JSONArray arr = new JSONArray(args[0].toString());
            for (int i = 0; i < arr.length(); i++) {
              JSONObject object = arr.getJSONObject(i);
              for (int j = 0; j < object.length(); j++) {
                JSONObject objWay = object.getJSONObject("way");
                JSONObject obgStation = object.getJSONObject("station");
                StationModel stationModel = new StationModel();
                stationModel.setAddress(objWay.getString("name"));
                stationModel.setName(obgStation.getString("stationName"));
                stationModel.setCode(obgStation.getString("stationCode"));
                stationModel.setStatus(obgStation.getInt("countryside"));
                stationModels.add(stationModel);
              }
            }

            vfLocation.setDisplayedChild(2);
            addressAdapter = new StationAdapter(stationModels, MyApplication.context);
            listPlace.setAdapter(addressAdapter);

            if (stationModels.size() == 0) {
              vfLocation.setDisplayedChild(0);
            }

          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      });
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {

    }
  };

//  private ArrayList<StationModel> stations() {
//    stationModels = new ArrayList<>();
//    try {
//      JSONArray arr = new JSONArray(city);
//      for (int i = 0; i < arr.length(); i++) {
//        JSONObject object = arr.getJSONObject(i);
//        StationModel stationModel = new StationModel();
//        stationModel.setAddress(object.getString("address"));
//        stationModel.setCode(object.getString("stationCode"));
//        stationModel.setName(object.getString("stationName"));
//        stationModel.setStatus(object.getInt("status"));
//        stationModels.add(stationModel);
//      }
//      addressAdapter = new StationAdapter(stationModels, MyApplication.context);
//      listPlace.setAdapter(addressAdapter);
//
//      if (stationModels.size() == 0) {
//        vfLocation.setDisplayedChild(0);
//      }
//
//    } catch (JSONException e) {
//      e.printStackTrace();
//    }
//
//    return stationModels;
//  }


}

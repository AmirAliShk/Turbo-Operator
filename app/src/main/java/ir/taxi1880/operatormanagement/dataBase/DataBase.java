package ir.taxi1880.operatormanagement.dataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.model.CityModel;

public class DataBase extends SQLiteOpenHelper {
  private static int VERSION = 1;
  //TODO Do not change names any way
  private static String DB_NAME = "operators";
  private static String TRIP_TABLE = "Trip";
  private static String CITY_TABLE = "City";

  //**************************** Trip Column ****************************
  private static String COLUMN_TRIP_ID = "tripId";
  private static String COLUMN_ORIGIN_TEXT = "originText";
  private static String COLUMN_ORIGIN_STATION = "originStation";
  private static String COLUMN_CITY = "city";
  private static String COLUMN_SAVE_DATE = "saveDate";
  private static String COLUMN_SEND_DATE = "sendDate";

  //***************************** City Column ***************************
  private static String COLUMN_CITY_ID = "cityId";
  private static String COLUMN_CITY_NAME = "cityName";
  private static String COLUMN_CITY_L_NAME = "cityLName";
  //**********************************************************************

  public DataBase(Context context) {
    super(context, DB_NAME, null, VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    createTripTable(sqLiteDatabase);
    createCityTable(sqLiteDatabase);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TRIP_TABLE);
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CITY_TABLE);
    onCreate(sqLiteDatabase);
  }

  public void createTripTable(SQLiteDatabase database) {
    database.execSQL("CREATE TABLE " + TRIP_TABLE +
            "(" + COLUMN_TRIP_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_ORIGIN_TEXT + " TEXT," +
            COLUMN_ORIGIN_STATION + " INTEGER," +
            COLUMN_SAVE_DATE + " TEXT," +
            COLUMN_SEND_DATE + " TEXT," +
            COLUMN_CITY + " TEXT)");
  }

  public void insertTripRow(TripModel tripModel) {
    try {
      SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
      contentValues.put(COLUMN_TRIP_ID, tripModel.getId());
      contentValues.put(COLUMN_ORIGIN_TEXT, tripModel.getOriginText());
      contentValues.put(COLUMN_ORIGIN_STATION, tripModel.getOriginStation());
      contentValues.put(COLUMN_SAVE_DATE, tripModel.getSaveDate());
      contentValues.put(COLUMN_SEND_DATE, tripModel.getSendDate());
      contentValues.put(COLUMN_CITY, tripModel.getCity());
      //TODO insert with conflict or without
      // this will insert, if record is new, replace otherwise
      sqLiteDatabase.insertWithOnConflict(TRIP_TABLE, COLUMN_TRIP_ID, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public ArrayList<TripModel> getTripRow() {
    ArrayList<TripModel> tripModels = new ArrayList<>();
    SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
    try {
      @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TRIP_TABLE + " ORDER BY " + COLUMN_TRIP_ID + " ASC; ", null);
      cursor.moveToFirst();

      while (!cursor.isAfterLast()) {
        TripModel tripModel = new TripModel();
        tripModel.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_TRIP_ID)));
        tripModel.setOriginText(cursor.getString(cursor.getColumnIndex(COLUMN_ORIGIN_TEXT)));
        tripModel.setOriginStation(cursor.getInt(cursor.getColumnIndex(COLUMN_ORIGIN_STATION)));
        tripModel.setSaveDate(cursor.getString(cursor.getColumnIndex(COLUMN_SAVE_DATE)));
        tripModel.setSendDate(cursor.getString(cursor.getColumnIndex(COLUMN_SEND_DATE)));
        tripModel.setCity(cursor.getInt(cursor.getColumnIndex(COLUMN_CITY)));
        tripModels.add(tripModel);
        cursor.moveToNext();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return tripModels;
  }

  int current = 0;

  public TripModel getTopAddress() {
    TripModel tripModel = new TripModel();
    SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
    try {
      @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TRIP_TABLE + " ORDER BY " + COLUMN_TRIP_ID + " ASC LIMIT 1 ; ", null);
      if (cursor != null) {
        cursor.moveToFirst();
        if (cursor.isFirst()) {
          tripModel.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_TRIP_ID)));
          tripModel.setOriginText(cursor.getString(cursor.getColumnIndex(COLUMN_ORIGIN_TEXT)));
          tripModel.setOriginStation(cursor.getInt(cursor.getColumnIndex(COLUMN_ORIGIN_STATION)));
          tripModel.setSaveDate(cursor.getString(cursor.getColumnIndex(COLUMN_SAVE_DATE)));
          tripModel.setSendDate(cursor.getString(cursor.getColumnIndex(COLUMN_SEND_DATE)));
          tripModel.setCity(cursor.getInt(cursor.getColumnIndex(COLUMN_CITY)));
          current = cursor.getPosition();
          return tripModel;
        } else {
          current = 0;
          return null;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public boolean goToNextRecord() {
    SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
    Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TRIP_TABLE, null);
    if (cursor != null && current != 0) {
      cursor.moveToPosition(current + 1);
      return true;
    } else {
      //TODO do sth
      if (cursor != null) {
        cursor.close();
      }
      return false;
    }
  }

  public int getRemainingAddress() {
    String countQuery = "SELECT  * FROM " + TRIP_TABLE;
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery(countQuery, null);
    int count = cursor.getCount();
    cursor.close();
    return count;
  }

  public int insertSendDate(int tripId, String date) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues cv = new ContentValues();
    cv.put(COLUMN_SEND_DATE, date);
    int i = sqLiteDatabase.update(TRIP_TABLE, cv, COLUMN_TRIP_ID + " = " + tripId, null);
    Log.i("TAG", "insertSendDate:update=== " + i);
    return i;
  }

  public void update(int tripId, String date) {
    SQLiteDatabase db = this.getReadableDatabase();
    db.execSQL("UPDATE Trip SET sendDate = " + "'" + date + "'" + " WHERE tripId = " + "'" + tripId + "'");
    //    update Trip set sendDate='12345' where tripId='39403'
  }

  public void deleteRow(int tripId) {

    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    boolean b = sqLiteDatabase.delete(TRIP_TABLE, COLUMN_TRIP_ID + "=" + tripId, null) > 0;

    if (b) {
      Log.i("TripDataBase", "deleteRow: = true  " + tripId);
    } else {
      Log.i("TripDataBase", "deleteRow: = false  " + tripId);
    }
  }

  public void deleteAllData() {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    sqLiteDatabase.delete(TRIP_TABLE, null, null);
  }

  public void deleteRemainingRecord(int tripId) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    sqLiteDatabase.delete(TRIP_TABLE, COLUMN_TRIP_ID + "<>" + tripId, null);
    Log.e("TAG", "deleteRemainingRecord: "+tripId);
  }

// ****************************************************** City Table ******************************************************

  public void createCityTable(SQLiteDatabase database) {
    database.execSQL("CREATE TABLE " + CITY_TABLE +
            "(" + COLUMN_CITY_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_CITY_NAME + " TEXT," +
            COLUMN_CITY_L_NAME + " TEXT)");
  }

  public void insertCity(CityModel cityModel) {
    try {
      SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
      contentValues.put(COLUMN_CITY_ID, cityModel.getId());
      contentValues.put(COLUMN_CITY_NAME, cityModel.getCity());
      contentValues.put(COLUMN_CITY_L_NAME, cityModel.getCityLatin());
      sqLiteDatabase.insertWithOnConflict(CITY_TABLE, COLUMN_CITY_ID, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public CityModel getCityName(int id) {
    SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
    @SuppressLint("Recycle") Cursor res = sqLiteDatabase.rawQuery("select * from " + CITY_TABLE + " where " + id + " = " + COLUMN_CITY_ID, null);
    if (!res.moveToFirst()) {
      return null;
    }
    res.moveToFirst();
    CityModel model = new CityModel();
    model.setId(res.getInt(res.getColumnIndex(COLUMN_CITY_ID)));
    model.setCity(res.getString(res.getColumnIndex(COLUMN_CITY_NAME)));
    model.setCityLatin(res.getString(res.getColumnIndex(COLUMN_CITY_L_NAME)));

    return model;
  }

}

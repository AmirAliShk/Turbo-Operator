package ir.taxi1880.operatormanagement.dataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class TripDataBase extends SQLiteOpenHelper {
  private static int VERSION = 1;
  private static String DB_NAME = "operators";
  private static String TRIP_TABLE = "Trip";

  //**************************** Trip Column ****************************
  private static String COLUMN_TRIP_ID = "tripId";
  private static String COLUMN_ORIGIN_TEXT = "originText";
  private static String COLUMN_ORIGIN_STATION = "originStation";
  private static String COLUMN_CITY = "city";
  private static String COLUMN_SAVE_DATE = "saveDate";
  private static String COLUMN_SEND_DATE = "sendDate";
  //*********************************************************************

  public TripDataBase(Context context) {
    super(context, DB_NAME, null, VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    createTripTable(sqLiteDatabase);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TRIP_TABLE);
    onCreate(sqLiteDatabase);
  }

  public static void createTripTable(SQLiteDatabase database) {
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
      // this will insert if record is new, update otherwise
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
        tripModel.setCity(cursor.getString(cursor.getColumnIndex(COLUMN_CITY)));
        tripModels.add(tripModel);
        cursor.moveToNext();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return tripModels;
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

}

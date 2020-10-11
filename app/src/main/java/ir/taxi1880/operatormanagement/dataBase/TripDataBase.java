package ir.taxi1880.operatormanagement.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class TripDataBase extends SQLiteOpenHelper {
  private static int VERSION = 1;
  private static String DB_NAME = "operators";
  private static String TRIP_TABLE = "Trip";

  //**************************** Trip Column ****************************
  private static String COLUMN_TRIP_ID = "tripId";
  private static String COLUMN_ORIGIN_TEXT = "originText";
  private static String COLUMN_DESTINATION_TEXT = "destinationText";
  private static String COLUMN_ORIGIN_STATION = "originStation";
  private static String COLUMN_DESTINATION_STATION = "destinationStation";
  private static String COLUMN_CITY = "city";
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
    database.execSQL("create table " + TRIP_TABLE + "(" + COLUMN_TRIP_ID + " integer primary key AUTOINCREMENT, " +
            COLUMN_ORIGIN_TEXT + "text, " +
            COLUMN_DESTINATION_TEXT + "text, " +
            COLUMN_ORIGIN_STATION + "int, " +
            COLUMN_DESTINATION_STATION + "int, " +
            COLUMN_CITY + "text)");
  }

  public void insertTripRow(TripModel tripModel) {
    try {
      SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
      contentValues.put(COLUMN_TRIP_ID, tripModel.getId());
      contentValues.put(COLUMN_ORIGIN_TEXT, tripModel.getOriginText());
      contentValues.put(COLUMN_DESTINATION_TEXT, tripModel.getDestinationText());
      contentValues.put(COLUMN_ORIGIN_STATION, tripModel.getOriginStation());
      contentValues.put(COLUMN_DESTINATION_STATION, tripModel.getDestinationStation());
      contentValues.put(COLUMN_CITY, tripModel.getCity());
      //TODO insert with conflict or without
      sqLiteDatabase.insert(TRIP_TABLE, null, contentValues);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public ArrayList<TripModel> getTripRow() {
    ArrayList<TripModel> tripModels = new ArrayList<>();
    SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
    try {
      Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TRIP_TABLE, null);
      cursor.moveToFirst();

      while (!cursor.isAfterLast()) {
        TripModel tripModel = new TripModel();
        tripModel.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_TRIP_ID)));
        tripModel.setOriginText(cursor.getString(cursor.getColumnIndex(COLUMN_ORIGIN_TEXT)));
        tripModel.setDestinationText(cursor.getString(cursor.getColumnIndex(COLUMN_DESTINATION_TEXT)));
        tripModel.setOriginStation(cursor.getInt(cursor.getColumnIndex(COLUMN_ORIGIN_STATION)));
        tripModel.setDestinationStation(cursor.getInt(cursor.getColumnIndex(COLUMN_DESTINATION_STATION)));
        tripModel.setCity(cursor.getString(cursor.getColumnIndex(COLUMN_CITY)));
        tripModels.add(tripModel);
        cursor.moveToNext();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return tripModels;
  }

}

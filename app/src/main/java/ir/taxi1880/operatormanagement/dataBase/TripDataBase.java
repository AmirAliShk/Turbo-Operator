package ir.taxi1880.operatormanagement.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


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

  private static void createTripTable(SQLiteDatabase database) {
    database.execSQL("create table " + TRIP_TABLE + "(" + COLUMN_TRIP_ID + " integer primary key AUTOINCREMENT, " +
            COLUMN_ORIGIN_TEXT + "text, " +
            COLUMN_DESTINATION_TEXT + "text, " +
            COLUMN_ORIGIN_STATION + "int, " +
            COLUMN_DESTINATION_STATION + "int, " +
            COLUMN_CITY + "text)");
  }

}

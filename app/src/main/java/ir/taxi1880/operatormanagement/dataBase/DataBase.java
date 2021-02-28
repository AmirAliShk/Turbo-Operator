package ir.taxi1880.operatormanagement.dataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.model.AllComplaintModel;
import ir.taxi1880.operatormanagement.model.CityModel;

public class DataBase extends SQLiteOpenHelper {
    // TODO when you change the entitys structure, please increase the version of dataBase.
    private static int VERSION = 3;
    //TODO Do not change names any way
    private static String DB_NAME = "operators";
    private static String TRIP_TABLE = "Trip";
    private static String CITY_TABLE = "City";
    private static String COMPLAINT_TABLE = "Complaint";

    //**************************** Trip Column ****************************
    private static String COLUMN_TRIP_ID = "tripId";
    private static String COLUMN_OPERATOR_ID = "operatorId";
    private static String COLUMN_ORIGIN_TEXT = "originText";
    private static String COLUMN_ORIGIN_STATION = "originStation";
    private static String COLUMN_CITY = "city";
    private static String COLUMN_SAVE_DATE = "saveDate";
    private static String COLUMN_SEND_DATE = "sendDate";
    private static String COLUMN_TELL = "tell";
    private static String COLUMN_CUSTOMER_NAME = "customerName";
    private static String COLUMN_VOIP_ID = "voipId";
    private static String COLUMN_NEXT_RECORD = "nextRecord";

    //***************************** City Column ***************************
    private static String COLUMN_CITY_ID = "cityId";
    private static String COLUMN_CITY_NAME = "cityName";
    private static String COLUMN_CITY_L_NAME = "cityLName";

    //************************** Complaint Column *********************************

    private static String COLUMN_COMPLAINT_ID = "complaintId";
    private static String COLUMN_COMPLAINT_DATE = "complaintDate";
    private static String COLUMN_COMPLAINT_TIME = "complaintTime";
    private static String COLUMN_COMPLAINT_DESCRIPTION = "complaintDescription";
    //    private static String COLUMN_COMPLAINT_CITY = "complaintCity";
    private static String COLUMN_COMPLAINT_ADDRESS = "complaintAddress";
    //    private static String COLUMN_COMPLAINT_STATION_CODE = "complaintStationCode";
    private static String COLUMN_COMPLAINT_PASSENGER_VOICE = "complaintPassengerVoice";
    private static String COLUMN_COMPLAINT_SERVICE_CODE = "complaintServiceCode";
    private static String COLUMN_COMPLAINT_USER_CODE = "complaintUserCode";
    private static String COLUMN_COMPLAINT_IS_CHECK = "complaintIsCheck";
    private static String COLUMN_COMPLAINT_TELL = "complaintTell";
    private static String COLUMN_COMPLAINT_RESULT = "complaintResult";
    private static String COLUMN_COMPLAINT_USER_CODE_CONTACT = "complaintUserCodeContact";
    private static String COLUMN_COMPLAINT_TYPE_RESULT = "complaintTypeResult";
    private static String COLUMN_COMPLAINT_CUSTOMER_NAME = "complaintCustomerName";
    private static String COLUMN_COMPLAINT_CON_DATE = "complaintConDate";
    private static String COLUMN_COMPLAINT_CON_TIME = "complaintConTime";
    private static String COLUMN_COMPLAINT_SEND_TIME = "complaintSendTime";
    private static String COLUMN_COMPLAINT_INSPECTOR_USER = "complaintInspectorUser";

    //******************************************************************************************

    public DataBase(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTripTable(sqLiteDatabase);
        createCityTable(sqLiteDatabase);
        createComplaintTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TRIP_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CITY_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + COMPLAINT_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void createTripTable(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + TRIP_TABLE +
                "(" + COLUMN_TRIP_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_OPERATOR_ID + " INTEGER," +
                COLUMN_ORIGIN_TEXT + " TEXT," +
                COLUMN_ORIGIN_STATION + " INTEGER," +
                COLUMN_SAVE_DATE + " TEXT," +
                COLUMN_SEND_DATE + " TEXT," +
                COLUMN_CITY + " TEXT," +
                COLUMN_TELL + " TEXT," +
                COLUMN_CUSTOMER_NAME + " TEXT," +
                COLUMN_NEXT_RECORD + " INTEGER DEFAULT 0," +
                COLUMN_VOIP_ID + " TEXT)");
    }

    public void insertTripRow(DBTripModel DBTripModel) {
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_TRIP_ID, DBTripModel.getId());
            contentValues.put(COLUMN_OPERATOR_ID, DBTripModel.getOperatorId());
            contentValues.put(COLUMN_ORIGIN_TEXT, DBTripModel.getOriginText());
            contentValues.put(COLUMN_ORIGIN_STATION, DBTripModel.getOriginStation());
            contentValues.put(COLUMN_SAVE_DATE, DBTripModel.getSaveDate());
            contentValues.put(COLUMN_SEND_DATE, DBTripModel.getSendDate());
            contentValues.put(COLUMN_CITY, DBTripModel.getCity());
            contentValues.put(COLUMN_TELL, DBTripModel.getTell());
            contentValues.put(COLUMN_CUSTOMER_NAME, DBTripModel.getCustomerName());
            contentValues.put(COLUMN_VOIP_ID, DBTripModel.getVoipId());
            sqLiteDatabase.insertWithOnConflict(TRIP_TABLE, COLUMN_TRIP_ID, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<DBTripModel> getTripRow() {
        ArrayList<DBTripModel> DBTripModels = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        try {
            @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TRIP_TABLE + " ORDER BY " + COLUMN_TRIP_ID + " ASC; ", null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                DBTripModel DBTripModel = new DBTripModel();
                DBTripModel.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_TRIP_ID)));
                DBTripModel.setOperatorId(cursor.getInt(cursor.getColumnIndex(COLUMN_OPERATOR_ID)));
                DBTripModel.setOriginText(cursor.getString(cursor.getColumnIndex(COLUMN_ORIGIN_TEXT)));
                DBTripModel.setOriginStation(cursor.getInt(cursor.getColumnIndex(COLUMN_ORIGIN_STATION)));
                DBTripModel.setSaveDate(cursor.getString(cursor.getColumnIndex(COLUMN_SAVE_DATE)));
                DBTripModel.setSendDate(cursor.getString(cursor.getColumnIndex(COLUMN_SEND_DATE)));
                DBTripModel.setCity(cursor.getInt(cursor.getColumnIndex(COLUMN_CITY)));
                DBTripModels.add(DBTripModel);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DBTripModels;
    }

    @SuppressLint("Recycle")
    public DBTripModel getTopAddress() {
        DBTripModel DBTripModel = new DBTripModel();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        try {
            Cursor cursor;
//      if (goNext) {
//        cursor = sqLiteDatabase.rawQuery("select * from " + TRIP_TABLE + " ORDER BY " + COLUMN_NEXT_RECORD + " ASC LIMIT 1; ", null);
//      } else {
            cursor = sqLiteDatabase.rawQuery("select * from " + TRIP_TABLE + " ORDER BY " + COLUMN_TRIP_ID + " ASC LIMIT 1 ; ", null);
//      }
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.isFirst()) {
                    DBTripModel.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_TRIP_ID)));
                    DBTripModel.setOperatorId(cursor.getInt(cursor.getColumnIndex(COLUMN_OPERATOR_ID)));
                    DBTripModel.setOriginText(cursor.getString(cursor.getColumnIndex(COLUMN_ORIGIN_TEXT)));
                    DBTripModel.setOriginStation(cursor.getInt(cursor.getColumnIndex(COLUMN_ORIGIN_STATION)));
                    DBTripModel.setSaveDate(cursor.getString(cursor.getColumnIndex(COLUMN_SAVE_DATE)));
                    DBTripModel.setSendDate(cursor.getString(cursor.getColumnIndex(COLUMN_SEND_DATE)));
                    DBTripModel.setCity(cursor.getInt(cursor.getColumnIndex(COLUMN_CITY)));
                    DBTripModel.setTell(cursor.getString(cursor.getColumnIndex(COLUMN_TELL)));
                    DBTripModel.setCustomerName(cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOMER_NAME)));
                    DBTripModel.setVoipId(cursor.getString(cursor.getColumnIndex(COLUMN_VOIP_ID)));
                    return DBTripModel;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateNextRecord(int tripId) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NEXT_RECORD, 1);
        sqLiteDatabase.update(TRIP_TABLE, cv, COLUMN_TRIP_ID + " = " + tripId, null);
    }

    public void goToNextRecord() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TRIP_TABLE + " ORDER BY " + COLUMN_TRIP_ID + " ASC, " + COLUMN_NEXT_RECORD + " ASC", null);
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
        Log.e("TAG", "deleteRemainingRecord: " + tripId);
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

    public int getCityCode(String name) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_CITY + " FROM " + CITY_TABLE + " WHERE " + COLUMN_CITY_NAME + "=" + name;
        //select cityCode from city where cityName=name
        @SuppressLint("Recycle") Cursor res = sqLiteDatabase.rawQuery(query, null);

        if (res.getCount() == 0) {
            return 0;
        }

        res.moveToFirst();
        return res.getInt(res.getColumnIndex(COLUMN_CITY));
    }

    public String getCityName2(int id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "select " + COLUMN_CITY_NAME +
                " from " + CITY_TABLE +
                " left join " + TRIP_TABLE +
                " on " + CITY_TABLE + "." + COLUMN_CITY_ID + "=" + TRIP_TABLE + "." + COLUMN_CITY +
                " where " + TRIP_TABLE + "." + COLUMN_CITY + "=" + id;
        @SuppressLint("Recycle") Cursor res = sqLiteDatabase.rawQuery(query, null);

//    select City.cityName
//    from City
//    left join Trip
//    on City.cityId = Trip.cityId
//    where Trip.CityId = id

        if (res.getCount() == 0)
            return "";

        res.moveToFirst();
        return res.getString(res.getColumnIndex(COLUMN_CITY_NAME));
    }

    //******************************************************** Complaint Table *******************************************

    public void createComplaintTable(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + COMPLAINT_TABLE +
                        "(" + COLUMN_COMPLAINT_ID + " INTEGER PRIMARY KEY," +
                        COLUMN_COMPLAINT_DATE + " TEXT," +
                        COLUMN_COMPLAINT_TIME + "TEXT" +
                        COLUMN_COMPLAINT_DESCRIPTION + " TEXT," +
//                COLUMN_COMPLAINT_CITY + " TEXT," +
                        COLUMN_COMPLAINT_ADDRESS + " TEXT," +
                        COLUMN_COMPLAINT_SERVICE_CODE + " INTEGER," +
                        COLUMN_COMPLAINT_USER_CODE + " INTEGER," +
                        COLUMN_COMPLAINT_IS_CHECK + " BOOLEAN," + //TODO VARIABLE
                        COLUMN_COMPLAINT_TELL + " TEXT," +
                        COLUMN_COMPLAINT_RESULT + " TEXT," +
                        COLUMN_COMPLAINT_USER_CODE_CONTACT + " TEXT," +
                        COLUMN_COMPLAINT_TYPE_RESULT + " TEXT," +
                        COLUMN_COMPLAINT_CUSTOMER_NAME + " TEXT," +
                        COLUMN_COMPLAINT_CON_DATE + " TEXT," +
                        COLUMN_COMPLAINT_CON_TIME + " TEXT," +
                        COLUMN_COMPLAINT_SEND_TIME + " TEXT," +
                        COLUMN_COMPLAINT_INSPECTOR_USER + " TEXT," +
//                COLUMN_COMPLAINT_STATION_CODE + " INTEGER," +
                        COLUMN_COMPLAINT_PASSENGER_VOICE + " TEXT)"
        );
    }

    public void insertComplaint(AllComplaintModel complaintModel) {
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_COMPLAINT_ID, complaintModel.getId());
            contentValues.put(COLUMN_COMPLAINT_DATE, complaintModel.getDate());
            contentValues.put(COLUMN_COMPLAINT_TIME, complaintModel.getTime());
            contentValues.put(COLUMN_COMPLAINT_DESCRIPTION, complaintModel.getDescription());
//            contentValues.put(COLUMN_COMPLAINT_CITY, complaintModel.getCity());
            contentValues.put(COLUMN_COMPLAINT_ADDRESS, complaintModel.getAddress());
//            contentValues.put(COLUMN_COMPLAINT_STATION_CODE, complaintModel.getStationCode());
            contentValues.put(COLUMN_COMPLAINT_SERVICE_CODE, complaintModel.getVoipId());
            contentValues.put(COLUMN_COMPLAINT_USER_CODE, complaintModel.getUserCode());
            contentValues.put(COLUMN_COMPLAINT_IS_CHECK, complaintModel.isIscheck());
            contentValues.put(COLUMN_COMPLAINT_TELL, complaintModel.getTell());
            contentValues.put(COLUMN_COMPLAINT_RESULT, complaintModel.getResult());
            contentValues.put(COLUMN_COMPLAINT_USER_CODE_CONTACT, complaintModel.getUserCodeContact());
            contentValues.put(COLUMN_COMPLAINT_TYPE_RESULT, complaintModel.getTypeResult());
            contentValues.put(COLUMN_COMPLAINT_CUSTOMER_NAME, complaintModel.getCustomerName());
            contentValues.put(COLUMN_COMPLAINT_CON_DATE, complaintModel.getConDate());
            contentValues.put(COLUMN_COMPLAINT_CON_TIME, complaintModel.getConTime());
            contentValues.put(COLUMN_COMPLAINT_SEND_TIME, complaintModel.getSendTime());
            contentValues.put(COLUMN_COMPLAINT_INSPECTOR_USER, complaintModel.getInspectorUser());
            contentValues.put(COLUMN_COMPLAINT_PASSENGER_VOICE, complaintModel.getVoipId());
            sqLiteDatabase.insertWithOnConflict(COMPLAINT_TABLE, COLUMN_COMPLAINT_ID, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AllComplaintModel getComplaintRow(int id) {
        AllComplaintModel pendingComplaintModel = new AllComplaintModel();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor res = sqLiteDatabase.rawQuery("select * from " + COMPLAINT_TABLE + " where " + COLUMN_COMPLAINT_ID + " = " + id, null);
        if (res.getCount() == 0) return null;

        res.moveToFirst();

        pendingComplaintModel.setId(res.getInt(res.getColumnIndex(COLUMN_COMPLAINT_ID)));
        pendingComplaintModel.setDate(res.getString(res.getColumnIndex(COLUMN_COMPLAINT_DATE)));
        pendingComplaintModel.setTime(res.getString(res.getColumnIndex(COLUMN_COMPLAINT_TIME)));
        pendingComplaintModel.setDescription(res.getString(res.getColumnIndex(COLUMN_COMPLAINT_DESCRIPTION)));
//        pendingComplaintModel.setCity(res.getString(res.getColumnIndex(COLUMN_COMPLAINT_CITY)));
        pendingComplaintModel.setAddress(res.getString(res.getColumnIndex(COLUMN_COMPLAINT_ADDRESS)));
        pendingComplaintModel.setServiceCode(res.getInt(res.getColumnIndex(COLUMN_COMPLAINT_SERVICE_CODE)));
        pendingComplaintModel.setUserCode(res.getInt(res.getColumnIndex(COLUMN_COMPLAINT_USER_CODE)));
        pendingComplaintModel.setIscheck(res.isNull(res.getColumnIndex(COLUMN_COMPLAINT_IS_CHECK)));
        pendingComplaintModel.setTell(res.getString(res.getColumnIndex(COLUMN_COMPLAINT_TELL)));
        pendingComplaintModel.setResult(res.getString(res.getColumnIndex(COLUMN_COMPLAINT_RESULT)));
        pendingComplaintModel.setUserCodeContact(res.getInt(res.getColumnIndex(COLUMN_COMPLAINT_USER_CODE_CONTACT)));
        pendingComplaintModel.setTypeResult(res.getInt(res.getColumnIndex(COLUMN_COMPLAINT_TYPE_RESULT)));
        pendingComplaintModel.setCustomerName(res.getString(res.getColumnIndex(COLUMN_COMPLAINT_CUSTOMER_NAME)));
        pendingComplaintModel.setConDate(res.getString(res.getColumnIndex(COLUMN_COMPLAINT_CON_DATE)));
        pendingComplaintModel.setConTime(res.getString(res.getColumnIndex(COLUMN_COMPLAINT_CON_TIME)));
        pendingComplaintModel.setSendTime(res.getString(res.getColumnIndex(COLUMN_COMPLAINT_SEND_TIME)));
        pendingComplaintModel.setInspectorUser(res.getInt(res.getColumnIndex(COLUMN_COMPLAINT_INSPECTOR_USER)));
//        pendingComplaintModel.setStationCode(res.getInt(res.getColumnIndex(COLUMN_COMPLAINT_STATION_CODE)));
        pendingComplaintModel.setVoipId(res.getString(res.getColumnIndex(COLUMN_COMPLAINT_PASSENGER_VOICE)));

        return pendingComplaintModel;
    }

}

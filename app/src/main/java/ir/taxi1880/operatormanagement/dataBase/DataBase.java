package ir.taxi1880.operatormanagement.dataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import ir.taxi1880.operatormanagement.model.AllMistakesModel;
import ir.taxi1880.operatormanagement.model.CityModel;
import ir.taxi1880.operatormanagement.model.SameNameStreetsModel;
import ir.taxi1880.operatormanagement.push.AvaCrashReporter;

public class DataBase extends SQLiteOpenHelper {

    public static final String TAG = DataBase.class.getSimpleName();
    // TODO when you change the entitys structure, please increase the version of dataBase.
    private static int VERSION = 7;
    //TODO Do not change names any way
    private static String DB_NAME = "operators";
    private static String TRIP_TABLE = "Trip";
    private static String CITY_TABLE = "City";
    private static String MISTAKES_TABLE = "Mistakes";

    private static String SAME_NAME_STREETS_TABLE = "SameNameStreet";

    //**************************** Trip Column ****************************
    private static String COLUMN_TRIP_ID = "tripId";
    private static String COLUMN_OPERATOR_ID = "operatorId";
    private static String COLUMN_ORIGIN_TEXT = "originText";
    private static String COLUMN_ORIGIN_STATION = "originStation";
    private static String COLUMN_CITY = "city";
    private static String COLUMN_SAVE_DATE = "saveDate";
    private static String COLUMN_SEND_DATE = "sendDate";
    private static String COLUMN_TELL = "tell";
    private static String COLUMN_MOBILE = "mobile";
    private static String COLUMN_CUSTOMER_NAME = "customerName";
    private static String COLUMN_VOIP_ID = "voipId";
    private static String COLUMN_NEXT_RECORD = "nextRecord";
    private static String COLUMN_DESTINATION = "destination";
    private static String COLUMN_DESTINATION_STATION = "destinationStation";
    private static String PRICEABLE = "priceable";

    //***************************** City Column ***************************
    private static String COLUMN_CITY_ID = "cityId";
    private static String COLUMN_CITY_NAME = "cityName";
    private static String COLUMN_CITY_L_NAME = "cityLName";

    //************************** MISTAKES Column *********************************

    private static String COLUMN_MISTAKES_ID = "mistakesId";
    private static String COLUMN_MISTAKES_DATE = "mistakesDate";
    private static String COLUMN_MISTAKES_TIME = "mistakesTime";
    private static String COLUMN_MISTAKES_DESCRIPTION = "mistakesDescription";
    private static String COLUMN_MISTAKES_CITY = "mistakesCity";
    private static String COLUMN_MISTAKES_ADDRESS = "mistakesAddress";
    private static String COLUMN_MISTAKES_STATION_CODE = "mistakesStationCode";
    private static String COLUMN_MISTAKES_PASSENGER_VOICE = "mistakesPassengerVoice";
    private static String COLUMN_MISTAKES_SERVICE_CODE = "mistakesServiceCode";
    private static String COLUMN_MISTAKES_USER_CODE = "mistakesUserCode";
    private static String COLUMN_MISTAKES_TELL = "mistakesTell";
    private static String COLUMN_MISTAKES_MOBILE = "mistakesMobile";
    private static String COLUMN_MISTAKES_USER_CODE_CONTACT = "mistakesUserCodeContact";
    private static String COLUMN_MISTAKES_DEST_STATION_REGISTER_USER = "destStationRegisterUser";
    private static String COLUMN_MISTAKES_STATION_REGISTER_USER = "stationRegisterUser";
    private static String COLUMN_MISTAKES_CUSTOMER_NAME = "mistakesCustomerName";
    private static String COLUMN_MISTAKES_CON_DATE = "mistakesConDate";
    private static String COLUMN_MISTAKES_CON_TIME = "mistakesConTime";
    private static String COLUMN_MISTAKES_SEND_TIME = "mistakesSendTime";
    private static String COLUMN_MISTAKE_DESTINATION = "destination";
    private static String COLUMN_MISTAKES_DESTINATION_CODE = "destStation";
    private static String COLUMN_MISTAKES_REASONS = "mistakeReason";
    private static String COLUMN_MISTAKES_PRICE = "price";

    //************************************ SameStreetName Column ***************************************

    private static String COLUMN_CITY_Id_SN_Local = "localCityID";
    private static String COLUMN_CITY_CODE_SN = "cityCode";
    private static String COLUMN_CITY_NAME_SN = "cityName";
    private static String COLUMN_STREETS_NAME_WITH_SAME_NAME = "streetsNameWithSameName";
    private static String COLUMN_AROUND_STREET = "aroundStreet";

    public DataBase(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTripTable(sqLiteDatabase);
        createCityTable(sqLiteDatabase);
        createMistakesTable(sqLiteDatabase);
        createSameNameStreetsTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TRIP_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CITY_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MISTAKES_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void createTripTable(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + TRIP_TABLE +
                "(" + COLUMN_TRIP_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_OPERATOR_ID + " INTEGER," +
                COLUMN_ORIGIN_TEXT + " TEXT," +
                COLUMN_ORIGIN_STATION + " INTEGER," +
                COLUMN_DESTINATION + " TEXT," +
                COLUMN_DESTINATION_STATION + " INTEGER," +
                PRICEABLE + " INTEGER," +
                COLUMN_SAVE_DATE + " TEXT," +
                COLUMN_SEND_DATE + " TEXT," +
                COLUMN_CITY + " TEXT," +
                COLUMN_TELL + " TEXT," +
                COLUMN_MOBILE + " TEXT," +
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
            contentValues.put(COLUMN_DESTINATION, DBTripModel.getDestination());
            contentValues.put(COLUMN_DESTINATION_STATION, DBTripModel.getDestinationStation());
            contentValues.put(PRICEABLE, DBTripModel.getPriceable());
            contentValues.put(COLUMN_SAVE_DATE, DBTripModel.getSaveDate());
            contentValues.put(COLUMN_SEND_DATE, DBTripModel.getSendDate());
            contentValues.put(COLUMN_CITY, DBTripModel.getCity());
            contentValues.put(COLUMN_TELL, DBTripModel.getTell());
            contentValues.put(COLUMN_MOBILE, DBTripModel.getMobile());
            contentValues.put(COLUMN_CUSTOMER_NAME, DBTripModel.getCustomerName());
            contentValues.put(COLUMN_VOIP_ID, DBTripModel.getVoipId());
            sqLiteDatabase.insertWithOnConflict(TRIP_TABLE, COLUMN_TRIP_ID, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, insertTripRow method");
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
                DBTripModel.setDestination(cursor.getString(cursor.getColumnIndex(COLUMN_DESTINATION)));
                DBTripModel.setDestinationStation(cursor.getInt(cursor.getColumnIndex(COLUMN_DESTINATION_STATION)));
                DBTripModel.setPriceable(cursor.getInt(cursor.getColumnIndex(PRICEABLE)));
                DBTripModel.setSaveDate(cursor.getString(cursor.getColumnIndex(COLUMN_SAVE_DATE)));
                DBTripModel.setSendDate(cursor.getString(cursor.getColumnIndex(COLUMN_SEND_DATE)));
                DBTripModel.setCity(cursor.getInt(cursor.getColumnIndex(COLUMN_CITY)));
                DBTripModels.add(DBTripModel);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, getTripRow method");
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
                    DBTripModel.setDestination(cursor.getString(cursor.getColumnIndex(COLUMN_DESTINATION)));
                    DBTripModel.setDestinationStation(cursor.getInt(cursor.getColumnIndex(COLUMN_DESTINATION_STATION)));
                    DBTripModel.setPriceable(cursor.getInt(cursor.getColumnIndex(PRICEABLE)));
                    DBTripModel.setSaveDate(cursor.getString(cursor.getColumnIndex(COLUMN_SAVE_DATE)));
                    DBTripModel.setSendDate(cursor.getString(cursor.getColumnIndex(COLUMN_SEND_DATE)));
                    DBTripModel.setCity(cursor.getInt(cursor.getColumnIndex(COLUMN_CITY)));
                    DBTripModel.setTell(cursor.getString(cursor.getColumnIndex(COLUMN_TELL)));
                    DBTripModel.setMobile(cursor.getString(cursor.getColumnIndex(COLUMN_MOBILE)));
                    DBTripModel.setCustomerName(cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOMER_NAME)));
                    DBTripModel.setVoipId(cursor.getString(cursor.getColumnIndex(COLUMN_VOIP_ID)));
                    return DBTripModel;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, getTopAddress method");
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

    public void updateOriginStation(int tripId, int originStation) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ORIGIN_STATION, originStation);
        db.update(TRIP_TABLE, cv, COLUMN_TRIP_ID + " = " + tripId, null);
        //    update Trip set originStation='199' where tripId='39403'
    }

    public void updateDestinationStation(int tripId, int destinationStation) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DESTINATION_STATION, destinationStation);
        db.update(TRIP_TABLE, cv, COLUMN_TRIP_ID + " = " + tripId, null);
        //    update Trip set destinationStation='199' where tripId='39403'
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
    public void createSameNameStreetsTable(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + SAME_NAME_STREETS_TABLE +
                " (" + COLUMN_CITY_Id_SN_Local + " INTEGER PRIMARY KEY AutoIncrement," +
                COLUMN_CITY_CODE_SN + " TEXT," +
                COLUMN_CITY_NAME_SN + " TEXT," +
                COLUMN_STREETS_NAME_WITH_SAME_NAME + " TEXT," +
                COLUMN_AROUND_STREET + " TEXT)"
        );
    }

    public void insertSameNameStreets(SameNameStreetsModel sameNameStreetsModel) {
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_CITY_CODE_SN, sameNameStreetsModel.getCityId());
            contentValues.put(COLUMN_CITY_NAME_SN, sameNameStreetsModel.getCityName());
            contentValues.put(COLUMN_STREETS_NAME_WITH_SAME_NAME, sameNameStreetsModel.getSameNameStreet());
            contentValues.put(COLUMN_AROUND_STREET, sameNameStreetsModel.getAroundStreet());
            sqLiteDatabase.insert(SAME_NAME_STREETS_TABLE, null, contentValues);

//            Toast.makeText(MyApplication.currentActivity, "databaseUpdated", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, insertSameNameStreet method");
        }
    }

    public ArrayList<SameNameStreetsModel> getStreetNameWithSameName(String searchPhrase, int cityCode) {
        ArrayList<SameNameStreetsModel> models = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor res = sqLiteDatabase.rawQuery(
                "select * from " + SAME_NAME_STREETS_TABLE + " where " + COLUMN_STREETS_NAME_WITH_SAME_NAME +
                        " LIKE '" + searchPhrase + "%' AND " + COLUMN_CITY_CODE_SN + "=" + cityCode
                , null);
        if (res.getCount() == 0) {
            return null;
        }

        res.moveToFirst();
        while (!res.isAfterLast()) {
            SameNameStreetsModel sameNameStreetsModel = new SameNameStreetsModel(
                    res.getInt(res.getColumnIndex(COLUMN_CITY_CODE_SN)),
                    res.getString(res.getColumnIndex(COLUMN_CITY_NAME_SN)),
                    res.getString(res.getColumnIndex(COLUMN_STREETS_NAME_WITH_SAME_NAME)),
                    res.getString(res.getColumnIndex(COLUMN_AROUND_STREET)));
            models.add(sameNameStreetsModel);
            res.moveToNext();
        }
        return models;

    }

    public boolean isStreetNameWithSameName(String searchPhrase, int cityCode) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor res = sqLiteDatabase.rawQuery(
                "select * from " + SAME_NAME_STREETS_TABLE + " where " + COLUMN_STREETS_NAME_WITH_SAME_NAME +
                        " LIKE '" + searchPhrase + "%' AND " + COLUMN_CITY_CODE_SN + "=" + cityCode
                , null);
        if (res.getCount() == 0)
            return false;
        else {
            return true;
        }
    }

    public ArrayList<SameNameStreetsModel> getStreetNameWithSameNameEqualAndLessThree(String searchPhrase, int cityCode) {
        ArrayList<SameNameStreetsModel> models = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor res = sqLiteDatabase.rawQuery(
                "select * from " + SAME_NAME_STREETS_TABLE + " where " + COLUMN_STREETS_NAME_WITH_SAME_NAME +
                        " = '" + searchPhrase + "' AND " + COLUMN_CITY_CODE_SN + "=" + cityCode
                , null);
        if (res.getCount() == 0) {
            return null;
        }

        res.moveToFirst();
        while (!res.isAfterLast()) {
            SameNameStreetsModel sameNameStreetsModel = new SameNameStreetsModel(
                    res.getInt(res.getColumnIndex(COLUMN_CITY_CODE_SN)),
                    res.getString(res.getColumnIndex(COLUMN_CITY_NAME_SN)),
                    res.getString(res.getColumnIndex(COLUMN_STREETS_NAME_WITH_SAME_NAME)),
                    res.getString(res.getColumnIndex(COLUMN_AROUND_STREET)));
            models.add(sameNameStreetsModel);
            res.moveToNext();
        }
        return models;

    }


    public boolean isStreetNameWithSameNameEqualAndLessThree(String searchPhrase, int cityCode) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor res = sqLiteDatabase.rawQuery(
                "select * from " + SAME_NAME_STREETS_TABLE + " where " + COLUMN_STREETS_NAME_WITH_SAME_NAME +
                        " = '" + searchPhrase +  "' AND " + COLUMN_CITY_CODE_SN + "=" + cityCode
                , null);
        if (res.getCount() == 0)
            return false;
        else {
            return true;
        }
    }

    public void clearStreetNameWithSameNameTable() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(SAME_NAME_STREETS_TABLE, null, null);
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
            AvaCrashReporter.send(e, TAG + " class, insertCity method");
        }
    }

    public String getCityName(int id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_CITY_NAME + " FROM " + CITY_TABLE + " WHERE " + COLUMN_CITY_ID + "=" + id;
        //select cityName from city where cityId = id
        @SuppressLint("Recycle") Cursor res = sqLiteDatabase.rawQuery(query, null);

        if (res.getCount() == 0) {
            return "نامشخص";
        }

        res.moveToFirst();
        return res.getString(res.getColumnIndex(COLUMN_CITY_NAME));
    }

    public String getCityName2(int id) {
        if (id == 0) {
            return "نامشخص";
        }
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
            return "نامشخص";

        res.moveToFirst();
        return res.getString(res.getColumnIndex(COLUMN_CITY_NAME));
    }

    //******************************************************** MISTAKES Table *******************************************

    public void createMistakesTable(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + MISTAKES_TABLE +
                " (" + COLUMN_MISTAKES_ID + " INTEGER PRIMARY KEY," +
                COLUMN_MISTAKES_DATE + " TEXT," +
                COLUMN_MISTAKES_TIME + " TEXT," +
                COLUMN_MISTAKES_DESCRIPTION + " TEXT," +
                COLUMN_MISTAKES_CITY + " TEXT," +
                COLUMN_MISTAKES_ADDRESS + " TEXT," +
                COLUMN_MISTAKES_SERVICE_CODE + " INTEGER," +
                COLUMN_MISTAKES_USER_CODE + " INTEGER," +
                COLUMN_MISTAKES_TELL + " TEXT," +
                COLUMN_MISTAKES_MOBILE + " TEXT," +
                COLUMN_MISTAKES_USER_CODE_CONTACT + " TEXT," +
                COLUMN_MISTAKES_STATION_REGISTER_USER + " TEXT," +
                COLUMN_MISTAKES_DEST_STATION_REGISTER_USER + " TEXT," +
                COLUMN_MISTAKES_CUSTOMER_NAME + " TEXT," +
                COLUMN_MISTAKES_CON_DATE + " TEXT," +
                COLUMN_MISTAKES_CON_TIME + " TEXT," +
                COLUMN_MISTAKES_SEND_TIME + " TEXT," +
                COLUMN_MISTAKE_DESTINATION + " TEXT," +
                COLUMN_MISTAKES_DESTINATION_CODE + " TEXT," +
                COLUMN_MISTAKES_REASONS + " TEXT," +
                COLUMN_MISTAKES_PRICE + " TEXT," +
                COLUMN_MISTAKES_STATION_CODE + " INTEGER," +
                COLUMN_MISTAKES_PASSENGER_VOICE + " TEXT )"
        );
    }

    public void insertMistakes(AllMistakesModel mistakesModel) {
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_MISTAKES_DESCRIPTION, mistakesModel.getDescription());
            contentValues.put(COLUMN_MISTAKES_ID, mistakesModel.getId());
            contentValues.put(COLUMN_MISTAKES_DATE, mistakesModel.getDate());
            contentValues.put(COLUMN_MISTAKES_TIME, mistakesModel.getTime());
            contentValues.put(COLUMN_MISTAKES_CITY, mistakesModel.getCity());
            contentValues.put(COLUMN_MISTAKES_ADDRESS, mistakesModel.getAddress());
            contentValues.put(COLUMN_MISTAKES_STATION_CODE, mistakesModel.getStationCode());
            contentValues.put(COLUMN_MISTAKES_SERVICE_CODE, mistakesModel.getServiceCode());
            contentValues.put(COLUMN_MISTAKES_USER_CODE, mistakesModel.getUserCode());
            contentValues.put(COLUMN_MISTAKES_TELL, mistakesModel.getTell());
            contentValues.put(COLUMN_MISTAKES_MOBILE, mistakesModel.getMobile());
            contentValues.put(COLUMN_MISTAKES_USER_CODE_CONTACT, mistakesModel.getUserCodeContact());
            contentValues.put(COLUMN_MISTAKES_DEST_STATION_REGISTER_USER, mistakesModel.getDestStationRegisterUser());
            contentValues.put(COLUMN_MISTAKES_STATION_REGISTER_USER, mistakesModel.getStationRegisterUser());
            contentValues.put(COLUMN_MISTAKES_CUSTOMER_NAME, mistakesModel.getCustomerName());
            contentValues.put(COLUMN_MISTAKES_CON_DATE, mistakesModel.getConDate());
            contentValues.put(COLUMN_MISTAKES_CON_TIME, mistakesModel.getConTime());
            contentValues.put(COLUMN_MISTAKES_SEND_TIME, mistakesModel.getSendTime());
            contentValues.put(COLUMN_MISTAKES_DESTINATION_CODE, mistakesModel.getDestStation());
            contentValues.put(COLUMN_MISTAKE_DESTINATION, mistakesModel.getDestination());
            contentValues.put(COLUMN_MISTAKES_REASONS, mistakesModel.getMistakeReason());
            contentValues.put(COLUMN_MISTAKES_PRICE, mistakesModel.getPrice());
            contentValues.put(COLUMN_MISTAKES_PASSENGER_VOICE, mistakesModel.getVoipId());
            sqLiteDatabase.insertWithOnConflict(MISTAKES_TABLE, COLUMN_MISTAKES_ID, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + " class, insertMistakes method");
        }
    }

    public AllMistakesModel getMistakesRow() {
        AllMistakesModel pendingMistakesModel = new AllMistakesModel();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor res = sqLiteDatabase.rawQuery("select * from " + MISTAKES_TABLE, null);
        if (res.getCount() == 0) return null;

        res.moveToFirst();

        pendingMistakesModel.setId(res.getInt(res.getColumnIndex(COLUMN_MISTAKES_ID)));
        pendingMistakesModel.setDate(res.getString(res.getColumnIndex(COLUMN_MISTAKES_DATE)));
        pendingMistakesModel.setTime(res.getString(res.getColumnIndex(COLUMN_MISTAKES_TIME)));
        pendingMistakesModel.setDescription(res.getString(res.getColumnIndex(COLUMN_MISTAKES_DESCRIPTION)));
        pendingMistakesModel.setCity(res.getInt(res.getColumnIndex(COLUMN_MISTAKES_CITY)));
        pendingMistakesModel.setAddress(res.getString(res.getColumnIndex(COLUMN_MISTAKES_ADDRESS)));
        pendingMistakesModel.setServiceCode(res.getInt(res.getColumnIndex(COLUMN_MISTAKES_SERVICE_CODE)));
        pendingMistakesModel.setUserCode(res.getInt(res.getColumnIndex(COLUMN_MISTAKES_USER_CODE)));
        pendingMistakesModel.setTell(res.getString(res.getColumnIndex(COLUMN_MISTAKES_TELL)));
        pendingMistakesModel.setMobile(res.getString(res.getColumnIndex(COLUMN_MISTAKES_MOBILE)));
        pendingMistakesModel.setUserCodeContact(res.getInt(res.getColumnIndex(COLUMN_MISTAKES_USER_CODE_CONTACT)));
        pendingMistakesModel.setStationRegisterUser(res.getInt(res.getColumnIndex(COLUMN_MISTAKES_STATION_REGISTER_USER)));
        pendingMistakesModel.setDestStationRegisterUser(res.getInt(res.getColumnIndex(COLUMN_MISTAKES_DEST_STATION_REGISTER_USER)));
        pendingMistakesModel.setCustomerName(res.getString(res.getColumnIndex(COLUMN_MISTAKES_CUSTOMER_NAME)));
        pendingMistakesModel.setConDate(res.getString(res.getColumnIndex(COLUMN_MISTAKES_CON_DATE)));
        pendingMistakesModel.setConTime(res.getString(res.getColumnIndex(COLUMN_MISTAKES_CON_TIME)));
        pendingMistakesModel.setSendTime(res.getString(res.getColumnIndex(COLUMN_MISTAKES_SEND_TIME)));
        pendingMistakesModel.setDestination(res.getString(res.getColumnIndex(COLUMN_MISTAKE_DESTINATION)));
        pendingMistakesModel.setDestStation(res.getString(res.getColumnIndex(COLUMN_MISTAKES_DESTINATION_CODE)));
        pendingMistakesModel.setMistakeReason(res.getString(res.getColumnIndex(COLUMN_MISTAKES_REASONS)));
        pendingMistakesModel.setPrice(res.getString(res.getColumnIndex(COLUMN_MISTAKES_PRICE)));
        pendingMistakesModel.setStationCode(res.getInt(res.getColumnIndex(COLUMN_MISTAKES_STATION_CODE)));
        pendingMistakesModel.setVoipId(res.getString(res.getColumnIndex(COLUMN_MISTAKES_PASSENGER_VOICE)));

        return pendingMistakesModel;
    }

    public AllMistakesModel moveNextMistakes(int id) {
        AllMistakesModel pendingMistakesModel = new AllMistakesModel();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor res = sqLiteDatabase.rawQuery("select * from " + MISTAKES_TABLE + " WHERE " + COLUMN_MISTAKES_ID + "=" + id, null);
        if (res.getCount() == 0) return null;
        res.moveToPosition(res.getPosition() + 1);

        pendingMistakesModel.setId(res.getInt(res.getColumnIndex(COLUMN_MISTAKES_ID)));
        pendingMistakesModel.setDate(res.getString(res.getColumnIndex(COLUMN_MISTAKES_DATE)));
        pendingMistakesModel.setTime(res.getString(res.getColumnIndex(COLUMN_MISTAKES_TIME)));
        pendingMistakesModel.setDescription(res.getString(res.getColumnIndex(COLUMN_MISTAKES_DESCRIPTION)));
        pendingMistakesModel.setCity(res.getInt(res.getColumnIndex(COLUMN_MISTAKES_CITY)));
        pendingMistakesModel.setAddress(res.getString(res.getColumnIndex(COLUMN_MISTAKES_ADDRESS)));
        pendingMistakesModel.setServiceCode(res.getInt(res.getColumnIndex(COLUMN_MISTAKES_SERVICE_CODE)));
        pendingMistakesModel.setUserCode(res.getInt(res.getColumnIndex(COLUMN_MISTAKES_USER_CODE)));
        pendingMistakesModel.setTell(res.getString(res.getColumnIndex(COLUMN_MISTAKES_TELL)));
        pendingMistakesModel.setMobile(res.getString(res.getColumnIndex(COLUMN_MISTAKES_MOBILE)));
        pendingMistakesModel.setUserCodeContact(res.getInt(res.getColumnIndex(COLUMN_MISTAKES_USER_CODE_CONTACT)));
        pendingMistakesModel.setDestStationRegisterUser(res.getInt(res.getColumnIndex(COLUMN_MISTAKES_DEST_STATION_REGISTER_USER)));
        pendingMistakesModel.setStationRegisterUser(res.getInt(res.getColumnIndex(COLUMN_MISTAKES_STATION_REGISTER_USER)));
        pendingMistakesModel.setCustomerName(res.getString(res.getColumnIndex(COLUMN_MISTAKES_CUSTOMER_NAME)));
        pendingMistakesModel.setConDate(res.getString(res.getColumnIndex(COLUMN_MISTAKES_CON_DATE)));
        pendingMistakesModel.setConTime(res.getString(res.getColumnIndex(COLUMN_MISTAKES_CON_TIME)));
        pendingMistakesModel.setSendTime(res.getString(res.getColumnIndex(COLUMN_MISTAKES_SEND_TIME)));
        pendingMistakesModel.setDestination(res.getString(res.getColumnIndex(COLUMN_MISTAKE_DESTINATION)));
        pendingMistakesModel.setDestStation(res.getString(res.getColumnIndex(COLUMN_MISTAKES_DESTINATION_CODE)));
        pendingMistakesModel.setMistakeReason(res.getString(res.getColumnIndex(COLUMN_MISTAKES_REASONS)));
        pendingMistakesModel.setPrice(res.getString(res.getColumnIndex(COLUMN_MISTAKES_PRICE)));
        pendingMistakesModel.setStationCode(res.getInt(res.getColumnIndex(COLUMN_MISTAKES_STATION_CODE)));
        pendingMistakesModel.setVoipId(res.getString(res.getColumnIndex(COLUMN_MISTAKES_PASSENGER_VOICE)));

        return pendingMistakesModel;
    }

    public void deleteMistakesRow(int id) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        boolean b = sqLiteDatabase.delete(MISTAKES_TABLE, COLUMN_MISTAKES_ID + "=" + id, null) > 0;

        if (b) {
            Log.i("deleteMistakesRow", "deleteRow: = true  " + id);
        } else {
            Log.i("deleteMistakesRow", "deleteRow: = false  " + id);
        }
    }

    public int getMistakesCount() {
        String countQuery = "SELECT  * FROM " + MISTAKES_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void clearMistakeTable() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(MISTAKES_TABLE, null, null);
    }
}
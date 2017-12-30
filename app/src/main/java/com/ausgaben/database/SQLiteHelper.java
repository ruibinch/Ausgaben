package com.ausgaben.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ruibin on 1/1/2017.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private static SQLiteHelper mInstance = null;

    public static final String TABLE_EXPENSES = "expenses";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_CURRENCY = "currency";
    public static final String COLUMN_FOREXRATE = "forexrate";
    public static final String COLUMN_FOREXRATE_EURTOSGD = "forexrate_eurtosgd";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_IMAGEPATH = "imagepath";

    private static final String DATABASE_NAME = "expenses.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
        "CREATE TABLE " + TABLE_EXPENSES + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DATE + " INTEGER NOT NULL, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_CATEGORY + " TEXT NOT NULL, " +
            COLUMN_AMOUNT + " DECIMAL(10,2) NOT NULL, " +
            COLUMN_CURRENCY + " TEXT NOT NULL, " +
            COLUMN_FOREXRATE + " DECIMAL(5,2) NOT NULL, " +
            COLUMN_FOREXRATE_EURTOSGD + " DECIMAL(3,2) NOT NULL, " +
            COLUMN_CITY + " TEXT NOT NULL, " +
            COLUMN_COUNTRY + " TEXT NOT NULL, " +
            COLUMN_IMAGEPATH + " TEXT);";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // DB is a Singleton
    static SQLiteHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SQLiteHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        onCreate(database);
    }

}

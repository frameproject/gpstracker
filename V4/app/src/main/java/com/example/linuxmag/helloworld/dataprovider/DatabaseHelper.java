package com.example.linuxmag.helloworld.dataprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "data";
    static final String LOG_APP_TABLE_NAME = "logs";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + LOG_APP_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " date TEXT NOT NULL, " +
                    " app TEXT NOT NULL,  " +
                    " info TEXT NOT NULL);";


    DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_DB_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +  LOG_APP_TABLE_NAME);
        onCreate(db);
    }
}
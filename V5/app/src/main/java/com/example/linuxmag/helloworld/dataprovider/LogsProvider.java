package com.example.linuxmag.helloworld.dataprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class LogsProvider extends ContentProvider {

    private SQLiteDatabase db;

    private static HashMap<String, String> LOG_PROJECTION_MAP;

    static final String PROVIDER_NAME = "com.example.linuxmag.helloworld.Info";
    static final String URL = "content://" + PROVIDER_NAME + "/logs";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String _ID = "_id";
    public static final String DATE = "date";
    public static final String APP = "app";
    public static final String INFO = "info";

    static final int LOG = 1;
    static final int LOG_ID = 2;

    static final UriMatcher uriMatcher;

    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "logs", LOG);
        uriMatcher.addURI(PROVIDER_NAME, "log/#", LOG_ID);
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long rowID = db.insert(DatabaseHelper.LOG_APP_TABLE_NAME, "", values);

        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(DatabaseHelper.LOG_APP_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case LOG:
                qb.setProjectionMap(LOG_PROJECTION_MAP);
                break;

            case LOG_ID:
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor c = qb.query(db,	projection,	selection, selectionArgs,null, null, null);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case LOG:
                count = db.delete(DatabaseHelper.LOG_APP_TABLE_NAME, selection, selectionArgs);
                break;

            case LOG_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(DatabaseHelper.LOG_APP_TABLE_NAME, _ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            /**
             * Get all logs
             */
            case LOG:
                return "vnd.android.cursor.dir/vnd.logprovider.log";

            /**
             * Get a particular log
             */
            case LOG_ID:
                return "vnd.android.cursor.item/vnd.logprovider.logs";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case LOG:
                count = db.update(DatabaseHelper.LOG_APP_TABLE_NAME, values, selection, selectionArgs);
                break;

            case LOG_ID:
                count = db.update(DatabaseHelper.LOG_APP_TABLE_NAME, values, _ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
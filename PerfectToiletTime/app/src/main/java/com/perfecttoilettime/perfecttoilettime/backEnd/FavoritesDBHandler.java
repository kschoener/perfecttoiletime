package com.perfecttoilettime.perfecttoilettime.backEnd;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.perfecttoilettime.perfecttoilettime.frontEnd.Bathroom;

/**
 * Created by Mark on 11/12/16.
 */

public class FavoritesDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "favoriteBathrooms.db";
    private static final String TABLE_BATHROOMS = "favoriteBathrooms";
    private static final String COLUMN_BATHROOMNAME = "name";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";

    public FavoritesDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE" + TABLE_BATHROOMS + "(" +
                COLUMN_BATHROOMNAME + " TEXT " +
                COLUMN_LATITUDE + " TEXT " +
                COLUMN_LONGITUDE + " TEXT " +
                ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_BATHROOMS);
        onCreate(db);
    }

    public void addBathroom(Bathroom bathroom){
        ContentValues values = new ContentValues();
        values.put(COLUMN_BATHROOMNAME, bathroom.getBathroomName());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_BATHROOMS, null, values);
        db.close();
    }

    public void deleteBathroom(String bathroomName){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_BATHROOMS + " WHERE " + COLUMN_BATHROOMNAME + "=\"" + bathroomName + "\";");
    }
}

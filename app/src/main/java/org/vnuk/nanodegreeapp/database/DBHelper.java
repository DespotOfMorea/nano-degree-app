package org.vnuk.nanodegreeapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "persons.db";
    private static final int DATABASE_VERSION = 3;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_PERSONS_TABLE =

                "CREATE TABLE " + PersonContract.PersonEntry.TABLE_NAME_PERSONS + " (" +
                        PersonContract.PersonEntry._ID                + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PersonContract.PersonEntry.COLUMN_TITLE       + " TEXT NOT NULL, "                 +
                        PersonContract.PersonEntry.COLUMN_FIRST_NAME  + " TEXT NOT NULL, "                 +
                        PersonContract.PersonEntry.COLUMN_LAST_NAME   + " TEXT NOT NULL, "                    +
                        PersonContract.PersonEntry.COLUMN_TIMESTAMP   + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_PERSONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PersonContract.PersonEntry.TABLE_NAME_PERSONS);
        onCreate(sqLiteDatabase);
    }
}
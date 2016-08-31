package com.allattentionhere.conferencemanager.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.allattentionhere.conferencemanager.Data.DataContract.*;

public class DataDbHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "allattentionhere.db";
    private static final int DATABASE_VERSION = 3;

    public DataDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String SQL_CREATE_ADMIN_TABLE =
                "CREATE TABLE " + AdminEntry.TABLE_NAME + " (" +
                        AdminEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        AdminEntry.COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, " +
                        AdminEntry.COLUMN_PASSWORD + " TEXT NOT NULL, " +
                        AdminEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                        AdminEntry.COLUMN_AGE + " INTEGER NOT NULL " +
                        " );";

        final String SQL_CREATE_DOCTOR_TABLE =
                "CREATE TABLE " + DoctorEntry.TABLE_NAME + " (" +
                        DoctorEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DoctorEntry.COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, " +
                        DoctorEntry.COLUMN_PASSWORD + " TEXT NOT NULL, " +
                        DoctorEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                        DoctorEntry.COLUMN_AGE + " INTEGER NOT NULL " +
                        " );";

        final String SQL_CREATE_CONFERENCE_TABLE =
                "CREATE TABLE " + ConferenceEntry.TABLE_NAME + " (" +
                        ConferenceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        ConferenceEntry.COLUMN_USER_ID + " INTEGER NOT NULL, " +
                        ConferenceEntry.COLUMN_TOPIC + " TEXT NOT NULL, " +
                        ConferenceEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                        ConferenceEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                        ConferenceEntry.COLUMN_READ_TAG + " TEXT DEFAULT '1' " +
                        " );";

        final String SQL_CREATE_SUGGESTION_TABLE =
                "CREATE TABLE " + SuggestionEntry.TABLE_NAME + " (" +
                        SuggestionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        SuggestionEntry.COLUMN_USER_ID + " INTEGER NOT NULL, " +
                        SuggestionEntry.COLUMN_TOPIC + " TEXT NOT NULL, " +
                        SuggestionEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                        SuggestionEntry.COLUMN_AVAILABILITY_DATE + " INTEGER NOT NULL, " +
                        SuggestionEntry.COLUMN_READ_TAG + " TEXT DEFAULT '1' " +
                        " );";

        db.execSQL(SQL_CREATE_ADMIN_TABLE);
        db.execSQL(SQL_CREATE_DOCTOR_TABLE);
        db.execSQL(SQL_CREATE_CONFERENCE_TABLE);
        db.execSQL(SQL_CREATE_SUGGESTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + AdminEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DoctorEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ConferenceEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SuggestionEntry.TABLE_NAME);
        onCreate(db);
    }
}

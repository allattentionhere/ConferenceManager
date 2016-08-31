package com.allattentionhere.conferencemanager.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;


public class DataProvider extends ContentProvider
{
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DataDbHelper mConferenceDatabaseHelper;

    static final int ADMIN = 200;
    static final int ADMIN_WITH_ID = 201;
    static final int ADMIN_WITH_USERNAME = 202;

    static final int DOCTOR = 300;
    static final int DOCTOR_WITH_ID = 301;
    static final int DOCTOR_WITH_USERNAME = 302;

    static final int CONFERENCE = 400;
    static final int CONFERENCE_WITH_ID = 401;

    static final int SUGGESTION = 500;
    static final int SUGGESTION_WITH_ID = 501;

    private static final SQLiteQueryBuilder sUserwithAdmin;
    private static final SQLiteQueryBuilder sUserwithDoctor;

    static
    {
        sUserwithAdmin = new SQLiteQueryBuilder();
        sUserwithDoctor = new SQLiteQueryBuilder();

        sUserwithAdmin.setTables(
                DataContract.AdminEntry.TABLE_NAME
        );

        sUserwithDoctor.setTables(
                DataContract.DoctorEntry.TABLE_NAME
        );
    }

    private static final String sAdminIDSelection =
            DataContract.AdminEntry.TABLE_NAME + "." + DataContract.AdminEntry._ID + " = ? ";

    private static final String sAdminNameSelection =
            DataContract.AdminEntry.TABLE_NAME + "." + DataContract.AdminEntry.COLUMN_USERNAME + " = ? ";

    private static final String sDoctorIDSelection =
            DataContract.DoctorEntry.TABLE_NAME + "." + DataContract.DoctorEntry._ID + " = ? ";

    private static final String sDoctorNameSelection =
            DataContract.DoctorEntry.TABLE_NAME + "." + DataContract.DoctorEntry.COLUMN_USERNAME + " = ? ";

    public static final String sConferenceIDSelection =
            DataContract.ConferenceEntry.TABLE_NAME + "." + DataContract.ConferenceEntry._ID + " = ? ";

    public static final String sConferenceByUserIDSelection =
            DataContract.ConferenceEntry.TABLE_NAME + "." + DataContract.ConferenceEntry.COLUMN_USER_ID + " = ? ";

    public static final String sSuggestionIDSelection =
            DataContract.SuggestionEntry.TABLE_NAME + "." + DataContract.SuggestionEntry._ID + " = ? ";

    public static final String sSuggestionByUserIDSelection =
            DataContract.SuggestionEntry.TABLE_NAME + "." + DataContract.SuggestionEntry.COLUMN_USER_ID + " = ? ";

    static UriMatcher buildUriMatcher()
    {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DataContract.PATH_ADMIN, ADMIN);
        matcher.addURI(authority, DataContract.PATH_ADMIN + "/*", ADMIN_WITH_ID);
        matcher.addURI(authority, DataContract.PATH_ADMIN + "/*/#", ADMIN_WITH_USERNAME);

        matcher.addURI(authority, DataContract.PATH_DOCTOR, DOCTOR);
        matcher.addURI(authority, DataContract.PATH_DOCTOR + "/*", DOCTOR_WITH_ID);
        matcher.addURI(authority, DataContract.PATH_DOCTOR + "/*/#", DOCTOR_WITH_USERNAME);

        matcher.addURI(authority, DataContract.PATH_CONFERENCE, CONFERENCE);
        matcher.addURI(authority, DataContract.PATH_CONFERENCE + "/*", CONFERENCE_WITH_ID);

        matcher.addURI(authority, DataContract.PATH_SUGGESTION, SUGGESTION);
        matcher.addURI(authority, DataContract.PATH_SUGGESTION + "/*", SUGGESTION_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mConferenceDatabaseHelper = new DataDbHelper(getContext());
        return true;
    }

    private Cursor getAdminByUserName(Uri uri, String[] projection, String sortOrder)
    {
        String username = DataContract.AdminEntry.getAdminNameFromUri(uri);

        return sUserwithAdmin.query(mConferenceDatabaseHelper.getReadableDatabase(),
                projection,
                sAdminNameSelection,
                new String[]{username},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getDoctorByUserName(Uri uri, String[] projection, String sortOrder)
    {
        String username = DataContract.DoctorEntry.getDoctorNameFromUri(uri);

        return sUserwithDoctor.query(mConferenceDatabaseHelper.getReadableDatabase(),
                projection,
                sDoctorNameSelection,
                new String[]{username},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getAdminByID(Uri uri, String[] projection, String sortOrder)
    {
        long id = DataContract.AdminEntry.getAdminIDFromUri(uri);
        return sUserwithAdmin.query(mConferenceDatabaseHelper.getReadableDatabase(),
                projection,
                sAdminIDSelection,
                new String[]{Long.toString(id)},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getDoctorByID(Uri uri, String[] projection, String sortOrder)
    {
        long id = DataContract.DoctorEntry.getDoctorIDFromUri(uri);
        return sUserwithDoctor.query(mConferenceDatabaseHelper.getReadableDatabase(),
                projection,
                sDoctorIDSelection,
                new String[]{Long.toString(id)},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getConferenceByID(Uri uri, String[] projection, String sortOrder)
    {
        long id = DataContract.ConferenceEntry.getConferenceIDFromUri(uri);
        return mConferenceDatabaseHelper.getReadableDatabase().query(
                DataContract.ConferenceEntry.TABLE_NAME,
                projection,
                sConferenceIDSelection,
                new String[]{Long.toString(id)},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getSuggestionByID(Uri uri, String[] projection, String sortOrder)
    {
        long id = DataContract.SuggestionEntry.getSuggestionIDFromUri(uri);
        return mConferenceDatabaseHelper.getReadableDatabase().query(
                DataContract.SuggestionEntry.TABLE_NAME,
                projection,
                sSuggestionIDSelection,
                new String[]{Long.toString(id)},
                null,
                null,
                sortOrder
        );
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        Cursor retCursor;
        switch (sUriMatcher.match(uri))
        {
            case ADMIN:{
                retCursor = mConferenceDatabaseHelper.getReadableDatabase().query(
                        DataContract.AdminEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case ADMIN_WITH_ID: {
                retCursor = getAdminByID(uri, projection, sortOrder);
                break;
            }

            case ADMIN_WITH_USERNAME:{
                retCursor = getAdminByUserName(uri, projection, sortOrder);
                break;
            }

            case DOCTOR:{
                retCursor = mConferenceDatabaseHelper.getReadableDatabase().query(
                        DataContract.DoctorEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case DOCTOR_WITH_ID: {
                retCursor = getDoctorByID(uri, projection, sortOrder);
                break;
            }

            case DOCTOR_WITH_USERNAME:{
                retCursor = getDoctorByUserName(uri, projection, sortOrder);
                break;
            }

            case CONFERENCE: {
                retCursor = mConferenceDatabaseHelper.getReadableDatabase().query(
                        DataContract.ConferenceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case CONFERENCE_WITH_ID:{
                retCursor = getConferenceByID(uri , projection , sortOrder);
                break;
            }

            case SUGGESTION: {
                retCursor = mConferenceDatabaseHelper.getReadableDatabase().query(
                        DataContract.SuggestionEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case SUGGESTION_WITH_ID:{
                retCursor = getSuggestionByID(uri , projection , sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri)
    {
        final int match = sUriMatcher.match(uri);

        switch (match)
        {
            case ADMIN:
                return DataContract.AdminEntry.CONTENT_TYPE;
            case ADMIN_WITH_ID:
                return DataContract.AdminEntry.CONTENT_ITEM_TYPE;
            case ADMIN_WITH_USERNAME:
                return DataContract.AdminEntry.CONTENT_ITEM_TYPE;
            case DOCTOR:
                return DataContract.DoctorEntry.CONTENT_TYPE;
            case DOCTOR_WITH_ID:
                return DataContract.DoctorEntry.CONTENT_ITEM_TYPE;
            case DOCTOR_WITH_USERNAME:
                return DataContract.DoctorEntry.CONTENT_ITEM_TYPE;
            case CONFERENCE:
                return DataContract.ConferenceEntry.CONTENT_TYPE;
            case CONFERENCE_WITH_ID:
                return DataContract.ConferenceEntry.CONTENT_TYPE;
            case SUGGESTION:
                return DataContract.SuggestionEntry.CONTENT_TYPE;
            case SUGGESTION_WITH_ID:
                return DataContract.SuggestionEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        final SQLiteDatabase db = mConferenceDatabaseHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri))
        {
            case ADMIN:
            {
                long _id = db.insert(DataContract.AdminEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DataContract.AdminEntry.buildAdminUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case DOCTOR:
            {
                long _id = db.insert(DataContract.DoctorEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DataContract.DoctorEntry.buildDoctorUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CONFERENCE:
            {
                long _id = db.insert(DataContract.ConferenceEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DataContract.ConferenceEntry.buildConferenceUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case SUGGESTION:
            {
                long _id = db.insert(DataContract.SuggestionEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DataContract.SuggestionEntry.buildSuggestionUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = mConferenceDatabaseHelper.getWritableDatabase();
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (sUriMatcher.match(uri))
        {
            case ADMIN:
                rowsDeleted = db.delete(
                        DataContract.AdminEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case DOCTOR:
                rowsDeleted = db.delete(
                        DataContract.DoctorEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CONFERENCE:
                rowsDeleted = db.delete(
                        DataContract.ConferenceEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SUGGESTION:
                rowsDeleted = db.delete(
                        DataContract.SuggestionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = mConferenceDatabaseHelper.getWritableDatabase();
        int rowsUpdated;

        switch (sUriMatcher.match(uri))
        {
            case ADMIN:
                rowsUpdated = db.update(DataContract.AdminEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case DOCTOR:
                rowsUpdated = db.update(DataContract.DoctorEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CONFERENCE:
                rowsUpdated = db.update(DataContract.ConferenceEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case SUGGESTION:
                rowsUpdated = db.update(DataContract.SuggestionEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}

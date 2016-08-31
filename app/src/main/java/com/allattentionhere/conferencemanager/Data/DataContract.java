package com.allattentionhere.conferencemanager.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class DataContract
{
    public static final String CONTENT_AUTHORITY = "com.allattentionhere";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ADMIN = "admin";
    public static final String PATH_DOCTOR = "doctor";
    public static final String PATH_CONFERENCE = "conference";
    public static final String PATH_SUGGESTION = "suggestion";

    public static final class AdminEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ADMIN).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ADMIN;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ADMIN;

        public static final String TABLE_NAME = "admin";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AGE = "age";

        public static Uri buildAdminUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getAdminIDFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getAdminNameFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static Uri buildAdminWithName(String userName)
        {
            return CONTENT_URI.buildUpon().appendPath(userName).build();
        }
    }

    public static final class DoctorEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DOCTOR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DOCTOR;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DOCTOR;

        public static final String TABLE_NAME = "doctor";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AGE = "age";


        public static Uri buildDoctorUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getDoctorIDFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getDoctorNameFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static Uri buildDoctorWithName(String userName)
        {
            return CONTENT_URI.buildUpon().appendPath(userName).build();
        }
    }

    public static final class ConferenceEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONFERENCE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONFERENCE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONFERENCE;

        public static final String TABLE_NAME = "conference";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_TOPIC = "topic";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_READ_TAG = "read_tag";

        public static Uri buildConferenceUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getConferenceIDFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static final class SuggestionEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUGGESTION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUGGESTION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUGGESTION;

        public static final String TABLE_NAME = "suggestion";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_TOPIC = "topic";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_AVAILABILITY_DATE = "availability_date";
        public static final String COLUMN_READ_TAG = "read_tag";

        public static Uri buildSuggestionUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getSuggestionIDFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }
}

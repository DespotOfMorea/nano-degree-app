package org.vnuk.nanodegreeapp.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static org.vnuk.nanodegreeapp.database.PersonContract.PersonEntry.TABLE_NAME_PERSONS;

public class PersonContentProvider extends ContentProvider {

    private DBHelper dbHelper;
    public static final int PERSONS = 100;
    public static final int PERSONS_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PersonContract.AUTHORITY, PersonContract.PATH_PERSONS, PERSONS);
        uriMatcher.addURI(PersonContract.AUTHORITY, PersonContract.PATH_PERSONS + "/#", PERSONS_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DBHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case PERSONS:
                retCursor = db.query(TABLE_NAME_PERSONS, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case PERSONS_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};

                retCursor = db.query(TABLE_NAME_PERSONS, projection, mSelection, mSelectionArgs, null, null, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        int match = sUriMatcher.match(uri);

        switch (match) {
            case PERSONS:
                return "vnd.android.cursor.dir" + "/" + PersonContract.AUTHORITY + "/" + PersonContract.PATH_PERSONS;
            case PERSONS_ID:
                return "vnd.android.cursor.item" + "/" + PersonContract.AUTHORITY + "/" + PersonContract.PATH_PERSONS;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PERSONS:
                long id = db.insert(TABLE_NAME_PERSONS, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(PersonContract.PersonEntry.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numRowsDeleted;

        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {
            case PERSONS:
                numRowsDeleted = db.delete(TABLE_NAME_PERSONS, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int tasksUpdated;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case PERSONS_ID:
                String id = uri.getPathSegments().get(1);
                tasksUpdated = db.update(TABLE_NAME_PERSONS, values, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksUpdated;
    }
}

package com.guilardi.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by deguilardi on 5/14/18.
 */

public class MoviesProvider extends ContentProvider{

    public static final int CODE_MOVIES = 100;
    public static final int CODE_MOVIE = 101;

    private static final UriMatcher mUriMatcher = buildUriMatcher();
    private DbHelper mDbHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Movie.CONTENT_AUTHORITY;

        matcher.addURI(authority, Movie.PATH_MOVIES, CODE_MOVIES);
        matcher.addURI(authority, Movie.PATH_MOVIES + "/#", CODE_MOVIE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsInserted = 0;

        switch (mUriMatcher.match(uri)) {
            case CODE_MOVIES:
                final String tableName = Movie.MovieEntry.TABLE_NAME;
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(tableName, null, value);
                        if (id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;

            default:
                return super.bulkInsert(uri, values);
        }

        return rowsInserted;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (mUriMatcher.match(uri)){
            case CODE_MOVIES:{
                cursor = mDbHelper.getReadableDatabase().query(
                        Movie.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted;

        /*
         * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
         * deleted. However, if we do pass null and delete all of the rows in the table, we won't
         * know how many rows were deleted. According to the documentation for SQLiteDatabase,
         * passing "1" for the selection will delete all rows and return the number of rows
         * deleted, which is what the caller of this method expects.
         */
        if (null == selection) selection = "1";

        switch (mUriMatcher.match(uri)) {
            case CODE_MOVIES:
                numRowsDeleted = mDbHelper.getWritableDatabase().delete(
                        Movie.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("getType not implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new RuntimeException("insert not implemented");
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int output = 0;
        switch (mUriMatcher.match(uri)) {
            case CODE_MOVIE:
                output = mDbHelper.getReadableDatabase().update(Movie.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

        }
        return output;
    }
}

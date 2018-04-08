package io.github.anvell.popularmovies.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MoviesProvider extends ContentProvider {

    private static final int CODE_MOVIE_ID = 100;
    private static final int CODE_MOVIES = 101;

    private MoviesDatabaseHelper mDatabaseHelper;
    private UriMatcher mUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {

        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIE, CODE_MOVIES);
        matcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIE + "/#", CODE_MOVIE_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new MoviesDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        Cursor cursor;

        switch (mUriMatcher.match(uri)) {
            case CODE_MOVIES:
                cursor = db.query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CODE_MOVIE_ID:
                long id = ContentUris.parseId(uri);
                cursor = db.query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                return null;
        }

        if (getContext() != null)
            cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case CODE_MOVIES:
                return MoviesContract.MovieEntry.CONTENT_TYPE;
            case CODE_MOVIE_ID:
                return MoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        Uri returnUri;

        if (mUriMatcher.match(uri) == CODE_MOVIES) {
            long movieId = db.insertWithOnConflict(MoviesContract.MovieEntry.TABLE_NAME, null, values,
                                              SQLiteDatabase.CONFLICT_REPLACE);

            if (movieId > -1) {
                returnUri = MoviesContract.MovieEntry.buildMovieUri(movieId);
            } else {
                throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
            }
        } else
            throw new UnsupportedOperationException("Unknown uri: " + uri);

        if (getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int rows;

        switch (mUriMatcher.match(uri)) {
            case CODE_MOVIES:
                rows = db.delete(MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CODE_MOVIE_ID:
                long id = ContentUris.parseId(uri);
                rows = db.delete(MoviesContract.MovieEntry.TABLE_NAME,
                        MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(id)});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rows > 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int rows;

        switch (mUriMatcher.match(uri)) {
            case CODE_MOVIES:
                rows = db.update(MoviesContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CODE_MOVIE_ID:
                long id = ContentUris.parseId(uri);
                rows = db.update(MoviesContract.MovieEntry.TABLE_NAME, values,
                                 MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                                 new String[]{String.valueOf(id)});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rows > 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }
}

package io.github.anvell.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class MoviesDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movieDetails.db";

    MoviesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME
                + " (" + MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                         MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                         MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                         MoviesContract.MovieEntry.COLUMN_DETAILS + " TEXT NOT NULL"
                + ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not yet
    }
}

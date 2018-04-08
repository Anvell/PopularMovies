package io.github.anvell.popularmovies.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.reactivex.Observable;

import io.github.anvell.popularmovies.web.MovieDetails;
import io.reactivex.Single;

public class MoviesProviderClient {

    public static Observable<MovieDetails> getMovies(@NonNull ContentResolver resolver) {
        return Observable.create(emitter -> {
            Cursor cursor = null;

            try {
                cursor = resolver.query(MoviesContract.MovieEntry.CONTENT_URI,
                                        null, null, null, null);

                if(cursor != null && cursor.getCount() > 0) {
                    while(cursor.moveToNext()) {
                        emitter.onNext(parseDetails(cursor));
                    }
                }
                emitter.onComplete();

            } catch (Exception ex) {
                emitter.onError(ex);
            } finally {
                closeCursor(cursor);
            }

        });
    }

    public static Single<MovieDetails> getMovie(@NonNull ContentResolver resolver, int movieId) {
        return Single.create(emitter -> {
            Cursor cursor = null;

            try {
                cursor = resolver.query(MoviesContract.MovieEntry.buildMovieUri(movieId),
                                        null, null, null, null);

                if(cursor != null && cursor.getCount() > 0 && cursor.moveToNext()) {
                    emitter.onSuccess(parseDetails(cursor));
                } else
                    throw new Exception("Unable to find movie by ID");
            } catch (Exception ex) {
                emitter.onError(ex);
            } finally {
                closeCursor(cursor);
            }
        });
    }

    public static Single<Uri> addMovie(@NonNull ContentResolver resolver,
                                                @NonNull MovieDetails movieDetails) {
        return Single.create(emitter -> {

            ContentValues values = new ContentValues();
            Uri uri = MoviesContract.MovieEntry.CONTENT_URI;

            try {
                values.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, movieDetails.id);
                values.put(MoviesContract.MovieEntry.COLUMN_TITLE, movieDetails.title);
                values.put(MoviesContract.MovieEntry.COLUMN_DETAILS,
                           new Gson().toJson(movieDetails, MovieDetails.class));

                Uri finalUri = resolver.insert(uri, values);

                if (finalUri != null)
                    emitter.onSuccess(finalUri);
                else
                    throw new Exception("Unable store movie in provider");
            } catch (Exception ex) {
                emitter.onError(ex);
            }
        });
    }

    public static Single<Integer> deleteMovie(@NonNull ContentResolver resolver, int movieId) {
        return Single.create(emitter -> {

            try {
                Integer rows = resolver.delete(MoviesContract.MovieEntry.buildMovieUri(movieId),
                                               null, null);

                if(rows > 0) {
                    emitter.onSuccess(rows);
                } else
                    throw new Exception("Record not found");
            } catch (Exception ex) {
                emitter.onError(ex);
            }
        });
    }

    @NonNull
    private static MovieDetails parseDetails(Cursor cursor) {
        MovieDetails details = new MovieDetails();
        if(cursor != null) {
            int col = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_DETAILS);
            details = new Gson().fromJson(cursor.getString(col), MovieDetails.class);
        }
        return details;
    }

    private static void closeCursor(Cursor cursor) {
        if(cursor != null) cursor.close();
    }
}

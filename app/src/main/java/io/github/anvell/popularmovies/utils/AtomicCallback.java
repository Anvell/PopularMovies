package io.github.anvell.popularmovies.utils;

import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AtomicCallback<T> implements Callback<T> {

    private final AtomicReference<T> data;
    private final Runnable onSuccess;
    private final Runnable onFailure;

    public AtomicCallback(AtomicReference<T> data, Runnable onSuccess, Runnable onFailure) {
        this.data = data;
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful() && response.body() != null) {
            data.set(response.body());
            onSuccess.run();
        } else {
            onFailure.run();
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFailure.run();
    }
}

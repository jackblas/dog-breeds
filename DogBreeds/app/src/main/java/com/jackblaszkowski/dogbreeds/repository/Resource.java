package com.jackblaszkowski.dogbreeds.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Resource<T> {
    @NonNull
    public final Status status;
    @Nullable
    public final T data;
    @Nullable public final ErrorType errorType;

    private Resource(@NonNull Status status, @Nullable T data,
                     @Nullable ErrorType errorType) {
        this.status = status;
        this.data = data;
        this.errorType = errorType;
    }

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(ErrorType error, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, error);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

    public enum Status { SUCCESS, ERROR, LOADING }

    public enum ErrorType { NO_CONNECTION, SERVER_ERROR, NOT_FOUND }

}
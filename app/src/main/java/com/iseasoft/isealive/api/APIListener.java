package com.iseasoft.isealive.api;

public interface APIListener<T> {
    void onRequestCompleted(T obj, String json);

    void onError(Error e);
}

package com.iseasoft.iseafootball.api;

public interface APIListener<T> {
    void onRequestCompleted(T obj, String json);

    void onError(Error e);
}

package com.iseasoft.iseagoal.api;

public interface APIListener<T> {
    void onRequestCompleted(T obj, String json);

    void onError(Error e);
}

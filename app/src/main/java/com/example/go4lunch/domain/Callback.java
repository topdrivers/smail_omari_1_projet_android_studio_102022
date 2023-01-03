package com.example.go4lunch.domain;


    public interface Callback<Result> {
        void onSuccess(Result result);
        void onFailure();
    }


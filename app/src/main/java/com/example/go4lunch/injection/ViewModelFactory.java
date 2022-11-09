package com.example.go4lunch.injection;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.example.go4lunch.Repository.RetrofitRepository;
import com.example.go4lunch.ViewModel.RetrofitViewModel;

import java.util.concurrent.Executor;

/**
 * Created by Philippe on 27/02/2018.
 */


public class ViewModelFactory implements ViewModelProvider.Factory {

    private final RetrofitRepository retrofitDataSource;

    public ViewModelFactory(RetrofitRepository retrofitDataSource) {
        this.retrofitDataSource = retrofitDataSource;

    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //return ViewModelProvider.Factory.super.create(modelClass);

        if (modelClass.isAssignableFrom(RetrofitViewModel.class)) {
            return (T) new RetrofitViewModel(retrofitDataSource);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");


    }


}


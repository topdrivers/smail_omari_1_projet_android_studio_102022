package com.example.go4lunch.injection;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.repository.RetrofitRepository;
import com.example.go4lunch.viewModel.RetrofitViewModel;


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


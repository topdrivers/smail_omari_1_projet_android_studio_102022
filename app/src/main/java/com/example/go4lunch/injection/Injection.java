package com.example.go4lunch.injection;

import android.content.Context;


import com.example.go4lunch.dataSource.remoteData.PlaceService;
import com.example.go4lunch.repository.RetrofitRepository;
import com.example.go4lunch.repository.UserRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class Injection {

    public static RetrofitRepository provideRetrofitDataSource(Context context) {
        return new RetrofitRepository(PlaceService.retrofit.create(PlaceService.class));
    }

    public static Executor provideExecutor(){ return Executors.newSingleThreadExecutor(); }

    public static ViewModelFactory provideViewModelFactory(Context context) {
        RetrofitRepository dataSourceItem = provideRetrofitDataSource(context);
        return new ViewModelFactory(dataSourceItem);
    }

    public static UserViewModelFactory provideUserViewModelFactory(Context context) {
        UserRepository userDataSourceItem = provideUserDataSource(context);
        return new UserViewModelFactory(userDataSourceItem);
    }

    private static UserRepository provideUserDataSource(Context context) {
        return new UserRepository();
    }


}

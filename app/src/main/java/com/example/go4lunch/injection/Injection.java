package com.example.go4lunch.injection;

import android.content.Context;


import com.example.go4lunch.dataSource.remoteData.PlaceService;
import com.example.go4lunch.repository.RetrofitRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Philippe on 27/02/2018.
 */

public class Injection {

    public static RetrofitRepository provideRetrofitDataSource(Context context) {
        //TodocDatabase database = TodocDatabase.getInstance(context);
        return new RetrofitRepository(PlaceService.retrofit.create(PlaceService.class));
    }





    public static Executor provideExecutor(){ return Executors.newSingleThreadExecutor(); }

    public static ViewModelFactory provideViewModelFactory(Context context) {
        RetrofitRepository dataSourceItem = provideRetrofitDataSource(context);
        System.out.println("-----------viewomdelfactory-------"+new ViewModelFactory(dataSourceItem));
        return new ViewModelFactory(dataSourceItem);
    }



}

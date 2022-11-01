package com.example.go4lunch.Utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.Models.RestaurantPlace;
import com.example.go4lunch.Models.Result;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GithubStreams {
    public static  PlaceService placeService;
/*
    public GithubStreams(PlaceService placeService) {
        this.placeService = placeService;
    }

 */

    public static Observable<RestaurantPlace> getPlaceResultsLiveData(String location) {
       // PlaceService gitHubService = PlaceService.retrofit.create(PlaceService.class);
        placeService = PlaceService.retrofit.create(PlaceService.class);
        System.out.println("---------------------getplaceresults---------------");
        Observable<RestaurantPlace> placeResultsCall = placeService
                .getNearby(location, 6500, "restaurant", /*BuildConfig.MAPS_API_KEY*/"AIzaSyDBrw5T0cNzqnSGQW6vA_QADtMBa1t-sR8")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);

        System.out.println("---------------------getplaceresultstostring---------------"+placeResultsCall.toString());
        return placeResultsCall;
    }

}


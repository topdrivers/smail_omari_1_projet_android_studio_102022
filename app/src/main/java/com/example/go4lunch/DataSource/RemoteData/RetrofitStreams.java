package com.example.go4lunch.DataSource.RemoteData;

import com.example.go4lunch.DataSource.Models.RestaurantPlace;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class RetrofitStreams {
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


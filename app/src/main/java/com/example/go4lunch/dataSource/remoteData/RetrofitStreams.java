package com.example.go4lunch.dataSource.remoteData;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.dataSource.models.RestaurantPlace;
import com.example.go4lunch.dataSource.models.Result;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RetrofitStreams implements LifecycleOwner {
    public static  PlaceService placeService;
    public MutableLiveData<RestaurantPlace> restaurantPlaceMutableLiveData= new MutableLiveData<>();

    public RetrofitStreams(PlaceService placeService) {
        this.placeService = placeService;
    }



    public     LiveData<RestaurantPlace> getPlaceResultsLiveData(String location) {
       // PlaceService gitHubService = PlaceService.retrofit.create(PlaceService.class);
        placeService = PlaceService.retrofit.create(PlaceService.class);
        System.out.println("---------------------getplaceresults---------------");


        Call<RestaurantPlace> restaurantPlaceCall = placeService.getNearby("48.550720,7.763412", 1500, "restaurant", /*BuildConfig.MAPS_API_KEY*/"AIzaSyDBrw5T0cNzqnSGQW6vA_QADtMBa1t-sR8");

        restaurantPlaceCall.enqueue(new Callback<RestaurantPlace>() {
            @Override
            public void onResponse(Call<RestaurantPlace> call, Response<RestaurantPlace> response) {
                restaurantPlaceMutableLiveData.setValue(response.body()); ;
            }

            @Override
            public void onFailure(Call<RestaurantPlace> call, Throwable t) {

            }
        });

        return  restaurantPlaceMutableLiveData;

    }

    private static void populateList(List<Result> results) {
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }
}


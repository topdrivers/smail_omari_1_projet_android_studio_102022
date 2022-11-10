package com.example.go4lunch.Repository;


import static com.example.go4lunch.DataSource.RemoteData.RetrofitStreams.placeService;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.go4lunch.DataSource.Models.RestaurantPlace;
import com.example.go4lunch.DataSource.RemoteData.PlaceService;
import com.example.go4lunch.DataSource.RemoteData.RetrofitStreams;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.observers.DisposableObserver;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class RetrofitRepository {
    //private final PlaceService placeService;
    private DisposableObserver<RestaurantPlace> disposable;


    public RetrofitRepository(PlaceService placeService) {
        /*this.placeService = placeService;*/
    }

    // --- GET ---
    public LiveData<RestaurantPlace> getResults(){

        System.out.println("-------------1fois------------------");
        // 1.2 - Execute the stream subscribing to Observable defined inside GithubStream

        RetrofitStreams retrofitStreams = new RetrofitStreams(PlaceService.retrofit.create(PlaceService.class));
        /*
        LiveData<RestaurantPlace> restaurantPlaceLiveData = retrofitStreams.getPlaceResultsLiveData("48.550720,7.763412");
        restaurantPlaceLiveData = placeService
                .getNearby("48.550720,7.763412", 6500, "restaurant", "AIzaSyDBrw5T0cNzqnSGQW6vA_QADtMBa1t-sR8")

        ;

         */

        return retrofitStreams.getPlaceResultsLiveData("48.550720,7.763412");



        //return RetrofitStreams.getPlaceResultsLiveData("48.550720,7.763412").getValue();
        //return disposable;

/*
        return new LiveData<RestaurantPlace>() {
            @Override
            public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super RestaurantPlace> observer) {
                super.observe(owner, observer);
            }
        };

 */

    }

}

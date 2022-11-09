package com.example.go4lunch.Repository;


import android.util.Log;

import com.example.go4lunch.DataSource.Models.RestaurantPlace;
import com.example.go4lunch.DataSource.RemoteData.RetrofitStreams;
import com.example.go4lunch.DataSource.RemoteData.PlaceService;

import io.reactivex.observers.DisposableObserver;
import retrofit2.Retrofit;

public class RetrofitRepository {
    //private final PlaceService placeService;
    private DisposableObserver<RestaurantPlace> disposable;


    public RetrofitRepository(PlaceService placeService) {
        /*this.placeService = placeService;*/
    }

    // --- GET ---
    public DisposableObserver<RestaurantPlace> getResults(){

        System.out.println("-------------1fois------------------");
        // 1.2 - Execute the stream subscribing to Observable defined inside GithubStream
        return RetrofitStreams.getPlaceResultsLiveData("48.550720,7.763412").subscribeWith(new DisposableObserver<RestaurantPlace>() {
            @Override
            public void onNext(RestaurantPlace restaurantPlace) {
                Log.e("TAG","On Next");
                // 1.3 - Update UI with list of users
                System.out.println("--------------esult"+restaurantPlace.getResults().get(1).getName());
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAG","On Error"+Log.getStackTraceString(e));
            }

            @Override
            public void onComplete() {
                Log.e("TAG","On Complete !!");
            }
        });
        //return disposable;

    }

}

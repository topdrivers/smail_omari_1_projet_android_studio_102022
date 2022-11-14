package com.example.go4lunch.repository;


import androidx.lifecycle.LiveData;

import com.example.go4lunch.dataSource.models.RestaurantPlace;
import com.example.go4lunch.dataSource.remoteData.PlaceService;
import com.example.go4lunch.dataSource.remoteData.RetrofitStreams;

import io.reactivex.observers.DisposableObserver;

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

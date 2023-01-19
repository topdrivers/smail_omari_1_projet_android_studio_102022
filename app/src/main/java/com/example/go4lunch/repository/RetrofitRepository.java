package com.example.go4lunch.repository;




import static com.example.go4lunch.dataSource.remoteData.RetrofitStreams.streamFetchAutoCompleteInformations;

import androidx.lifecycle.LiveData;

import com.example.go4lunch.Utils.AutocompleteApi.AutocompleteResult;
import com.example.go4lunch.dataSource.models.PlaceDetails.Result;
import com.example.go4lunch.dataSource.models.RestaurantPlace;
import com.example.go4lunch.dataSource.remoteData.PlaceService;
import com.example.go4lunch.dataSource.remoteData.RetrofitStreams;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

public class RetrofitRepository {
    private DisposableObserver<RestaurantPlace> disposable;


    public RetrofitRepository(PlaceService placeService) {

    }

    // --- GET ---
    public LiveData<RestaurantPlace> getResults(){

        RetrofitStreams retrofitStreams = new RetrofitStreams(PlaceService.retrofit.create(PlaceService.class));
        return retrofitStreams.getPlaceResultsLiveData("48.550720,7.763412");
    }

    public Observable<List<Result>> getAutoCompleteResults(String location, String autocompleteString){
        RetrofitStreams retrofitStreams = new RetrofitStreams(PlaceService.retrofit.create(PlaceService.class));
        return streamFetchAutoCompleteInformations(autocompleteString, location, 6500, "");
    }

    public Observable<AutocompleteResult> streamFetchAutoComplete(String location, String query) {
        return RetrofitStreams.streamFetchAutoComplete(query,location, 6500, "");
    }
}

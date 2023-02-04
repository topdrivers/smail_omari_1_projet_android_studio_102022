package com.example.go4lunch.viewModel;


import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.Utils.AutocompleteApi.AutocompleteResult;
import com.example.go4lunch.dataSource.models.PlaceDetails.Result;
import com.example.go4lunch.repository.RetrofitRepository;

import com.example.go4lunch.dataSource.models.RestaurantPlace;

import java.util.List;
import io.reactivex.Observable;

public class RetrofitViewModel extends ViewModel {
    // REPOSITORIES
    private final RetrofitRepository retrofitDataSource;


    // DATA
    @Nullable
    private LiveData<RestaurantPlace> results;

    public RetrofitViewModel(RetrofitRepository retrofitDataSource) {
        this.retrofitDataSource = retrofitDataSource;
    }

    public void init(Double latitude, Double longitude) {
        if (this.results != null) {
            return;
        }
        results = retrofitDataSource.getResults(latitude,longitude);
    }

    // -------------
    // FOR Results
    // -------------
    public LiveData<RestaurantPlace> getResults(Double latitude, Double longitude) { return retrofitDataSource.getResults( latitude,  longitude);  }

    public Observable<List<Result>> getAutoCompletePlaceResults(String location, String autocompleteString){
        return retrofitDataSource.getAutoCompleteResults(location, autocompleteString);
    }

    public Observable<AutocompleteResult> streamFetchAutoComplete(String location, String query) {
        return retrofitDataSource.streamFetchAutoComplete(location, query);
    }
}

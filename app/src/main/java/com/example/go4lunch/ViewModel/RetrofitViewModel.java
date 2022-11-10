package com.example.go4lunch.ViewModel;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.Repository.RetrofitRepository;

import com.example.go4lunch.DataSource.Models.RestaurantPlace;

public class RetrofitViewModel extends ViewModel {
    // REPOSITORIES
    private final RetrofitRepository retrofitDataSource;


    // DATA
    @Nullable
    private LiveData<RestaurantPlace> results;

    public RetrofitViewModel(RetrofitRepository retrofitDataSource) {
        this.retrofitDataSource = retrofitDataSource;
    }

    public void init() {
        if (this.results != null) {
            return;
        }
        results = retrofitDataSource.getResults();
    }

    // -------------
    // FOR Results
    // -------------

    public LiveData<RestaurantPlace> getResults() { return retrofitDataSource.getResults();  }


}

package com.example.go4lunch.ViewModel;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.Repository.RetrofitRepository;

import java.util.concurrent.Executor;
import com.example.go4lunch.DataSource.Models.RestaurantPlace;

import io.reactivex.observers.DisposableObserver;

public class RetrofitViewModel extends ViewModel {
    // REPOSITORIES
    private final RetrofitRepository retrofitDataSource;


    // DATA
    @Nullable
    private  DisposableObserver<RestaurantPlace> results;

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

    public DisposableObserver<RestaurantPlace> getResults() { return retrofitDataSource.getResults();  }


}

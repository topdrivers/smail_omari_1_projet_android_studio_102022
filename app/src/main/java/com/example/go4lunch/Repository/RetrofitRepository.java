package com.example.go4lunch.Repository;


import com.example.go4lunch.Utils.PlaceService;

public class RetrofitRepository {
    private final PlaceService placeService;

    public RetrofitRepository(PlaceService placeService) {
        this.placeService = placeService;
    }


}

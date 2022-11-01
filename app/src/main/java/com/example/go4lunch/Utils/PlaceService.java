package com.example.go4lunch.Utils;

import com.example.go4lunch.Models.RestaurantPlace;
import com.example.go4lunch.Models.Result;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PlaceService {

    @GET("nearbysearch/json?")
    Observable <RestaurantPlace> getNearby(@Query("location") String location,
                                        @Query("radius") int radius,
                                        @Query("type") String type,
                                        @Query("key") String key);
/*
    @GET("nearbysearch/json")
    List<Result> getList(@Query("results") String results);

    @GET("details/json")
    Call<PlaceDetails> getDetailsPlaces(@Query("place_id") String placeId,
                                        @Query("fields") String fields,
                                        @Query("key") String key);

 */

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}

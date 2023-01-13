package com.example.go4lunch.dataSource.remoteData;


import com.example.go4lunch.Utils.AutocompleteApi.AutocompleteResult;
import com.example.go4lunch.dataSource.models.PlaceDetails.PlaceDetailsInfo;
import com.example.go4lunch.dataSource.models.RestaurantPlace;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaceService {

    @GET("nearbysearch/json?")
    Call<RestaurantPlace> getNearby(@Query("location") String location,
                                    @Query("radius") int radius,
                                    @Query("type") String type,
                                    @Query("key") String key);

    //Autocomplete API Request
    @GET("autocomplete/json?strictbounds&key=AIzaSyDBrw5T0cNzqnSGQW6vA_QADtMBa1t-sR8")
    Observable<AutocompleteResult> getAutocomplete(@Query("input") String input, @Query("radius") int radius, @Query("location") String location, @Query("type") String type);

    //PlaceDetails API Request
    @GET("details/json?key=AIzaSyDBrw5T0cNzqnSGQW6vA_QADtMBa1t-sR8")
    Observable<PlaceDetailsInfo> getDetails(@Query("place_id") String placeId);

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

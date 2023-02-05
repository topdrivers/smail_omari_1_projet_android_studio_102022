package com.example.go4lunch.dataSource.remoteData;





import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.Utils.AutocompleteApi.AutocompleteResult;
import com.example.go4lunch.Utils.AutocompleteApi.Prediction;
import com.example.go4lunch.dataSource.models.PlaceDetails.PlaceDetailsInfo;
import com.example.go4lunch.dataSource.models.RestaurantPlace;
import com.example.go4lunch.dataSource.models.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RetrofitStreams implements LifecycleOwner {
    public static  PlaceService placeService;
    public static MutableLiveData<RestaurantPlace> restaurantPlaceMutableLiveData= new MutableLiveData<>();
    public MutableLiveData<RestaurantPlace> autoCompleteRestaurantPlaceMutable= new MutableLiveData<>();
    public static int radius = 1500;
    public static int zoom = 10;

    public RetrofitStreams(PlaceService placeService) {
        this.placeService = placeService;
    }

    public   int getRadius() {
        return radius;
    }

    public static void setRadius(int radius) {
        radius = radius;
    }

    public static LiveData<RestaurantPlace> getPlaceResultsLiveData(String location) {
       // PlaceService gitHubService = PlaceService.retrofit.create(PlaceService.class);
        placeService = PlaceService.retrofit.create(PlaceService.class);
        System.out.println("---------------------getplaceresults---------------");


        Call<RestaurantPlace> restaurantPlaceCall = placeService.getNearby(location, radius, "restaurant", /*BuildConfig.MAPS_API_KEY*/"AIzaSyDBrw5T0cNzqnSGQW6vA_QADtMBa1t-sR8");
        System.out.println("------------location----------"+location);
        restaurantPlaceCall.enqueue(new Callback<RestaurantPlace>() {
            @Override
            public void onResponse(Call<RestaurantPlace> call, Response<RestaurantPlace> response) {
                restaurantPlaceMutableLiveData.setValue(response.body()); ;
            }

            @Override
            public void onFailure(Call<RestaurantPlace> call, Throwable t) {

            }
        });

        return  restaurantPlaceMutableLiveData;

    }

    public static Observable<AutocompleteResult> getAutoCompletePlaceResults(String location, String autocompleteString) {
        // PlaceService gitHubService = PlaceService.retrofit.create(PlaceService.class);
        placeService = PlaceService.retrofit.create(PlaceService.class);
        System.out.println("---------------------autocomplete ---------------"+autocompleteString);

//        Call<RestaurantPlace> autocompleteRestaurantPlace = placeService.getAutocomplete(autocompleteString, 1500, "48.550720,7.763412", "restaurant" );
        //PlaceService autocompleteRestaurantPlace = placeService.getAutocomplete(autocompleteString, 1500, "48.550720,7.763412", "restaurant" );

        return placeService.getAutocomplete(autocompleteString, radius, "48.550720,7.763412", "restaurant")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);

/*
        autocompleteRestaurantPlace.enqueue(new Callback<RestaurantPlace>() {
            @Override
            public void onResponse(Call<RestaurantPlace> call, Response<RestaurantPlace> response) {
                autoCompleteRestaurantPlaceMutable.setValue(response.body()); ;
            }

            @Override
            public void onFailure(Call<RestaurantPlace> call, Throwable t) {

            }
        });

 */

        //return  autoCompleteRestaurantPlaceMutable;

    }

/*
    public static Observable<RestaurantPlace> stremFecthDetails(String placeId) {
        // PlaceService gitHubService = PlaceService.retrofit.create(PlaceService.class);
        placeService = PlaceService.retrofit.create(PlaceService.class);

        return placeService.getDetails(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);

    }

 */

    //For autocomplete 2 chained request

    public static Observable<RestaurantPlace> streamFetchAutocompleteInfos(String input, int radius, String location, String type) {
        return getAutoCompletePlaceResults(location, input)
                .flatMapIterable(new Function<AutocompleteResult, List<Prediction>>() {
                    List<Prediction> food = new ArrayList<>();

                    @Override
                    public List<Prediction> apply(AutocompleteResult autocompleteResult) throws Exception {

                        for (Prediction prediction : autocompleteResult.getPredictions()) {
                            if (prediction.getTypes().contains("restaurant")) {

                                food.add(prediction);
                            }
                        }
                        return food;
                    }
                })

                .flatMap(new Function<Prediction, ObservableSource<RestaurantPlace>>() {
                    @Override
                    public ObservableSource<RestaurantPlace> apply(Prediction prediction) throws Exception {
                        //return stremFecthDetails(prediction.getPlaceId());
                        System.out.println("------------getPlaceResultsLiveData--------"+(ObservableSource<RestaurantPlace>) getPlaceResultsLiveData("48.550720,7.763412"));
                        return (ObservableSource<RestaurantPlace>) getPlaceResultsLiveData("48.550720,7.763412");
                    }
                })



                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<AutocompleteResult> streamFetchAutoComplete(String query, String location, int radius, String apiKey){
        System.out.println("------------------arrivé---------------"+placeService.getAutocomplete(query, radius, location, "restaurant"));
        placeService = PlaceService.retrofit.create(PlaceService.class);
        return placeService.getAutocomplete(query, radius, location, "restaurant")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10,TimeUnit.SECONDS);
    }



    public static  Observable<List<com.example.go4lunch.dataSource.models.PlaceDetails.Result>> streamFetchAutoCompleteInformations(String query, String location, int radius, String apiKey){
        return placeService.getAutocomplete(query, radius, location, "restaurant")
                .flatMapIterable(AutocompleteResult::getPredictions)
                .flatMap(info -> placeService.getDetails(info.getPlaceId()))
                .map(PlaceDetailsInfo::getResult)
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    private static void populateList(List<Result> results) {
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }
}


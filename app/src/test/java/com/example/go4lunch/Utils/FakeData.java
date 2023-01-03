package com.example.go4lunch.Utils;

import android.gesture.Prediction;

import com.example.go4lunch.dataSource.models.RestaurantPlace;
import com.example.go4lunch.domain.Callback;
import com.example.go4lunch.model.User;

import java.util.Arrays;

public class FakeData {
    Callback<Place[]> fakePlaceCallback =
            new Callback<Place[]>() {
                @Override
                public void onSuccess(Place[] places) {}

                @Override
                public void onFailure() {}
            };

    Callback<Place> fakeSinglePlaceCallback =
            new Callback<Place>() {
                @Override
                public void onSuccess(Place places) {}

                @Override
                public void onFailure() {}
            };

    Callback<User> fakeSingleUserCallback =
            new Callback<User>() {
                @Override
                public void onSuccess(User places) {}

                @Override
                public void onFailure() {}
            };

    Callback<User[]> fakeUserCallback =
            new Callback<User[]>() {
                @Override
                public void onSuccess(User[] places) {}

                @Override
                public void onFailure() {}
            };

    Callback<Prediction[]> fakePredictionCallback =
            new Callback<Prediction[]>() {
                @Override
                public void onSuccess(Prediction[] places) {}

                @Override
                public void onFailure() {}
            };

    public static User fakeCurrentUser =
            /*
            new User(
                    "id",
                    "username",
                    "url.photo/",
                    "@mail",
                    "workplaceId",
                    "rstId",
                    "rstNm",
                    Arrays.asList("one", "two"),
                    Arrays.asList("three", "next..."));

             */
            new User("133", "name", "userEmail", "urlPicture");



    Place.LangCode fakeLangCode = Place.LangCode.en;

    String fakePlaceId = "id";

    GeoCoordinates fakeGeoCoordinates = new GeoCoordinates(0., 0.);

    String fakeAutocompleteInput = "fake_input";

    Place.Type[] fakeFilter = new Place.Type[] {Place.Type.RESTAURANT};
}

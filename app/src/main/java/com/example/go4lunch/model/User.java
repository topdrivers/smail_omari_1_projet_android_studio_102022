package com.example.go4lunch.model;

import androidx.annotation.Nullable;

import java.util.List;

public class User {

    private String uid;
    private String username;
    private String userEmail;
    @Nullable
    private String urlPicture;
    @Nullable
    List<String> favouriteRestaurants;
    @Nullable
    String restaurantChoice;

    public User() {
    }

    public User(String userId, String userName, String userEmail, @Nullable String userPicture) {
        this.uid = userId;
        this.username = userName;
        this.userEmail = userEmail;
        this.urlPicture = userPicture;
        this.favouriteRestaurants = null;
        this.restaurantChoice = null;
    }

    public User(String userId, String userName, String userEmail, @Nullable String userPicture, @Nullable String restaurantChoice) {
        this.uid = userId;
        this.username = userName;
        this.userEmail = userEmail;
        this.urlPicture = userPicture;
        this.favouriteRestaurants = null;
        this.restaurantChoice = restaurantChoice;
    }

    // GETTERS

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public String getUserEmail() {
        return userEmail;
    }

    @Nullable
    public List<String> getFavouriteRestaurants() {
        return favouriteRestaurants;
    }

    @Nullable
    public String getRestaurantChoice(){
        return restaurantChoice;
    }

    public void setFavouriteRestaurants(@Nullable List<String> favouriteRestaurants) {
        this.favouriteRestaurants = favouriteRestaurants;
    }

    public void setRestaurantChoice(@Nullable String restaurantChoice) {
        this.restaurantChoice = restaurantChoice;
    }
}

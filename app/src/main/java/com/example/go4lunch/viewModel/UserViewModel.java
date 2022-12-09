package com.example.go4lunch.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.UserRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class UserViewModel extends ViewModel {

    private final UserRepository userRepository;

    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserRepository getInstance() {
        return userRepository.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public LiveData<User> observeCurrentUser() {
        return userRepository.observeCurrentUser();
    }

    public Boolean isCurrentUserNotLoggedIn() {
        return (this.getCurrentUser() == null);
    }

    public void getCurrentUserDataFromFireStore(String userID) {
        userRepository.getUserDataFromDataBase(userID);
    }

    public void getDataBaseInstanceUser() {
        userRepository.getDataBaseInstance();
    }

    public LiveData<List<User>> getUserList(){
        System.out.println("---------user list viewmodel------"+userRepository.getUserList());
        return userRepository.getUserList();
    }

    public void addRestaurantToFavourite(String restaurantID) {
        userRepository.addFavouriteRestaurant(userRepository.getCurrentUserID(), restaurantID);
    }

    public void removeRestaurantFromFavourite(String restaurantID) {
        userRepository.removeFavouriteRestaurant(userRepository.getCurrentUserID(), restaurantID);
    }

    public void validateRestaurantChoice(String restaurantID) {
        userRepository.validateRestaurantChoice(userRepository.getCurrentUserID(), restaurantID);
    }

    public void cancelRestaurantChoice(String restaurantID) {
        userRepository.cancelRestaurantChoice(userRepository.getCurrentUserID(), restaurantID);
    }

    public void getAllUsersFromDataBase(){
        userRepository.getAllUsersFromDataBase();
    }

    public LiveData<List<User>> getAllUsers(){
        return userRepository.getAllUsers();
    }
}

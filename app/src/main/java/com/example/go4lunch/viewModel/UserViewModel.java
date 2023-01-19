package com.example.go4lunch.viewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.domain.Callback;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class UserViewModel extends ViewModel {

    private static volatile UserViewModel instance;
    private final UserRepository userRepository;

    private UserViewModel() {
        userRepository = UserRepository.getInstance();
    }

    public static UserViewModel getInstance() {
        UserViewModel result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class) {
            if (instance == null) {
                instance = new UserViewModel();
            }
            return instance;
        }
    }

    //private final UserRepository userRepository;

    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public FirebaseUser getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public LiveData<User> observeCurrentUser() {
        return userRepository.observeCurrentUser();
    }

    public Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() != null);
    }

    public Task<Void> signOut(Context context){
        return userRepository.signOut(context);
    }

    public void getCurrentUserDataFromFireStore(String userID) {
        userRepository.getUserDataFromDataBase(userID);
    }

    public void getDataBaseInstanceUser() {
        userRepository.getDataBaseInstance();
    }

    public LiveData<List<User>> getUserList(){
        return userRepository.getUserList();
    }

    public LiveData<List<User>> getUserDetailsRestaurantList(String restaurantId) {
        return userRepository.getUserDetailsRestaurantList(restaurantId);
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

    public void getUsersByChosenRestaurant(
            String restaurantId, String usersWorkplaceId, Callback<List<User>> callback) {
                userRepository.getUsersByChosenRestaurant(restaurantId,usersWorkplaceId,callback);
    }
}

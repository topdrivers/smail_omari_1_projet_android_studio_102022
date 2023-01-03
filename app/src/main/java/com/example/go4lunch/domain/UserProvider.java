package com.example.go4lunch.domain;

import com.example.go4lunch.model.User;

public interface UserProvider {

    String CHOSEN_RESTAURANT_ID = "chosenRestaurant";
    String CHOSEN_RESTAURANT_NAME = "chosenRestaurantName";
    String WORKPLACE = "workplaceId";
    String CONVERSATIONS = "conversations";
    String LIKED_RESTAURANTS = "likedRestaurants";
    String DEFAULT_USER_PHOTO_URL = "https://as2.ftcdn.net/v2/jpg/04/10/43/77/1000_F_410437733_hdq4Q3QOH9uwh0mcqAhRFzOKfrCR24Ta.jpg";

    void getUserById(String userId, Callback<User> callback);
    void getUsersByWorkplace( Callback<User[]> callback);
    void getUsersByChosenRestaurant(String restaurantId, Callback<User[]> callback);
    void pushCurrentUserData();
    void resetCurrentUserChosenRestaurant();
    void updateUserData(String dataType, Object data);
    boolean isCurrentUserNew();
    void logout(Runnable onLogout);
    void deleteCurrentUserAccount(Callback<Boolean> callback);
    void addUserLoginCompleteListener(OnUserLoginComplete listener);
    User getCurrentUser();

    interface OnUserLoginComplete {
        void onComplete(User currentUser);
    }
}

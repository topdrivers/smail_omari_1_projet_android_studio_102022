package com.example.go4lunch.dataSource.remoteData;


import android.net.Uri;

import com.example.go4lunch.domain.Callback;
import com.example.go4lunch.domain.RestaurantSpecificDataProvider;
import com.example.go4lunch.domain.UserProvider;
import com.example.go4lunch.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FirebaseServicesClient  implements UserProvider, RestaurantSpecificDataProvider {
    private final FirebaseFirestore firestoreDb;
    private final FirebaseAuth firebaseAuth;
    private User currentUser;

    private final Set<UserProvider.OnUserLoginComplete> loginCompleteListeners = new HashSet<>();
    private boolean userCompletelySignedIn = false;

    public FirebaseServicesClient(FirebaseFirestore firestoreDb, FirebaseAuth firebaseAuth) {
        this.firestoreDb = firestoreDb;
        this.firebaseAuth = firebaseAuth;
        setCurrentUserFromFirebaseUser(firebaseAuth);
        firebaseAuth.addAuthStateListener(this::setCurrentUserFromFirebaseUser);
    }

    private void setCurrentUserFromFirebaseUser(FirebaseAuth firebaseAuth) {
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            final Uri userPhotoUrl = firebaseUser.getPhotoUrl();
            currentUser =
                    new User(
                            firebaseUser.getUid(),
                            firebaseUser.getDisplayName(),
                            firebaseUser.getEmail(),
                            userPhotoUrl != null ? userPhotoUrl.toString() : DEFAULT_USER_PHOTO_URL);
            fetchCurrentUserAdditionalData();
        } else currentUser = null;
    }

    private void fetchCurrentUserAdditionalData() {
        firestoreDb
                .collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(this::onUserAdditionalDataFetched);
    }

    @SuppressWarnings("unchecked")
    private void onUserAdditionalDataFetched(DocumentSnapshot userDocument) {
        currentUser.setRestaurantChoice(userDocument.getString("restauranChoice"));
        currentUser.setFavouriteRestaurants((List<String>) userDocument.get("favouriteRestaurants"));

        if (loginCompleteListeners.isEmpty()) {
            userCompletelySignedIn = true;
        } else{
            for (OnUserLoginComplete listener: loginCompleteListeners){
                listener.onComplete(currentUser);
            }
            loginCompleteListeners.clear();
        }
    }



    @SuppressWarnings("unchecked")
    private User[] usersDocumentsToArray(List<DocumentSnapshot> usersDocuments) {
        final List<User> userList = new ArrayList<>();
        for (DocumentSnapshot userDoc : usersDocuments) {
            userList.add(
                    new User(
                            userDoc.getId(),
                            userDoc.getString("username"),
                            userDoc.getString("userEmail"),
                            userDoc.getString("urlPicture"),
                            userDoc.getString("restaurantChoice")));
        }
        return userList.toArray(new User[0]);
    }


    @Override
    public void getUserById(String userId, Callback<User> callback) {

    }

    @Override
    public void getUsersByWorkplace(Callback<User[]> callback) {

    }

    @Override
    public void getUsersByChosenRestaurant(String restaurantId, Callback<User[]> callback) {

    }

    @Override
    public void pushCurrentUserData() {
        final Map<String, Object> userData = new HashMap<>();
        userData.put(CHOSEN_RESTAURANT_ID, currentUser.getRestaurantChoice());
        userData.put(LIKED_RESTAURANTS, currentUser.getFavouriteRestaurants());
        firestoreDb.collection("users").document(currentUser.getUid()).update(userData);
    }

    @Override
    public void resetCurrentUserChosenRestaurant() {

    }

    @Override
    public void updateUserData(String dataType, Object data) {

    }

    @Override
    public boolean isCurrentUserNew() {
        final FirebaseUserMetadata userMetadata = firebaseAuth.getCurrentUser().getMetadata();
        return userMetadata.getCreationTimestamp() != userMetadata.getLastSignInTimestamp();
    }

    @Override
    public void logout(Runnable onLogout) {
        firebaseAuth.signOut();
        onLogout.run();
    }

    @Override
    public void deleteCurrentUserAccount(Callback<Boolean> callback) {
        firebaseAuth
                .getCurrentUser()
                .delete()
                .addOnSuccessListener(__ -> callback.onSuccess(true))
                .addOnFailureListener(__ -> callback.onFailure());
    }

    @Override
    public void addUserLoginCompleteListener(OnUserLoginComplete listener) {
        if (userCompletelySignedIn) {
            listener.onComplete(currentUser);
        } else loginCompleteListeners.add(listener);
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void getRestaurantClientCountByWorkplace(
            String workplaceId, Callback<Map<String, Integer>> callback) {
        firestoreDb
                .collection("users")
                .whereEqualTo(WORKPLACE, workplaceId)
                .get()
                .addOnSuccessListener(
                        usersList -> {
                            final Map<String, Integer> employeesCountByRestaurant = new HashMap<>();
                            String userChosenRestaurantId;
                            Integer employeesCount;
                            for (DocumentSnapshot userDoc : usersList.getDocuments()) {
                                userChosenRestaurantId = userDoc.getString(CHOSEN_RESTAURANT_ID);
                                employeesCount = employeesCountByRestaurant.get(userChosenRestaurantId);
                                if (employeesCount == null) {
                                    employeesCountByRestaurant.put(userChosenRestaurantId, 1);
                                } else employeesCountByRestaurant.put(userChosenRestaurantId, ++employeesCount);
                            }
                            callback.onSuccess(employeesCountByRestaurant);
                        });
    }

}

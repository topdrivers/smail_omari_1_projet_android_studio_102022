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
        /*
        currentUser.setWorkplaceId(userDocument.getString(WORKPLACE));
        currentUser.setChosenRestaurantName(userDocument.getString(CHOSEN_RESTAURANT_NAME));
        currentUser.setConversationsIds((List<String>) userDocument.get(CONVERSATIONS));

         */


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



    @Override
    public void getUserById(String userId, Callback<User> callback) {}


    @Override
    public void getUsersByWorkplace( Callback<User[]> callback) {
//    Log.i("getWorkplaceEmployees", "ON_REQUEST : " + workplaceId);
        firestoreDb
                .collection("users")
                .get()
                .addOnSuccessListener(
                        snapshots -> callback.onSuccess(usersDocumentsToArray(snapshots.getDocuments())))
                .addOnFailureListener(__ -> callback.onFailure());
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
    public void getUsersByChosenRestaurant(
            String restaurantId, Callback<User[]> callback) {
        firestoreDb
                .collection("users")
                .whereEqualTo(CHOSEN_RESTAURANT_ID, restaurantId)
                .get()
                .addOnSuccessListener(
                        snapshots -> callback.onSuccess(usersDocumentsToArray(snapshots.getDocuments())))
                .addOnFailureListener(__ -> callback.onFailure());
    }

    @Override
    public void pushCurrentUserData() {
        final Map<String, Object> userData = new HashMap<>();
        userData.put(CHOSEN_RESTAURANT_ID, currentUser.getRestaurantChoice());
        /*
        userData.put(CHOSEN_RESTAURANT_NAME, currentUser.getChosenRestaurantName());
        userData.put(CONVERSATIONS, currentUser.getConversationsIds());
        userData.put(WORKPLACE, currentUser.getWorkplaceId());

         */
        userData.put(LIKED_RESTAURANTS, currentUser.getFavouriteRestaurants());
        firestoreDb.collection("users").document(currentUser.getUid()).update(userData);
    }

    @Override
    public void resetCurrentUserChosenRestaurant() {

    }

    @Override
    public void updateUserData(String dataType, Object data) {

    }
/*
    @Override
    public void resetCurrentUserChosenRestaurant() {
        currentUser.setChosenRestaurantName("");
        currentUser.setChosenRestaurantId("");
        updateUserData(CHOSEN_RESTAURANT_ID, "");
    }

 */


 /*
    @Override
    public void updateUserData(String dataType, Object data) {
        final Map<String, Object> userData = new HashMap<>();
        userData.put(dataType, data);
        if (dataType.equals(CHOSEN_RESTAURANT_ID)) {
            userData.put(CHOSEN_RESTAURANT_NAME, currentUser.getChosenRestaurantName());
        } else if (dataType.equals(CHOSEN_RESTAURANT_NAME)) {
            userData.put(CHOSEN_RESTAURANT_ID, currentUser.getChosenRestaurantId());
        }
        firestoreDb.collection("users").document(currentUser.getId()).update(userData);
    }

  */


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

//  private User[] jsonArrayToUserArray(String json) {
//    final Type jsonArrayType = new TypeToken<Map<String, String>[]>() {}.getType();
//    final Map<String, String>[] listOfUserJson = new Gson().fromJson(json, jsonArrayType);
//    final List<User> users = new ArrayList<>();
//    User currentUserInList;
//    for (Map<String, String> userJson : listOfUserJson) {
//      currentUserInList =
//          new User(
//              userJson.get("id"),
//              userJson.get("name"),
//              getDefaultPhotoIfNull(userJson.get("photoUrl")),
//              "");
//      currentUserInList.setChosenRestaurantName(userJson.get("restaurantName"));
//      currentUserInList.setChosenRestaurantId(userJson.get("restaurantId"));
//      users.add(currentUserInList);
//    }
//    return users.toArray(new User[0]);
//  }
//
//  private String getDefaultPhotoIfNull(String photoUrl) {
//    return photoUrl != null ? photoUrl : DEFAULT_USER_PHOTO_URL;
//  }

    //  private static String getFirebaseUserPotentialFirstName(String firebaseUserDisplayName){
    //    if(!firebaseUserDisplayName.contains(" ")) return firebaseUserDisplayName;
    //    final List<String> names = Arrays.asList(firebaseUserDisplayName.split(" "));
    //    names.remove(names.size() -1);
    //    return TextUtils.join(" ", names);
    //  }
//
//  private void updateWorkplaceRestaurantRelatedData() {
//    if (currentUser.getChosenRestaurantId() != null
//        && !currentUser.getChosenRestaurantId().isEmpty()) {
//      final Map<String, Object> userData = new HashMap<>();
//      userData.put("clients", Arrays.asList(currentUser.getId()));
//      final DocumentReference restaurantRef =
//          firestoreDb
//              .collection("workplaces")
//              .document(currentUser.getWorkplaceId())
//              .collection("restaurantsChosenByEmployees")
//              .document(currentUser.getChosenRestaurantId());
//
//      restaurantRef.update("clients", FieldValue.arrayRemove(currentUser.getId()));
//
//      restaurantRef
//          .set(userData, SetOptions.mergeFields("clients"))
//          .addOnFailureListener(f -> Log.i("updateUserChosenRes", "FAILED"));
//    }
//  }
//  private void updateUserWorkplaceEmployees() {
//    final Map<String, Object> userData = new HashMap<>();
//    userData.put("employees", Arrays.asList(currentUser.getId()));
//    if (currentUser.getWorkplaceId() != null && !currentUser.getWorkplaceId().isEmpty()) {
//      final DocumentReference workplaceDocRef =
//          firestoreDb.collection("workplaces").document(currentUser.getWorkplaceId());
//      workplaceDocRef
//          .get()
//          .addOnSuccessListener(
//              workplaceDoc -> {
//                if (workplaceDoc.exists() && workplaceDoc.get("employees") != null) {
//                  workplaceDocRef.update("employees", FieldValue.arrayUnion(currentUser.getId()));
//                } else {
//                  workplaceDocRef.set(userData, SetOptions.mergeFields("employees"));
//                }
//              });
//    }
//  }
}

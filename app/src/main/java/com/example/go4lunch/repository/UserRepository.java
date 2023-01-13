package com.example.go4lunch.repository;



import static android.content.ContentValues.TAG;

import static com.example.go4lunch.domain.UserProvider.CHOSEN_RESTAURANT_ID;
import static com.example.go4lunch.domain.UserProvider.CHOSEN_RESTAURANT_NAME;
import static com.example.go4lunch.domain.UserProvider.CONVERSATIONS;
import static com.example.go4lunch.domain.UserProvider.LIKED_RESTAURANTS;
import static com.example.go4lunch.domain.UserProvider.WORKPLACE;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.go4lunch.dataSource.models.RestaurantPlace;
import com.example.go4lunch.domain.Callback;
import com.example.go4lunch.domain.UserProvider;
import com.example.go4lunch.model.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserRepository {

    private static volatile UserRepository instance;
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private static final String COLLECTION_NAME = "users";
    private FirebaseFirestore database;
    private static final String FIELD_FAVOURITE_RESTAURANT = "favouriteRestaurants";
    private static final String FIELD_RESTAURANT_CHOICE = "restaurantChoice";
    private final MutableLiveData<List<User>> allUsers = new MutableLiveData<>();
    private final MutableLiveData<List<User>> allDetailsRestaurantUsers = new MutableLiveData<>();

/*
    LiveData<List<User>> userList =  new LiveData<List<User>>(new ArrayList<>()) {
        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super List<User>> observer) {
            super.observe(owner, observer);
            observer.onChanged(userList.getValue());
        }
    };

 */


    public MutableLiveData <List<User>> userList= new MutableLiveData<>(new ArrayList<>());


    public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
        }
        return instance;
    }

    @Nullable
    public FirebaseUser getCurrentUser() {

         return FirebaseAuth.getInstance().getCurrentUser();

    }

    public Task<Void> signOut(Context context){
        return AuthUI.getInstance().signOut(context);
    }

    @Nullable
    public String getCurrentUserID() {
        FirebaseUser user = getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }


    public void getDataBaseInstance() {
        database = FirebaseFirestore.getInstance();
    }

    //Get user data
    public void getUserDataFromDataBase(String userID) {
        DocumentReference docRef = database.collection(COLLECTION_NAME).document(userID);

        docRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w(TAG, "onEvent: Listen failed", error);
                return;
            }

            if (value != null && value.exists()) {
                user.setValue(value.toObject(User.class));
            } else {
                Log.i(TAG, "onEvent: data is null");
                createUser();
            }

        });
    }
    //Create user in Firestore
    public void createUser() {
        FirebaseUser currentUser = getCurrentUser();

        if (currentUser != null) {
            String userID = currentUser.getUid();
            String username = currentUser.getDisplayName();
            String userEmail = currentUser.getEmail();
            String urlPicture = (currentUser.getPhotoUrl() != null) ? currentUser.getPhotoUrl().toString() : null;

            User userToCreate = new User(userID, username, userEmail, urlPicture);

            Task<DocumentSnapshot> userData = getUserData();

            userData.addOnSuccessListener(documentSnapshot -> this.getUserCollection().document(userID).set(userToCreate));
        }


    }
    //Get the collection reference
    public CollectionReference getUserCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Get User Data from Firestore
    public Task<DocumentSnapshot> getUserData() {
        assert this.getCurrentUser() != null;
        String uid = this.getCurrentUser().getUid();
        return this.getUserCollection().document(uid).get();
    }



    public LiveData<User> observeCurrentUser() {
        return user;
    }

    public void addFavouriteRestaurant(String userID, String restaurantID) {
        System.out.println("-------------restao id--------------"+restaurantID);
        database.collection(COLLECTION_NAME).document(userID)
                .update(FIELD_FAVOURITE_RESTAURANT, FieldValue.arrayUnion(restaurantID))
                .addOnFailureListener(e -> Log.e(TAG, "addFavouriteRestaurant: failed ", e));
    }

    public void removeFavouriteRestaurant(String userID, String restaurantID) {
        database.collection(COLLECTION_NAME).document(userID)
                .update(FIELD_FAVOURITE_RESTAURANT, FieldValue.arrayRemove(restaurantID))
                .addOnFailureListener(e -> Log.e(TAG, "removeFavouriteRestaurant: failed ", e));
    }

    public void validateRestaurantChoice(String userID, String restaurantID) {
        System.out.println("-------------restao id--------------"+restaurantID);
        database.collection(COLLECTION_NAME).document(userID)
                .update(FIELD_RESTAURANT_CHOICE, restaurantID)
                .addOnFailureListener(e -> Log.e(TAG, "validateRestaurantChoice: failed ", e));
    }

    public void cancelRestaurantChoice(String userID, String restaurantID) {
        database.collection(COLLECTION_NAME).document(userID)
                .update(FIELD_RESTAURANT_CHOICE, FieldValue.delete())
                .addOnFailureListener(e -> Log.e(TAG, "cancelRestaurantChoice: failed ", e));
    }

    public LiveData<List<User>> getUserList() {
        System.out.println("----------1---------------------");

        database.collection(COLLECTION_NAME).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                System.out.println("----------2---------------------");
                if (task.isSuccessful()) {
                    System.out.println("--------task success-------------");
                    System.out.println("----------3---------------------");

                    userList.getValue().clear();

                    String urlPicture;
                    String restaurantChoice;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        System.out.println("----------4---------------------");
                        String username = document.get("username").toString();
                        System.out.println("----------userlist repo------------"+userList);
                        System.out.println("----------userame------------"+username);
                        String id = document.get("uid").toString();
                        String userEmail = document.get("userEmail").toString();
                        try {
                             urlPicture = document.get("urlPicture").toString();
                            //userList.getValue().add(new User(id, username,userEmail,urlPicture));

                        }catch (NullPointerException e){
                            //userList.getValue().add(new User(id, username,userEmail,null));
                             urlPicture = null;
                        }
                        try {
                             restaurantChoice = document.get("restaurantChoice").toString();
                            //userList.getValue().add(new User(id, username,userEmail,urlPicture));

                        }catch (NullPointerException e){
                            //userList.getValue().add(new User(id, username,userEmail,null));
                             restaurantChoice = null;
                        }
                        //userList.setValue(new User(id, username,userEmail,urlPicture, restaurantChoice));
                        userList.getValue().add(new User(id, username,userEmail,urlPicture, restaurantChoice));
                        //userList.getValue().add(new User(id, username,userEmail,urlPicture, restaurantChoice));
/*
                        System.out.println("------------userlist add------------"+userList.getValue().size());
                        int size = userList.getValue().size();
                        System.out.println("------------userlist add------------"+userList.getValue().get(size-1).getUserEmail());

 */

                        System.out.println("----------5---------------------");

                        System.out.println("-----------document id--------------"+document.get("username"));


                        System.out.println("-----------user !!!!--------------"+user);

                    }
                    Log.d(TAG, userList.toString());
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());

                }

            }

        });
        System.out.println("-----------repository userlist-------"+userList);
        return userList;


    }

    public LiveData<List<User>> getUserDetailsRestaurantList(String restaurantId) {
        LiveData<List<User>> userList =  allUsers;

        for(User user : userList.getValue()){
            if(user.getRestaurantChoice() == restaurantId){
                userList.getValue().add(user);
            }
        }
        return userList;

    }


    //Get ll users from FireStore
    public void getAllUsersFromDataBase() {
        database.collection(COLLECTION_NAME)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Listen fail ", error);
                        return;
                    }

                    if (value != null) {
                        List<User> temList = value.toObjects(User.class);
                        allUsers.setValue(temList);
                    } else {
                        Log.i(TAG, "onEvent: all user is null");
                    }

                });
    }

    //Get updated list of users
    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public void getUsersByChosenRestaurant(
            String restaurantId, String usersWorkplaceId, Callback<List<User>> callback) {
        database
                .collection("users")
                .whereEqualTo(WORKPLACE, usersWorkplaceId)
                .whereEqualTo(CHOSEN_RESTAURANT_ID, restaurantId)
                .get()
                .addOnSuccessListener(
                        snapshots -> callback.onSuccess(usersDocumentsToArray(snapshots.getDocuments())))
                .addOnFailureListener(__ -> callback.onFailure());
    }

    @SuppressWarnings("unchecked")
    private List<User> usersDocumentsToArray(List<DocumentSnapshot> usersDocuments) {
        final List<User> userList = new ArrayList<>();
        for (DocumentSnapshot userDoc : usersDocuments) {
            userList.add(
                    new User(
                            userDoc.getId(),
                            userDoc.getString("username"),
                            userDoc.getString("urlPicture")));
        }
        //return userList.toArray(new User[0]);
        return userList;
    }


}

package com.example.go4lunch.activities;



//import static com.example.go4lunch.Utils.DetailsRestaurantFieldsUtils.getRestaurantDetailsFields;

import static androidx.test.InstrumentationRegistry.getContext;

import static com.example.go4lunch.activities.MainActivity.userViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.example.go4lunch.R;
import com.example.go4lunch.Utils.DetailsRestaurantFieldsUtils;
import com.example.go4lunch.dataSource.models.Result;
import com.example.go4lunch.domain.Callback;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.UserViewModelFactory;
import com.example.go4lunch.model.User;
import com.example.go4lunch.viewModel.UserViewModel;
import com.example.go4lunch.views.DetailsAdapter;
import com.example.go4lunch.views.MyListViewRestaurantAdapter;
import com.example.go4lunch.views.WorkmatesAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsRestaurantActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    User user ;
    public Result restaurant = new Result();
    ImageButton likeButton;
    ImageButton validateRestaurantChoice;
    DetailsAdapter detailsAdapter;
    RecyclerView recyclerView;
    List<User> userList = new ArrayList<>();
    Callback<List<User>> userCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_restaurant);
        likeButton = (ImageButton) findViewById(R.id.detail_activity_like_button);
        validateRestaurantChoice = (ImageButton) findViewById(R.id.details_activity_floating_button);
        configureRecyclerView();
        configureViewModel();
        getCurrentUser();
        getSerialisable();
        configureUserViewModel();


        setupLikeButton();
        setUpValidateRestaurantChoice();
        DetailsRestaurantFieldsUtils detailsRestaurantFieldsUtils = new DetailsRestaurantFieldsUtils();

        detailsRestaurantFieldsUtils.getRestaurantDetailsFields(this,restaurant);
        initUserList();
    }



    private void setUpValidateRestaurantChoice() {
        validateRestaurantChoice.setOnClickListener(view -> {
            boolean isInList = false;

            if (user != null) {
                if (user.getRestaurantChoice() != null) {

                   String restaurantChoice = user.getRestaurantChoice();
                        if (restaurantChoice.equalsIgnoreCase(restaurant.getPlaceId())) {
                            isInList = true;

                        }

                }
            }

            if (isInList) {
                userViewModel.cancelRestaurantChoice(restaurant.getPlaceId());
            } else {
                userViewModel.validateRestaurantChoice(restaurant.getPlaceId());
            }
            //setImageChoice();

        });
    }


    private void setImageChoice() {
        try {

            if (user.getRestaurantChoice().contains(restaurant.getPlaceId())) {
                likeButton.setImageDrawable(getResources()
                        .getDrawable(R.drawable.ic_star_border_white_24dp));


            } else {

                likeButton.setImageDrawable(getResources()
                        .getDrawable(R.drawable.ic_baseline_check_circle_24));

            }

        }catch (NullPointerException e){
            System.err.println("restaurant choice null exception ");
        }

    }

    private void setupLikeButton() {
        likeButton.setOnClickListener(view -> {
            boolean isInList = false;

            if (user != null) {
                if (user.getFavouriteRestaurants() != null) {
                    for (String rID : user.getFavouriteRestaurants()) {
                        if (rID.equalsIgnoreCase(restaurant.getPlaceId())) {
                            isInList = true;
                            break;
                        }
                    }
                }
            }

            if (isInList) {
                userViewModel.removeRestaurantFromFavourite(restaurant.getPlaceId());
            } else {
                userViewModel.addRestaurantToFavourite(restaurant.getPlaceId());
            }
            setImageFavorite();

        });
    }

    private void getSerialisable() {

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();


        if (bundle != null) {
            restaurant = (Result) bundle.getSerializable("restaurantSelected");

        }

    }

    private void configureViewModel() {
        UserViewModelFactory mViewModelFactory = Injection.provideUserViewModelFactory(this);
        userViewModel= new ViewModelProvider(this, mViewModelFactory).get(UserViewModel.class);
    }

    private void setNeighbourFavorite() {
        System.out.println("-----------userid---------"+user.getUid());
        if (user.getFavouriteRestaurants()!=null) {

           // neighbourApiService.removeFavorite(neighbour);
            userViewModel.addRestaurantToFavourite(restaurant.getPlaceId());

        } else {

            userViewModel.removeRestaurantFromFavourite(restaurant.getPlaceId());


        }
        setImageFavorite();
    }



    @SuppressLint("UseCompatLoadingForDrawables")
    private void setImageFavorite() {
        System.out.println("-------------resto placeid--"+restaurant.getPlaceId());
        System.out.println("-------------getFavouriteRestaurants--"+user.getFavouriteRestaurants());

        try {
            if (user.getFavouriteRestaurants().contains(restaurant.getPlaceId())) {
                System.out.println("------------1");

                likeButton.setImageDrawable(getResources()
                        .getDrawable(R.drawable.ic_baseline_star_rate_24));




            } else {
                System.out.println("--------------------2");

                likeButton.setImageDrawable(getResources()
                        .getDrawable(R.drawable.ic_star_border_white_24dp));

            }

        }catch (NullPointerException e){
            System.err.println("favourite restaurants null exception ");
        }

    }

    public Result getRestaurant(){return restaurant;}






    /**
     * Get current user
     */
    private void getCurrentUser() {
        userViewModel.getDataBaseInstanceUser();
        userViewModel.getCurrentUserDataFromFireStore(userViewModel.getCurrentUser().getUid());
        userViewModel.observeCurrentUser().observe(this, this::updateUser);
        userViewModel.observeCurrentUser().observe(this, this::updateLIkeImageButton);
    }

    private void updateLIkeImageButton(User user) {

        try {
            if (user.getFavouriteRestaurants().contains(restaurant.getPlaceId())) {


                System.out.println("--------------------2");

                likeButton.setImageDrawable(getResources()
                        .getDrawable(R.drawable.ic_baseline_star_rate_24));

            } else {

                System.out.println("------------1");
                likeButton.setImageDrawable(getResources()
                        .getDrawable(R.drawable.ic_star_border_white_24dp));

            }

        }catch (NullPointerException e){
            System.err.println("favourite restaurants null exception ");
        }

    }

    private void updateUser(User user) {
        this.user = user;
        System.out.println("--------------userid----------"+user.getUid());
        //setupLikeButtonAppearance();
        System.out.println("-----------userid---------"+user.getUid());

    }

    private void configureRecyclerView() {
        this.userList = new ArrayList<>();

        recyclerView = findViewById(R.id.details_restaurant_activity_recycler_view);


        detailsAdapter = new DetailsAdapter(userList,this);
        recyclerView.setAdapter(detailsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    private void configureUserViewModel() {
        UserViewModelFactory userViewModelFactory = Injection.provideUserViewModelFactory(this);
        userViewModel = new ViewModelProvider(this,userViewModelFactory).get(UserViewModel.class);
    }

    private void initUserList()   {
        //userViewModel.getUserList().observe(this,this::userList);
        userViewModel.getAllUsersFromDataBase();
        userViewModel.getAllUsers().observe(this, this::userDetailsRestaurantList);
        //userViewModel.getUsersByChosenRestaurant(restaurant.getPlaceId(),"", userCallback);
        //userViewModel.getUserDetailsRestaurantList(restaurant.getPlaceId()).observe(this,this::updateUserRestaurantDétails);
        System.out.println("-----------------restaurant id---------"+restaurant.getPlaceId());
        //System.out.println("----------------callback user--------------"+userCallback.toString());

    }

    private void userDetailsRestaurantList(List<User> users) {
        userList.clear();

        for(User user : users){
            System.out.println("--------------resto choice-------"+user.getRestaurantChoice());
            System.out.println("--------------place id-------"+restaurant.getPlaceId());
            try {
                if( user.getRestaurantChoice().equalsIgnoreCase(restaurant.getPlaceId())){
                    System.out.println("--------------test-------");
                    userList.add(user);
                }
            }catch (NullPointerException e){
                System.err.println("null pointeur restaurant id ");
            }

        }

        detailsAdapter = new DetailsAdapter(userList, this);
        recyclerView.setAdapter(detailsAdapter);
        detailsAdapter.notifyDataSetChanged();

    }

    private void updateUserRestaurantDétails(List<User> users) {
        System.out.println("----------------user restaurant details------------"+users);
    }


}
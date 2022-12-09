package com.example.go4lunch.activities;



import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.go4lunch.R;
import com.example.go4lunch.dataSource.models.Result;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.UserViewModelFactory;
import com.example.go4lunch.model.User;
import com.example.go4lunch.viewModel.UserViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsRestaurantActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    User user ;
    private Result restaurant;
    ImageButton likeButton;
    ImageButton validateRestaurantChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_restaurant);
        likeButton = (ImageButton) findViewById(R.id.detail_activity_like_button);
        validateRestaurantChoice = (ImageButton) findViewById(R.id.details_activity_floating_button);
        configureViewModel();
        getCurrentUser();
        getSerialisable();


        setupLikeButton();
        setUpValidateRestaurantChoice();
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
            setImageChoice();

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

        try {
            if (user.getFavouriteRestaurants().contains(restaurant.getPlaceId())) {
                likeButton.setImageDrawable(getResources()
                        .getDrawable(R.drawable.ic_star_border_white_24dp));


            } else {

                likeButton.setImageDrawable(getResources()
                        .getDrawable(R.drawable.ic_baseline_star_rate_24));

            }

        }catch (NullPointerException e){
            System.err.println("favourite restaurants null exception ");
        }

    }






    /**
     * Get current user
     */
    private void getCurrentUser() {
        userViewModel.getDataBaseInstanceUser();
        userViewModel.getCurrentUserDataFromFireStore(userViewModel.getCurrentUser().getUid());
        userViewModel.observeCurrentUser().observe(this, this::updateUser);
    }

    private void updateUser(User user) {
        this.user = user;
        System.out.println("--------------userid----------"+user.getUid());
        //setupLikeButtonAppearance();
        System.out.println("-----------userid---------"+user.getUid());

    }

}
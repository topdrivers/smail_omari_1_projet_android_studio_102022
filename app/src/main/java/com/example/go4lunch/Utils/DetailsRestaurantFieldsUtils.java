package com.example.go4lunch.Utils;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.go4lunch.R;
import com.example.go4lunch.dataSource.models.Result;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class DetailsRestaurantFieldsUtils {


    public  void getRestaurantDetailsFields(Activity activity, Result restaurant){
         TextView restaurantName;
         TextView address;
         ImageView imageView;

        List<String> result = Arrays.asList(restaurant.getVicinity().split(","));

        restaurantName = activity.findViewById(R.id.details_restaurant_name);
        address = activity.findViewById(R.id.details_restaurant_address);
        imageView = activity.findViewById(R.id.details_activity_image);

        restaurantName.setText(restaurant.getName());
        address.setText(result.get(0));
        try {

            Picasso.get().load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=100&photo_reference=" +  restaurant.getPhotos().get(0).getPhotoReference() + "&key=AIzaSyDBrw5T0cNzqnSGQW6vA_QADtMBa1t-sR8")
                         .into(imageView);

        }catch (NullPointerException e) {
            System.err.println("Null details image view ");
        }
    }
}

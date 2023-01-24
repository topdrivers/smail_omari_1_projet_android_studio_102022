package com.example.go4lunch.views;

import android.graphics.Color;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;

import com.example.go4lunch.dataSource.models.PlaceDetails.Result;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AutoCompleteListViewAdapter extends RecyclerView.Adapter<AutoCompleteListViewAdapter.ViewHolder>{

    private final List<Result> restaurantList;
    private Location location;

    public AutoCompleteListViewAdapter(List<Result> restaurantList) {
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public AutoCompleteListViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AutoCompleteListViewAdapter.ViewHolder holder, int position) {

        Result restaurant = restaurantList.get(position);
        holder.restaurantName.setText(restaurant.getName());
        holder.restaurantAddress.setText(restaurant.getVicinity());


        location = new Location("service provider");
        location.setLatitude(48.550720);
        location.setLongitude(7.763412);

        Location restaurantLocation = new Location("restaurant");
        restaurantLocation.setLatitude(restaurant.getGeometry().getLocation().getLat());
        restaurantLocation.setLongitude(restaurant.getGeometry().getLocation().getLng());
        holder.restaurantDistance.setText(String.format("%sm", Math.round(location.distanceTo(restaurantLocation))));

        //------------------rating--------------------------
        holder.restaurantRating.setIsIndicator(true);
        holder.restaurantRating.setMax(3);
        holder.restaurantRating.setNumStars(3);
        holder.restaurantRating.setStepSize(0.1f);
        holder.restaurantRating.setScaleX(-1f);


        try {
            Boolean isOpenNow;
            isOpenNow = restaurant.getOpeningHours().getOpenNow();

            if (!isOpenNow) {
                holder.restaurantOpeningTime.setTextColor(Color.RED);
                holder.restaurantOpeningTime.setText("Closed");
            } else {
                holder.restaurantOpeningTime.setTextColor(Color.GREEN);
                holder.restaurantOpeningTime.setText("Open");
            }

            Picasso.get().load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=100&photo_reference=" +  restaurant.getPhotos().get(0).getPhotoReference() + "&key=AIzaSyDBrw5T0cNzqnSGQW6vA_QADtMBa1t-sR8")
                    .resize(100,100)
                    .into(holder.restaurantAvatar);

            holder.restaurantRating.setRating(getRating(restaurant.getRating()));

        }catch (NullPointerException e){
            System.err.println("Null image reference OR Null opening hour or null rating ");
            holder.restaurantAvatar.setImageResource(R.drawable.baseline_restaurant_menu);
        }



    }

    private float getRating(Double rating) {
        return (float) ((rating / 5) * 3);
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantName;
        public ImageView restaurantAvatar;
        TextView restaurantAddress;
        TextView restaurantType;
        TextView restaurantOpeningTime;
        TextView restaurantDistance;
        RatingBar restaurantRating;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.item_name);
            restaurantAvatar = itemView.findViewById(R.id.item_imageView);
            restaurantAddress = itemView.findViewById(R.id.item_address);
            restaurantType = itemView.findViewById(R.id.item_restaurant_type);
            restaurantOpeningTime = itemView.findViewById(R.id.item_restaurant_opening_hour);
            restaurantDistance = itemView.findViewById(R.id.item_restaurant_distance);
            restaurantRating = itemView.findViewById(R.id.item_rating_bar);

        }
    }
}

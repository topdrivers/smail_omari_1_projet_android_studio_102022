package com.example.go4lunch.views;

import android.annotation.SuppressLint;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.activities.DetailsRestaurantActivity;
import com.example.go4lunch.dataSource.models.Result;
import com.example.go4lunch.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyListViewRestaurantAdapter extends RecyclerView.Adapter<MyListViewRestaurantAdapter.ViewHolder>{

    private  List<Result> restaurantList;

    public MyListViewRestaurantAdapter(List<Result> restaurantList) {
        this.restaurantList = restaurantList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_view_item, parent, false);
        //MyListViewRestaurantAdapter.ViewHolder holder = new MyListViewRestaurantAdapter.ViewHolder(view);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyListViewRestaurantAdapter.ViewHolder holder, int position) {
        Result restaurant = restaurantList.get(position);
        holder.restaurantName.setText(restaurant.getName());
        holder.restaurantAddress.setText(restaurant.getVicinity());







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





        }catch (NullPointerException e){
            System.err.println("Null image reference OR Null opening hour");
            holder.restaurantAvatar.setImageResource(R.drawable.baseline_restaurant_menu_black_20);

        }

    }


    @Override
    public int getItemCount() {
        System.out.println("--------------siezze------------"+restaurantList.size());
        return restaurantList.size();
    }

   public static class ViewHolder extends RecyclerView.ViewHolder {

        //@BindView(R.id.item_name) public TextView restaurantName;
        TextView restaurantName;
        ImageView restaurantAvatar;
        TextView restaurantAddress;
        TextView restaurantType;
        TextView restaurantOpeningTime;


       public ViewHolder(@NonNull View itemView) {
           super(itemView);
           //ButterKnife.bind(this, itemView);
           restaurantName = itemView.findViewById(R.id.item_name);
           restaurantAvatar = itemView.findViewById(R.id.item_imageView);
           restaurantAddress = itemView.findViewById(R.id.item_address);
           restaurantType = itemView.findViewById(R.id.item_restaurant_type);
           restaurantOpeningTime = itemView.findViewById(R.id.item_restaurant_opening_hour);

       }
   }


}

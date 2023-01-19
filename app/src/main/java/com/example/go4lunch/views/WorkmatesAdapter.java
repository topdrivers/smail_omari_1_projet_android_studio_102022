package com.example.go4lunch.views;




import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.dataSource.models.Result;
import com.example.go4lunch.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;


public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.WorkmatesViewHolder> {

    List<User> users;
    Context context;
    List<Result> resultList;

    public WorkmatesAdapter(List<User> users,List<Result>resultList, Context context) {
        this.users = users;
        this.context = context;
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_workmates_item, parent, false);
        return new WorkmatesViewHolder(view);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onBindViewHolder(@NonNull WorkmatesAdapter.WorkmatesViewHolder holder, int position) {

        for(Result restaurant : resultList){
            if (restaurant.getPlaceId().equalsIgnoreCase(users.get(position).getRestaurantChoice())) {
                holder.workmateLunchStatus.setText(String.format(context.getString(R.string.workmate_is_joining_this_restaurant), users.get(position).getUsername(), restaurant.getName()));
                holder.workmateLunchStatus.setEnabled(true);
                break;
            }else {
                holder.workmateLunchStatus.setText(String.format(context.getString(R.string.workmate_has_not_selected_a_restaurant), users.get(position).getUsername()));
                holder.workmateLunchStatus.setEnabled(false);
            }
        }

        if (users.get(position).getUrlPicture()!=null){
            Picasso.get().load(users.get(position).getUrlPicture())
                    .resize(100,100)
                    .into(holder.workmateAvatar);
        }else {
            Picasso.get().load(R.drawable.image_profil)
                    .resize(100,100)
                    .into(holder.workmateAvatar);
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class WorkmatesViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout item;
        ImageView workmateAvatar;
        TextView workmateLunchStatus;

        public WorkmatesViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item_workmate);
            workmateAvatar = itemView.findViewById(R.id.item_workmate_avatar);
            workmateLunchStatus = itemView.findViewById(R.id.item_workmate_restaurant_choice);
        }

    }

}

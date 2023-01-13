package com.example.go4lunch.views;

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
import com.example.go4lunch.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder> {

    List<User> users;
    Context context;

    public DetailsAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public DetailsAdapter.DetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_workmates_item, parent, false);

        return new DetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsAdapter.DetailsViewHolder holder, int position) {

        holder.workmateLunchStatus.setText(String.format(context.getString(R.string.workmate_has_selected_a_restaurant), users.get(position).getUsername()));
        if (users.get(position).getUrlPicture()!=null){
            Picasso.get().load(users.get(position).getUrlPicture())
                    .resize(100,100)
                    .into(holder.workmateAvatar);

        }else {
            System.out.println("----------------picture----------------");
            Picasso.get().load(R.drawable.image_profil)
                    .resize(100,100)
                    .into(holder.workmateAvatar);
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class DetailsViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout item;
        ImageView workmateAvatar;
        TextView workmateLunchStatus;

        public DetailsViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.item_workmate);
            workmateAvatar = itemView.findViewById(R.id.item_workmate_avatar);
            workmateLunchStatus = itemView.findViewById(R.id.item_workmate_restaurant_choice);
        }
    }

}

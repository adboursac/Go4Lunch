package com.alexdb.go4lunch.ui.fragment;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.User;

import java.util.List;

public class RestaurantDetailsRecyclerViewAdapter extends WorkmatesRecyclerViewAdapter {

    public RestaurantDetailsRecyclerViewAdapter(List<User> workmates) {
        super(workmates);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User workmate = getWorkmates().get(position);
        setPicture(workmate.getProfilePictureUrl(), holder.getItemBinding().picture);
        String description = workmate.getName()+" "+getResources().getString(R.string.workmate_joining);
        holder.getItemBinding().description.setText(description);
    }
}
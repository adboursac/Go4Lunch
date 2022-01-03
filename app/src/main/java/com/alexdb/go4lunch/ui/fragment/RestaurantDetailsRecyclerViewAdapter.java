package com.alexdb.go4lunch.ui.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.databinding.FragmentRestaurantDetailsWorkmateItemBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class RestaurantDetailsRecyclerViewAdapter extends RecyclerView.Adapter<RestaurantDetailsRecyclerViewAdapter.ViewHolder>{

    private List<User> mWorkmates;
    private Context mContext;

    public RestaurantDetailsRecyclerViewAdapter(List<User> workmates) {
        mWorkmates = workmates;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private FragmentRestaurantDetailsWorkmateItemBinding mBinding;

        public ViewHolder(FragmentRestaurantDetailsWorkmateItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ViewHolder(FragmentRestaurantDetailsWorkmateItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        User workmate = mWorkmates.get(position);
        String description = workmate.getName()+" "+ mContext.getResources().getString(R.string.workmate_joining);
        holder.mBinding.description.setText(description);
        setPicture(workmate.getProfilePictureUrl(), holder.mBinding.picture);
    }

    @Override
    public int getItemCount() {
        return mWorkmates.size();
    }

    private void setPicture(String pictureUrl, ImageView imageView) {
        if (pictureUrl == null) {
            imageView.setImageResource(R.drawable.ic_sharp_no_photography_24);
            return;
        }
        Glide.with(mContext)
                .load(pictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .error(R.drawable.ic_sharp_no_photography_24)
                .into(imageView);
    }
}

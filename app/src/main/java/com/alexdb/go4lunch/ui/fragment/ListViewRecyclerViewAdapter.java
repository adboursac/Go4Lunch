package com.alexdb.go4lunch.ui.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.maps.MapsPlace;
import com.alexdb.go4lunch.data.service.GoogleMapsApiClient;
import com.alexdb.go4lunch.databinding.FragmentListViewItemBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ListViewRecyclerViewAdapter extends RecyclerView.Adapter<ListViewRecyclerViewAdapter.ViewHolder> {

    private final List<MapsPlace> mRestaurants;
    private Context mContext;

    public ListViewRecyclerViewAdapter(List<MapsPlace> restaurants) {
        mRestaurants = restaurants;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private FragmentListViewItemBinding mBinding;

        public ViewHolder(FragmentListViewItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ViewHolder(FragmentListViewItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MapsPlace restaurant = mRestaurants.get(position);

        holder.mBinding.name.setText(restaurant.getName());
        holder.mBinding.address.setText(restaurant.getVicinity());
        setPicture(GoogleMapsApiClient.getPictureUrl(restaurant.getFirstPhotoReference()), holder.mBinding.picture);
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }

    private void setPicture(String pictureUrl, ImageView imageView) {
        if (pictureUrl == null) {
            imageView.setImageResource(R.drawable.ic_sharp_no_photography_24);
            return;
        }
        Glide.with(mContext)
                .load(pictureUrl)
                .apply(new RequestOptions().centerCrop())
                .into(imageView);
    }
}


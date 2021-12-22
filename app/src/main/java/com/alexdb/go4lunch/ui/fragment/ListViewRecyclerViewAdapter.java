package com.alexdb.go4lunch.ui.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexdb.go4lunch.data.model.maps.RestaurantPlace;
import com.alexdb.go4lunch.databinding.FragmentListViewItemBinding;

import java.util.List;

public class ListViewRecyclerViewAdapter extends RecyclerView.Adapter<ListViewRecyclerViewAdapter.ViewHolder> {

    private final List<RestaurantPlace> mRestaurants;
    private Context mContext;

    public ListViewRecyclerViewAdapter(List<RestaurantPlace> restaurants) {
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
        RestaurantPlace restaurant = mRestaurants.get(position);

        holder.mBinding.name.setText(restaurant.getName());
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }
}


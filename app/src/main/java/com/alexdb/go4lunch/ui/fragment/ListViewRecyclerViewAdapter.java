package com.alexdb.go4lunch.ui.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.RestaurantStateItem;
import com.alexdb.go4lunch.databinding.FragmentListViewItemBinding;
import com.alexdb.go4lunch.ui.helper.RatingHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ListViewRecyclerViewAdapter extends RecyclerView.Adapter<ListViewRecyclerViewAdapter.ViewHolder> {

    private final List<RestaurantStateItem> mRestaurants;
    private Context mContext;

    public ListViewRecyclerViewAdapter(List<RestaurantStateItem> restaurants) {
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
        RestaurantStateItem restaurant = mRestaurants.get(position);

        holder.mBinding.name.setText(restaurant.getName());
        holder.mBinding.address.setText(restaurant.getAddress());
        holder.mBinding.openingStatus.setText(restaurant.getOpenStatus());
        holder.mBinding.distance.setText(distanceToString(restaurant.getDistance()));
        holder.mBinding.workmatesAmount.setText(workmatesAmountToString(restaurant.getWorkmatesAmount()));

        setPicture(restaurant.getPhotoUrl(), holder.mBinding.picture);

        RatingHelper.displayStarsScheme(
                restaurant.getRating(),
                holder.mBinding.star1,
                holder.mBinding.star2,
                holder.mBinding.star3);

        holder.itemView.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(
                        ListViewFragmentDirections.navigateToDetails().setPlaceId(restaurant.getPlaceId())
                )
        );
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
                .timeout(10000)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(R.drawable.ic_sharp_no_photography_24)
                .into(imageView);
    }

    private String distanceToString(int distance) {
        return distance > -1 ? String.format(mContext.getResources().getString(R.string.placeholder_distance), distance) : "";
    }

    private String workmatesAmountToString(int workmatesAmount) {
        return String.format(mContext.getResources().getString(R.string.placeholder_amount), workmatesAmount);
    }
}


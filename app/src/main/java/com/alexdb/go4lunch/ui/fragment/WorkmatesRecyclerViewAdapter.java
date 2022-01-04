package com.alexdb.go4lunch.ui.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.databinding.WorkmateItemBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class WorkmatesRecyclerViewAdapter extends RecyclerView.Adapter<WorkmatesRecyclerViewAdapter.ViewHolder> {

    private List<User> mWorkmates;
    private Context mContext;

    public WorkmatesRecyclerViewAdapter(List<User> workmates) {
        mWorkmates = workmates;
    }

    public List<User> getWorkmates() {
        return mWorkmates;
    }

    public Resources getResources() {
        return mContext.getResources();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private WorkmateItemBinding mBinding;

        public ViewHolder(WorkmateItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public WorkmateItemBinding getItemBinding() {
            return mBinding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ViewHolder(WorkmateItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        User workmate = mWorkmates.get(position);
        setPicture(workmate.getProfilePictureUrl(), holder.mBinding.picture);
        setDescription(workmate, holder.mBinding.description);

        if (workmate.hasValidBookingDate()) {
            holder.itemView.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(
                            WorkmatesViewFragmentDirections.navigateToDetails().setPlaceId(workmate.getBookedPlaceId())
                    )
            );
        }
    }

    @Override
    public int getItemCount() {
        return mWorkmates.size();
    }

    public void setPicture(String pictureUrl, ImageView imageView) {
        if (pictureUrl == null) {
            imageView.setImageResource(R.drawable.ic_sharp_no_photography_24);
            return;
        }
        Glide.with(mContext)
                .load(pictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .timeout(10000)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(R.drawable.ic_sharp_no_photography_24)
                .into(imageView);
    }

    private void setDescription(User workmate, TextView textView) {
        String description;
        if (workmate.hasValidBookingDate()) {
            description = workmate.getName()
                    + " " + getResources().getString(R.string.workmate_eating)
                    + " " + workmate.getBookedPlaceName();
            textView.setTypeface(null, Typeface.NORMAL);
            textView.setTextColor(getResources().getColor(R.color.black));
        } else {
            description = workmate.getName() + " " + getResources().getString(R.string.workmate_not_yet);
            textView.setTypeface(null, Typeface.ITALIC);
            textView.setTextColor(getResources().getColor(R.color.grey));
        }
        textView.setText(description);
    }
}

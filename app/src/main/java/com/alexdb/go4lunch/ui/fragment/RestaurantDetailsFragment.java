package com.alexdb.go4lunch.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.RestaurantDetailsStateItem;
import com.alexdb.go4lunch.data.viewmodel.DetailsViewModel;
import com.alexdb.go4lunch.data.viewmodel.ViewModelFactory;
import com.alexdb.go4lunch.databinding.FragmentRestaurantDetailsBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;


public class RestaurantDetailsFragment extends Fragment {

    FragmentRestaurantDetailsBinding mBinding;

    private DetailsViewModel mDetailsViewModel;
    private RecyclerView mRecyclerView;
    private RestaurantDetailsStateItem mCurrentDetails;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentRestaurantDetailsBinding.inflate(inflater, container, false);
        //initRecyclerView();
        initData();
        initCallButton();
        initWebsiteButton();
        return mBinding.getRoot();
    }

    private void initRecyclerView() {
        mRecyclerView = mBinding.recyclerview;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        //ListViewRecyclerViewAdapter mAdapter = new ListViewRecyclerViewAdapter();
        //mRecyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        mDetailsViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(DetailsViewModel.class);
        mDetailsViewModel.getRestaurantsDetailsLiveData().observe(getViewLifecycleOwner(), this::populateDetails);

        String placeId = RestaurantDetailsFragmentArgs.fromBundle(requireArguments()).getPlaceId();
        mDetailsViewModel.fetchRestaurantDetails(placeId);
    }

    private void populateDetails( RestaurantDetailsStateItem details) {
        mCurrentDetails = details;
        mBinding.name.setText(details.getName());
        mBinding.address.setText(details.getAddress());
        setPicture(details.getPhotoUrl(), mBinding.picture);
    }

    private void initCallButton() {
        mBinding.callButton.setOnClickListener(v -> {
            if (mCurrentDetails.getPhoneNumber() != null) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mCurrentDetails.getPhoneNumber()));
                startActivity(intent);
            } else {
                showSnackBar(getResources().getString(R.string.restaurant_no_phone));
            }
        });
    }

    private void initWebsiteButton() {
        mBinding.website.setOnClickListener(v -> {
            if (mCurrentDetails.getWebsite() != null) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mCurrentDetails.getWebsite())));
            } else {
                showSnackBar(getResources().getString(R.string.restaurant_no_website));
            }
        });
    }

    private void setPicture(String pictureUrl, ImageView imageView) {
        if (pictureUrl == null) {
            imageView.setImageResource(R.drawable.ic_sharp_no_photography_24);
            return;
        }
        Glide.with(this)
                .load(pictureUrl)
                .apply(new RequestOptions().centerCrop())
                .error(R.drawable.ic_sharp_no_photography_24)
                .into(imageView);
    }

    /**
     * Show Snack Bar with a message
     * @param message message to display
     */
    private void showSnackBar( String message){
        Snackbar.make(mBinding.mainLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}

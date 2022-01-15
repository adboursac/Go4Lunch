package com.alexdb.go4lunch.ui.fragment;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.PredictionStateItem;
import com.alexdb.go4lunch.data.model.RestaurantStateItem;
import com.alexdb.go4lunch.data.viewmodel.MapViewModel;
import com.alexdb.go4lunch.data.viewmodel.ViewModelFactory;
import com.alexdb.go4lunch.databinding.FragmentMapViewBinding;
import com.alexdb.go4lunch.ui.helper.ArrayAdapterSearchView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapViewFragment extends Fragment implements OnMapReadyCallback, ArrayAdapterSearchView.OnQueryTextListener {

    private FragmentMapViewBinding mBinding;
    private MapViewModel mMapViewModel;
    private ArrayAdapterSearchView mSearchView;

    public MapViewFragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMapViewBinding.inflate(inflater, container, false);
        initData();
        setHasOptionsMenu(true);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    /**
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
        initMapStyle(googleMap);
        mMapViewModel.initMap(googleMap, requireActivity());
        mMapViewModel.refreshLocation();

        //Click on target button moves camera on current location
        mBinding.floatingActionButton.setOnClickListener(view -> {
            Location currentLocation = mMapViewModel.getLocationLiveData().getValue();
            if (currentLocation != null) mMapViewModel.moveCamera(currentLocation);
        });

        //Click on markers info navigate to details activity
        googleMap.setOnInfoWindowClickListener(marker ->
                Navigation.findNavController(mBinding.getRoot()).navigate(
                        MapViewFragmentDirections.navigateToDetails()
                                .setPlaceId((String) Objects.requireNonNull(marker.getTag()))
                ));
    }

    /**
     * Init data and data behaviour of the fragment
     */
    private void initData() {
        mMapViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(MapViewModel.class);

        //New location data triggers camera move and an optional restaurants fetch
        mMapViewModel.getLocationLiveData().observe(getViewLifecycleOwner(), this::handleNewLocation);

        //New restaurants data triggers markers update and optional camera move
        mMapViewModel.getRestaurantsLiveData().observe(getViewLifecycleOwner(), restaurants -> {
            mMapViewModel.updateEveryRestaurantsMarkers(restaurants);
            //Move camera if we are displaying a search result
            if (mMapViewModel.getCurrentSearchQuery().length() > 0) {
                Location searchResultLocation = restaurants.get(0).getLocation();
                mMapViewModel.moveCamera(searchResultLocation);
            }
        });

        //New search predictions data triggers a display
        mMapViewModel.getRestaurantPredictionsLivaData().observe(getViewLifecycleOwner(), predictionList -> {
            if (mSearchView != null) {
                mSearchView.setSuggestionsList(predictionsToStrings(predictionList), true);
            }
        });
    }

    private void handleNewLocation(Location location) {
        // If we are displaying search result, we don't fetch but camera move on search result location.
        if (mMapViewModel.getCurrentSearchQuery().length() > 0) {
            List<RestaurantStateItem> currentRestaurants = mMapViewModel.getRestaurantsLiveData().getValue();
            if (currentRestaurants == null) return;
            mMapViewModel.moveCamera(currentRestaurants.get(0).getLocation());
        }
        // If we don't, we fetch restaurants and move camera on currentLocation
        else {
            mMapViewModel.fetchRestaurants(location);
            mMapViewModel.moveCamera(location);
        }
    }

    private List<String> predictionsToStrings(List<PredictionStateItem> predictions) {
        List<String> predictionsStrings = new ArrayList<>();
        for (PredictionStateItem p : predictions) {
            predictionsStrings.add(p.getMainText());
        }
        return predictionsStrings;
    }

    private void initMapStyle(GoogleMap googleMap) {
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.google_map_style));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        mSearchView = (ArrayAdapterSearchView) menu.findItem(R.id.toolbar_search).getActionView();
        configureSearchView();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 2) {
            mMapViewModel.requestRestaurantPredictions(newText);
        } else {
            mSearchView.hideSuggestions();
        }
        return false;
    }

    private void configureSearchView() {
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getString(R.string.toolbar_search_restaurants));

        //Handle suggestion click
        mSearchView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedString = mSearchView.applyItemSelection(position);
            mMapViewModel.applySearch(selectedString);
        });

        //Handle clear button
        mSearchView.getClearButton().setOnClickListener(view -> {
            if (mSearchView.getQuery().length() == 0) {
                mSearchView.setIconified(true);
            } else {
                mSearchView.setQuery("", false);
                mMapViewModel.clearSearch();
            }
        });

        //Display current search on searchView
        String currentQuery = mMapViewModel.getCurrentSearchQuery();
        if (currentQuery.length() > 0) {
            mSearchView.setIconified(false);
            mSearchView.setQuery(mMapViewModel.getCurrentSearchQuery(), true);
        }
    }
}

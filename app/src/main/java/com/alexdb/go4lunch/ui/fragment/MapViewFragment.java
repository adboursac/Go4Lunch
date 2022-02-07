package com.alexdb.go4lunch.ui.fragment;

import android.annotation.SuppressLint;
import android.app.appsearch.SearchResult;
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
import com.alexdb.go4lunch.data.model.RestaurantDetailsStateItem;
import com.alexdb.go4lunch.data.model.RestaurantStateItem;
import com.alexdb.go4lunch.data.viewmodel.MainViewModel;
import com.alexdb.go4lunch.ViewModelFactory;
import com.alexdb.go4lunch.databinding.FragmentMapViewBinding;
import com.alexdb.go4lunch.ui.helper.ArrayAdapterSearchView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapViewFragment extends Fragment implements OnMapReadyCallback, ArrayAdapterSearchView.OnQueryTextListener {

    private int mMapZoom;
    private GoogleMap mMap;
    private FragmentMapViewBinding mBinding;
    private MainViewModel mMainViewModel;
    private ArrayAdapterSearchView mSearchView;
    private Location mLastLocation;

    public MapViewFragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMapViewBinding.inflate(inflater, container, false);

        mMainViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(MainViewModel.class);
        setHasOptionsMenu(true);
        initSearchPredictionsObserver();

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
        mMap = googleMap;
        configureMapObjects();
        initObservers();
        // We manually update location data
        handleNewLocation(mMainViewModel.getLocationLiveData().getValue());
    }

    /**
     * Configure all google map related objects and listeners
     */
    public void configureMapObjects() {
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.google_map_style));

        // Click on target button moves camera on current location
        mBinding.floatingActionButton.setOnClickListener(view -> {
            if (mLastLocation != null) moveCamera(mLastLocation);
        });

        // Click on markers info navigate to details activity
        mMap.setOnInfoWindowClickListener(marker -> {
                if (marker.getTag() == null) return;
                Navigation.findNavController(mBinding.getRoot()).navigate(
                        MapViewFragmentDirections.navigateToDetails()
                                .setPlaceId((String) Objects.requireNonNull(marker.getTag())));
        });

        if (mMainViewModel.hasLocationPermission()) configureLocationRelatedObjects();

        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    /**
     * Locations related configurations that require location permission to occur
     */
    @SuppressLint("MissingPermission")
    public void configureLocationRelatedObjects() {
        if (mMap != null && mMainViewModel.hasLocationPermission()) {
            mMap.setMyLocationEnabled(true);
        }
    }

    /**
     * Moves map camera on given location
     *
     * @param location location to move the camera
     */
    public void moveCamera(Location location) {
        if (location == null || mMap == null) return;
        LatLng cord = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cord, mMapZoom));
    }

    /**
     * Create a restaurant marker.
     * Provides location name and color based on workmates booking status
     *
     * @param location  place location
     * @param placeName place name
     * @param bookedByWorkmates  if true marker will display in green, red instead.
     * @param placeId   id of the place
     */
    private void createRestaurantMarker(Location location, String placeName, boolean bookedByWorkmates, String placeId, boolean displayInfo) {
        if (location == null || mMap == null) return;
        LatLng cord = new LatLng(location.getLatitude(), location.getLongitude());
        float hue = bookedByWorkmates ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_ORANGE;
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(cord)
                .title(placeName)
                .icon(BitmapDescriptorFactory.defaultMarker(hue)));
        if (marker != null) {
            marker.setTag(placeId);
            if (displayInfo) marker.showInfoWindow();
        }
    }

    /**
     * Create all the markers of the restaurants contained in the given list
     *
     * @param restaurants list of restaurants to display as markers
     */
    public void updateEveryRestaurantsMarkers(List<RestaurantStateItem> restaurants, String displayPlaceId) {
        if (mMap == null) return;
        mMap.clear();
        for (RestaurantStateItem restaurant : restaurants) {
            createRestaurantMarker(
                    restaurant.getLocation(),
                    restaurant.getName(),
                    restaurant.getWorkmatesAmount() > 0,
                    restaurant.getPlaceId(),
                    displayPlaceId != null && restaurant.getPlaceId().contentEquals(displayPlaceId)
            );
        }
    }

    /**
     * Create all the markers in a search result context.
     * Move camera on search result marker and display its info window
     *
     * @param restaurants list of restaurants to display as markers
     */
    private void displayMarkersWithSearchResult(List<RestaurantStateItem> restaurants) {
        if (restaurants == null) return;
        updateEveryRestaurantsMarkers(restaurants, restaurants.get(0).getPlaceId());
        Location searchResultLocation = restaurants.get(0).getLocation();
        moveCamera(searchResultLocation);
    }

    /**
     * Init viewModels observers reactions
     */
    private void initObservers() {
        // New location data triggers camera move
        mMainViewModel.getLocationLiveData().observe(getViewLifecycleOwner(), this::handleNewLocation);

        // New restaurants data triggers markers update and camera moves
        mMainViewModel.getRestaurantsLiveData().observe(getViewLifecycleOwner(), this::handleNewRestaurantList);

        // Configure location related objects as soon as permission is granted
        mMainViewModel.getLocationPermissionLiveData().observe(getViewLifecycleOwner(), permitted -> {
            if (permitted) configureLocationRelatedObjects();
        });

        // Init map zoom default value
        mMainViewModel.getMapZoomLiveData().observe(getViewLifecycleOwner(), zoom -> mMapZoom = zoom);
    }

    /**
     * Init search prediction observation.
     * This observer is implemented separately from other observers and called at fragment creation.
     * Goal is to set it before SearchView creation and avoid unnecessary prediction display at fragment launch.
     */
    private void initSearchPredictionsObserver() {
        // Search view observer is initiated separately, to be set before SearchView creation
        // and
        // New search predictions data triggers a display.
        mMainViewModel.getRestaurantPredictionsLivaData().observe(getViewLifecycleOwner(), predictionList -> {
            if (mSearchView != null) {
                List<String> predictionsStrings = mMainViewModel.predictionsToStrings(predictionList);
                mSearchView.setSuggestionsList(predictionsStrings, true);
            }
        });
    }

    /**
     * Handle location change. Moves camera on the right spot
     *
     * @param location new updated location
     */
    private void handleNewLocation(Location location) {
        mLastLocation = location;
        if (location == null) return;
        // If we are displaying search result
        if (mMainViewModel.getCurrentSearchQuery().length() > 0) {
            displayMarkersWithSearchResult(mMainViewModel.getRestaurantsLiveData().getValue());
        }
        // Instead, we only move camera on currentLocation
        else moveCamera(location);
    }

    /**
     * Handle restaurant list change. Update markers and moves camera
     *
     * @param restaurants new restaurant list
     */
    private void  handleNewRestaurantList(List<RestaurantStateItem> restaurants) {
        if (restaurants == null || restaurants.size() == 0) return;
        // If we are displaying search result
        if (mMainViewModel.getCurrentSearchQuery().length() > 0) {
            displayMarkersWithSearchResult(restaurants);
        } else {
            // Update markers and move camera on last Location
            updateEveryRestaurantsMarkers(restaurants, null);
            if (mLastLocation != null) moveCamera(mLastLocation);
        }
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
            mMainViewModel.requestPlacesPredictions(newText);
        }
        return false;
    }

    /**
     * Configure search view
     */
    private void configureSearchView() {
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getString(R.string.toolbar_search_restaurants));

        //Handle suggestion click
        mSearchView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedString = mSearchView.applySelection(position);
            mMainViewModel.applySearch(selectedString);
            mSearchView.setQuery(selectedString, true);
        });

        //Handle clear button
        mSearchView.getClearButton().setOnClickListener(view -> {
            if (mSearchView.getQuery().length() == 0) {
                mSearchView.setIconified(true);
            } else {
                mSearchView.setQuery("", false);
                mMainViewModel.clearSearch();
            }
        });

        //Display current search on searchView
        String currentQuery = mMainViewModel.getCurrentSearchQuery();
        if (currentQuery.length() > 0) {
            mSearchView.setIconified(false);
            mSearchView.setQuery(mMainViewModel.getCurrentSearchQuery(), true);
        }
    }
}

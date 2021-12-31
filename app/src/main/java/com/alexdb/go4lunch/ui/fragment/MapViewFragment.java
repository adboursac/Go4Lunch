package com.alexdb.go4lunch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.viewmodel.MapViewModel;
import com.alexdb.go4lunch.data.viewmodel.ViewModelFactory;
import com.alexdb.go4lunch.databinding.FragmentMapViewBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapViewBinding mBinding;
    private MapViewModel mMapViewModel;

    public MapViewFragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMapViewBinding.inflate(inflater, container, false);
        initViewModels();
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
        //Click on target button refresh location and moves camera on it
        mBinding.floatingActionButton.setOnClickListener(view -> mMapViewModel.refreshLocation());
        //Click on markers navigate to details activity
        googleMap.setOnInfoWindowClickListener(marker ->
                Navigation.findNavController(mBinding.getRoot()).navigate(
                        MapViewFragmentDirections.navigateToDetails()
                                .setPlaceId((String) Objects.requireNonNull(marker.getTag()))
                ));
    }

    private void initViewModels() {
        mMapViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MapViewModel.class);

        mMapViewModel.getLocationLiveData().observe(getViewLifecycleOwner(), location -> {
                    mMapViewModel.fetchRestaurants(location);
                    mMapViewModel.moveCamera(location);
                }
        );

        mMapViewModel.getRestaurantsLiveData().observe(getViewLifecycleOwner(), restaurants -> mMapViewModel.addEveryRestaurantsMarkers()
        );
    }

    private void initMapStyle(GoogleMap googleMap) {
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.google_map_style));
    }
}

package com.alexdb.go4lunch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.viewmodel.SettingsViewModel;
import com.alexdb.go4lunch.data.viewmodel.ViewModelFactory;
import com.alexdb.go4lunch.databinding.FragmentSettingsBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class SettingsFragment extends Fragment {

    private SettingsViewModel mSettingsViewModel;
    FragmentSettingsBinding mBinding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentSettingsBinding.inflate(inflater, container, false);
        initData();
        initViews();
        return mBinding.getRoot();
    }

    /**
     * Init view model and data behaviour of the fragment
     */
    private void initData() {
        mSettingsViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(SettingsViewModel.class);

        mSettingsViewModel.getMapZoomLiveData().observe(getViewLifecycleOwner(), zoom -> mBinding.mapZoom.setText(String.format(getString(R.string.placeholder_integer), zoom.intValue())));

        mSettingsViewModel.getSearchRadiusLiveData().observe(getViewLifecycleOwner(), radius -> mBinding.searchRadius.setText(String.format(getString(R.string.placeholder_integer), radius.intValue())));
    }

    private void initViews() {
        //Save button
        mBinding.saveButton.setOnClickListener( view -> mSettingsViewModel.saveSettings(
                mBinding.mapZoom.getText().toString(),
                mBinding.searchRadius.getText().toString()
        ));
    }
}
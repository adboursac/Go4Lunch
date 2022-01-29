package com.alexdb.go4lunch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.viewmodel.SettingsViewModel;
import com.alexdb.go4lunch.ViewModelFactory;
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

        mSettingsViewModel.getMapZoomLiveData().observe(getViewLifecycleOwner(), zoom -> mBinding.mapZoom.setText(String.format(getString(R.string.placeholder_integer), zoom)));

        mSettingsViewModel.getSearchRadiusLiveData().observe(getViewLifecycleOwner(), radius -> mBinding.searchRadius.setText(String.format(getString(R.string.placeholder_integer), radius)));

        mSettingsViewModel.getLunchNotificationLiveData().observe(getViewLifecycleOwner(), enabled -> {
            mBinding.notificationSwitch.setChecked(enabled);
            setNotificationTimeVisibility(enabled);
        });

        mSettingsViewModel.getNotificationTimeLiveData().observe(getViewLifecycleOwner(), time -> mBinding.notificationTime.setText(time));
    }

    private void initViews() {
        //Save button
        mBinding.saveButton.setOnClickListener(view -> mSettingsViewModel.saveSettings(
                Objects.requireNonNull(mBinding.mapZoom.getText()).toString(),
                Objects.requireNonNull(mBinding.searchRadius.getText()).toString(),
                mBinding.notificationSwitch.isChecked(),
                Objects.requireNonNull(mBinding.notificationTime.getText()).toString()
        ));

        mBinding.notificationSwitch.setOnCheckedChangeListener((compoundButton, enabled) -> setNotificationTimeVisibility(enabled));
    }

    private void setNotificationTimeVisibility(boolean enabled) {
        if (enabled) {
            mBinding.notificationTime.setVisibility(View.VISIBLE);
            mBinding.timeLabel.setVisibility(View.VISIBLE);
        }
        else {
            mBinding.notificationTime.setVisibility(View.GONE);
            mBinding.timeLabel.setVisibility(View.GONE);
        }
    }
}
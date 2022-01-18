package com.alexdb.go4lunch.data.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.alexdb.go4lunch.data.repository.SettingsRepository;

public class SettingsViewModel extends ViewModel {

    @NonNull
    private final SettingsRepository mSettingsRepository;

    public SettingsViewModel(@NonNull SettingsRepository settingsRepository) {
        mSettingsRepository = settingsRepository;
    }

    public LiveData<Integer> getMapZoomLiveData() {
        return mSettingsRepository.getMapZoomLiveData();
    }

    public LiveData<Integer> getSearchRadiusLiveData() {
        return mSettingsRepository.getSearchRadiusLiveData();
    }

    public void saveSettings(String mapZoomString, String searchRadiusString) {
        Integer mapZoom;
        try {
            mapZoom = Integer.parseInt(mapZoomString);
        }
        catch (Exception e) {
            mapZoom = mSettingsRepository.getMapZoomLiveData().getValue();
        }

        Integer searchRadius;
        try {
            searchRadius = Integer.parseInt(searchRadiusString);
        }
        catch (Exception e) {
            searchRadius = mSettingsRepository.getSearchRadiusLiveData().getValue();
        }

        mSettingsRepository.saveSettings(mapZoom, searchRadius);
    }
}

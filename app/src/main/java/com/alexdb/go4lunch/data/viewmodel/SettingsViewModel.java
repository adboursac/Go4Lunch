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

    public LiveData<Boolean> getLunchNotificationLiveData() {
        return mSettingsRepository.getLunchNotificationLiveData();
    }

    public LiveData<String> getNotificationTimeLiveData() {
        return mSettingsRepository.getNotificationTimeLiveData();
    }

    public void saveSettings(String mapZoomString, String searchRadiusString, Boolean lunchNotification, String notificationTime) {
        Integer mapZoom;
        try { mapZoom = Integer.parseInt(mapZoomString); } catch (Exception e) { mapZoom = null;}
        Integer searchRadius;
        try { searchRadius = Integer.parseInt(searchRadiusString); } catch (Exception e) { searchRadius = null;}
        mSettingsRepository.saveSettings(mapZoom, searchRadius, lunchNotification, notificationTime);
    }
}

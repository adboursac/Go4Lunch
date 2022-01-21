package com.alexdb.go4lunch.data.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alexdb.go4lunch.ui.MainApplication;
import com.alexdb.go4lunch.ui.helper.LocalTimeHelper;

import java.util.Objects;
import java.util.Observable;
import java.util.function.Predicate;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;

public class SettingsRepository {

    RxDataStore<Preferences> mDataStore;

    public SettingsRepository() {
        mDataStore = new RxPreferenceDataStoreBuilder(MainApplication.getApplication(), "settings").build();
        readSettings();
    }

    /**
     * Provide map zoom preferences and liveData
     */
    Preferences.Key<Integer> KEY_MAP_ZOOM = PreferencesKeys.intKey("map_zoom");
    public static final int DEFAULT_MAP_ZOOM = 18;
    public Predicate<Integer> mapZoomPredicate = zoom -> (zoom != null) && (11 < zoom) && (zoom < 23);
    private final MutableLiveData<Integer> mMapZoomLiveData = new MutableLiveData<>();
    public LiveData<Integer> getMapZoomLiveData() { return mMapZoomLiveData; }

    public void saveMapZoom(Integer mapZoom, MutablePreferences preferences) {
        saveValue(KEY_MAP_ZOOM, DEFAULT_MAP_ZOOM, mapZoom, mMapZoomLiveData, mapZoomPredicate, preferences); }
    public void readMapZoom() { readValue(KEY_MAP_ZOOM, DEFAULT_MAP_ZOOM, mMapZoomLiveData); }

    /**
     * Provide search radius preferences and liveData
     */
    Preferences.Key<Integer> KEY_SEARCH_RADIUS = PreferencesKeys.intKey("search_radius");
    public static final int DEFAULT_SEARCH_RADIUS = 200;
    public Predicate<Integer> searchRadiusPredicate = radius -> (radius != null) && (100 < radius) && (radius < 1000);
    private final MutableLiveData<Integer> mSearchRadiusLiveData = new MutableLiveData<>();
    public LiveData<Integer> getSearchRadiusLiveData() { return mSearchRadiusLiveData; }

    public void saveSearchRadius(Integer searchRadius, MutablePreferences preferences) {
        saveValue(KEY_SEARCH_RADIUS, DEFAULT_SEARCH_RADIUS, searchRadius, mSearchRadiusLiveData, searchRadiusPredicate, preferences); }
    public void readSearchRadius() { readValue(KEY_SEARCH_RADIUS, DEFAULT_SEARCH_RADIUS, mSearchRadiusLiveData); }


    /**
     * Provide lunch notification activation preferences and liveData
     */
    Preferences.Key<Boolean> KEY_LUNCH_NOTIFICATION = PreferencesKeys.booleanKey("lunch_notification");
    public static final Boolean DEFAULT_LUNCH_NOTIFICATION = true;
    public Predicate<Boolean> lunchNotificationPredicate = Objects::nonNull;
    private final MutableLiveData<Boolean> mLunchNotificationLiveData = new MutableLiveData<>();
    public LiveData<Boolean> getLunchNotificationLiveData() { return mLunchNotificationLiveData; }

    public void saveLunchNotification(Boolean lunchNotification, MutablePreferences preferences) {
        saveValue(KEY_LUNCH_NOTIFICATION, DEFAULT_LUNCH_NOTIFICATION, lunchNotification, mLunchNotificationLiveData, lunchNotificationPredicate, preferences); }
    public void readLunchNotification() { readValue(KEY_LUNCH_NOTIFICATION, DEFAULT_LUNCH_NOTIFICATION, mLunchNotificationLiveData); }


    /**
     * Provide notification time preferences and liveData
     */
    Preferences.Key<String> KEY_NOTIFICATION_TIME = PreferencesKeys.stringKey("notification_time");
    public static final String DEFAULT_NOTIFICATION_TIME = "12:00";
    public Predicate<String> notificationTimePredicate = timeString -> LocalTimeHelper.stringToTime(timeString, null) != null;
    private final MutableLiveData<String> mNotificationTimeLiveData = new MutableLiveData<>();
    public LiveData<String> getNotificationTimeLiveData() { return mNotificationTimeLiveData; }

    public void saveNotificationTime(String notificationTime, MutablePreferences preferences) {
        saveValue(KEY_NOTIFICATION_TIME, DEFAULT_NOTIFICATION_TIME, notificationTime, mNotificationTimeLiveData, notificationTimePredicate, preferences); }
    public void readNotificationTime() { readValue(KEY_NOTIFICATION_TIME, DEFAULT_NOTIFICATION_TIME, mNotificationTimeLiveData); }

    /**
     * Save all the settings values and updates the associated liveData
     *
     * @param searchRadius map zoom value
     */
    public void saveSettings(Integer mapZoom, Integer searchRadius, Boolean lunchNotification, String notificationTime) {
        mDataStore.updateDataAsync((Preferences prefsIn) -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();

            saveMapZoom(mapZoom, mutablePreferences);
            saveSearchRadius(searchRadius, mutablePreferences);
            saveLunchNotification(lunchNotification, mutablePreferences);
            saveNotificationTime(notificationTime, mutablePreferences);

            return Single.just(mutablePreferences);
        });
    }

    /**
     * Read all the settings values and updates the associated liveData
     */
    public void readSettings() {
        readMapZoom();
        readSearchRadius();
        readLunchNotification();
        readNotificationTime();
    }

    /**
     * Save a value in the given preferences instance and updates the associated liveData
     *
     * @param key         preference key of the value
     * @param newValue    new value
     * @param liveData    liveData associated to the value
     * @param predicate   predicate that validate the safety of the value
     * @param preferences preferences where the value is updated
     * @param <T>         Type of the value
     */
    public <T> void saveValue(Preferences.Key<T> key,
                              T defaultValue,
                              T newValue,
                              MutableLiveData<T> liveData,
                              Predicate<T> predicate,
                              MutablePreferences preferences) {
        //If key hasn't been stored yet, we store its default
        if (preferences.get(key) == null) preferences.set(key, defaultValue);

        //New value should satisfy the safety predicate and be different from current value
        if (predicate.test(newValue)) {
            if (!keyEquals(preferences, key, newValue)) {
                liveData.postValue(newValue);
                preferences.set(key, newValue);
            }
        } else {
            //New value didn't satisfied the safety predicate
            //We set current value back in liveData
            liveData.postValue(preferences.get(key));
        }
    }

    /**
     * Read a value from given key and updates the associated liveData
     *
     * @param key          preference key of the value
     * @param defaultValue default value for the given key
     * @param liveData     liveData associated to the value
     * @param <T>          Type of the value
     */
    public <T> void readValue(Preferences.Key<T> key,
                              T defaultValue,
                              MutableLiveData<T> liveData) {
        Flowable<T> mapZoom = mDataStore.data()
                //We get the value associated with the key
                .map(prefs -> prefs.get(key))
                //If the key hasn't been stored yet, we get its default value
                .onErrorResumeWith(Flowable.just(defaultValue));

        Disposable disposable = mapZoom.subscribe(liveData::postValue,
                throwable -> Log.d("Settings Repository", throwable.getMessage()));
    }

    //keyEquals is only called when both key has been checked and newValue passed predicate test.
    @SuppressWarnings("NullableProblems")
    private <T> boolean keyEquals(MutablePreferences preferences, Preferences.Key<T> key, T newValue) {
        return preferences.get(key).toString().contentEquals(newValue.toString());
    }
}

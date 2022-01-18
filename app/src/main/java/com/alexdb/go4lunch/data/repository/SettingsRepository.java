package com.alexdb.go4lunch.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.function.Predicate;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class SettingsRepository {

    RxDataStore<Preferences> mDataStore;

    public SettingsRepository(Context context) {
        mDataStore = new RxPreferenceDataStoreBuilder(context, "settings").build();
        readSettings();
    }

    /**
     * Provide map zoom preferences and liveData
     */
    Preferences.Key<Integer> KEY_MAP_ZOOM = PreferencesKeys.intKey("map_zoom");
    public static final int DEFAULT_MAP_ZOOM = 18;
    public Predicate<Integer> mapZoomPredicate = zoom -> (11 < zoom && zoom < 23);
    private final MutableLiveData<Integer> mMapZoomLiveData = new MutableLiveData<>();
    public LiveData<Integer> getMapZoomLiveData() { return mMapZoomLiveData; }

    public void saveMapZoom(int mapZoom, MutablePreferences preferences) { saveValue(KEY_MAP_ZOOM, mapZoom, mMapZoomLiveData, mapZoomPredicate, preferences); }
    public void readMapZoom() { readValue(KEY_MAP_ZOOM, DEFAULT_MAP_ZOOM, mMapZoomLiveData); }

    /**
     * Provide search radius preferences and liveData
     */
    Preferences.Key<Integer> KEY_SEARCH_RADIUS = PreferencesKeys.intKey("search_radius");
    public static final int DEFAULT_SEARCH_RADIUS = 200;
    public Predicate<Integer> searchRadiusPredicate = radius -> (100 < radius && radius < 1000);
    private final MutableLiveData<Integer> mSearchRadiusLiveData = new MutableLiveData<>();
    public LiveData<Integer> getSearchRadiusLiveData() { return mSearchRadiusLiveData; }

    public void saveSearchRadius(int searchRadius, MutablePreferences preferences) { saveValue(KEY_SEARCH_RADIUS, searchRadius, mSearchRadiusLiveData, searchRadiusPredicate, preferences); }
    public void readSearchRadius() { readValue(KEY_SEARCH_RADIUS, DEFAULT_SEARCH_RADIUS, mSearchRadiusLiveData); }

    /**
     * Save all the settings values and updates the associated liveData
     *
     * @param searchRadius map zoom value
     */
    public void saveSettings(int mapZoom, int searchRadius) {
        mDataStore.updateDataAsync((Preferences prefsIn) -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();

            saveMapZoom(mapZoom, mutablePreferences);
            saveSearchRadius(searchRadius, mutablePreferences);

            return Single.just(mutablePreferences);
        });
    }

    /**
     * Read all the settings values and updates the associated liveData
     */
    public void readSettings() {
        readMapZoom();
        readSearchRadius();
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
                                             T newValue,
                                             MutableLiveData<T> liveData,
                                             Predicate<T> predicate,
                                             MutablePreferences preferences) {
        // If new value doesn't satisfy the safety predicate we take preference's stored value instead.
        T safeValue = predicate.test(newValue) ? newValue : preferences.get(key);
        // If new value is different from the stored one, we update both preferences and liveData.
        if (!newValue.toString().contentEquals(preferences.get(key).toString())) {
            liveData.postValue(safeValue);
            preferences.set(key, safeValue);
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
        mapZoom.subscribe(liveData::postValue,
                throwable -> Log.d("Settings Repository", throwable.getMessage()));
    }
}

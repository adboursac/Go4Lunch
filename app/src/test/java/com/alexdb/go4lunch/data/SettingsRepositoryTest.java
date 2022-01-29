package com.alexdb.go4lunch.data;


import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.lifecycle.LiveData;

import com.alexdb.go4lunch.data.repository.SettingsRepository;
import com.alexdb.go4lunch.ui.LiveDataTestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static com.alexdb.go4lunch.data.repository.SettingsRepository.DEFAULT_LUNCH_NOTIFICATION;
import static com.alexdb.go4lunch.data.repository.SettingsRepository.DEFAULT_MAP_ZOOM;
import static com.alexdb.go4lunch.data.repository.SettingsRepository.DEFAULT_NOTIFICATION_TIME;
import static com.alexdb.go4lunch.data.repository.SettingsRepository.DEFAULT_SEARCH_RADIUS;
import static com.alexdb.go4lunch.data.repository.SettingsRepository.KEY_LUNCH_NOTIFICATION;
import static com.alexdb.go4lunch.data.repository.SettingsRepository.KEY_MAP_ZOOM;
import static com.alexdb.go4lunch.data.repository.SettingsRepository.KEY_NOTIFICATION_TIME;
import static com.alexdb.go4lunch.data.repository.SettingsRepository.KEY_SEARCH_RADIUS;
import static org.mockito.ArgumentMatchers.any;


import org.mockito.junit.MockitoJUnitRunner;


import io.reactivex.rxjava3.core.Flowable;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SettingsRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    RxDataStore<Preferences> mDataStore;

    @Mock
    Preferences mockedPreferences;

    @Mock
    MutablePreferences mockedMutablePreferences;

    @Before
    public void setUp() {
        given(mDataStore.data()).willReturn(Flowable.just(mockedPreferences));
    }

    @Test
    public void livaData_gets_default_values_when_a_key_is_not_set__test() {
        // When a preferences key is not set.
        given(mockedPreferences.get(any())).willReturn(null);

        // When a SettingsRepository instance is created
        SettingsRepository settingsRepository = new SettingsRepository(mDataStore);

        // Assert LiveData gets default value

        // DEFAULT_MAP_ZOOM
        LiveData<Integer> mapZoomLiveData = settingsRepository.getMapZoomLiveData();
        LiveDataTestUtils.observeForTesting(mapZoomLiveData, liveData ->
                assertEquals(DEFAULT_MAP_ZOOM, liveData.getValue()));

        // DEFAULT_SEARCH_RADIUS
        LiveData<Integer> searchRadiusLiveData = settingsRepository.getSearchRadiusLiveData();
        LiveDataTestUtils.observeForTesting(searchRadiusLiveData, liveData ->
                assertEquals(DEFAULT_SEARCH_RADIUS, liveData.getValue()));

        // DEFAULT_LUNCH_NOTIFICATION
        LiveData<Boolean> lunchNotificationLiveData = settingsRepository.getLunchNotificationLiveData();
        LiveDataTestUtils.observeForTesting(lunchNotificationLiveData, liveData ->
                assertEquals(DEFAULT_LUNCH_NOTIFICATION, liveData.getValue()));

        // DEFAULT_SEARCH_RADIUS
        LiveData<String> notificationTimeLiveData = settingsRepository.getNotificationTimeLiveData();
        LiveDataTestUtils.observeForTesting(notificationTimeLiveData, liveData ->
                assertEquals(DEFAULT_NOTIFICATION_TIME, liveData.getValue()));
    }

    @Test
    public void when_saving_values_that_are_correct_are_saved__test() {
        // When default value are set.
        given(mockedMutablePreferences.get(KEY_MAP_ZOOM)).willReturn(DEFAULT_MAP_ZOOM);
        given(mockedMutablePreferences.get(KEY_SEARCH_RADIUS)).willReturn(DEFAULT_SEARCH_RADIUS);
        given(mockedMutablePreferences.get(KEY_LUNCH_NOTIFICATION)).willReturn(DEFAULT_LUNCH_NOTIFICATION);
        given(mockedMutablePreferences.get(KEY_NOTIFICATION_TIME)).willReturn(DEFAULT_NOTIFICATION_TIME);

        // When a SettingsRepository instance is created
        SettingsRepository settingsRepository = new SettingsRepository(mDataStore);

        // Calling specific save method when correct but different new value
        settingsRepository.saveMapZoom(12, mockedMutablePreferences);
        settingsRepository.saveSearchRadius(300, mockedMutablePreferences);
        settingsRepository.saveLunchNotification(false, mockedMutablePreferences);
        settingsRepository.saveNotificationTime("13:30", mockedMutablePreferences);

        // Assert LiveData gets the value

        // DEFAULT_MAP_ZOOM
        LiveData<Integer> mapZoomLiveData = settingsRepository.getMapZoomLiveData();
        LiveDataTestUtils.observeForTesting(mapZoomLiveData, liveData ->
                assertEquals((Integer) 12, liveData.getValue()));


        // DEFAULT_SEARCH_RADIUS
        LiveData<Integer> searchRadiusLiveData = settingsRepository.getSearchRadiusLiveData();
        LiveDataTestUtils.observeForTesting(searchRadiusLiveData, liveData ->
                assertEquals((Integer) 300, liveData.getValue()));

        // DEFAULT_LUNCH_NOTIFICATION
        LiveData<Boolean> lunchNotificationLiveData = settingsRepository.getLunchNotificationLiveData();
        LiveDataTestUtils.observeForTesting(lunchNotificationLiveData, liveData ->
                assertEquals(false, liveData.getValue()));

        // DEFAULT_SEARCH_RADIUS
        LiveData<String> notificationTimeLiveData = settingsRepository.getNotificationTimeLiveData();
        LiveDataTestUtils.observeForTesting(notificationTimeLiveData, liveData ->
                assertEquals("13:30", liveData.getValue()));
    }
}

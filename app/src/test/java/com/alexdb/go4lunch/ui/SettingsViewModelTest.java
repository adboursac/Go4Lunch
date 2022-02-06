package com.alexdb.go4lunch.ui;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alexdb.go4lunch.data.repository.SettingsRepository;
import com.alexdb.go4lunch.data.viewmodel.MainViewModel;
import com.alexdb.go4lunch.data.viewmodel.SettingsViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class SettingsViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private SettingsViewModel mSettingsViewModel;

    @Mock
    private SettingsRepository mSettingsRepository;

    @Before
    public void setUp() {
        mSettingsViewModel = new SettingsViewModel(mSettingsRepository);
    }

    @Test
    public void getMapZoomLiveData_test() {
        given(mSettingsRepository.getMapZoomLiveData()).willReturn(new MutableLiveData<>(SettingsRepository.DEFAULT_MAP_ZOOM));
        assertEquals(mSettingsRepository.getMapZoomLiveData(), mSettingsViewModel.getMapZoomLiveData());
    }

    @Test
    public void getSearchRadiusLiveData() {
        given(mSettingsRepository.getSearchRadiusLiveData()).willReturn(new MutableLiveData<>(SettingsRepository.DEFAULT_SEARCH_RADIUS));
        assertEquals(mSettingsRepository.getSearchRadiusLiveData(), mSettingsViewModel.getSearchRadiusLiveData());
    }

    @Test
    public void getLunchNotificationLiveData() {
        given(mSettingsRepository.getLunchNotificationLiveData()).willReturn(new MutableLiveData<>(SettingsRepository.DEFAULT_LUNCH_NOTIFICATION));
        assertEquals(mSettingsRepository.getLunchNotificationLiveData(), mSettingsViewModel.getLunchNotificationLiveData());
    }

    @Test
    public void getNotificationTimeLiveData() {
        given(mSettingsRepository.getNotificationTimeLiveData()).willReturn(new MutableLiveData<>(SettingsRepository.DEFAULT_NOTIFICATION_TIME));
        assertEquals(mSettingsRepository.getNotificationTimeLiveData(), mSettingsViewModel.getNotificationTimeLiveData());
    }

    @Test
    public void saveSettings_test() {
        // Calling specific save method when correct but different new value
        int expectedMapZoom = 12;
        int expectedSearchRadius = 300;
        boolean expectedLunchNotification = false;
        String expectedNotificationTime = "13:30";

        // When when call saveSettings
        mSettingsViewModel.saveSettings(
                "" + expectedMapZoom,
                "" + expectedSearchRadius,
                expectedLunchNotification,
                expectedNotificationTime

        );

        // verify that settingsRepository's save method is correctly invoked
        verify(mSettingsRepository).saveSettings(
                expectedMapZoom,
                expectedSearchRadius,
                expectedLunchNotification,
                expectedNotificationTime);
    }
}

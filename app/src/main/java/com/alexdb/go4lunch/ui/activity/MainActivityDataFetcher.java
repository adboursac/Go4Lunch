package com.alexdb.go4lunch.ui.activity;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle   .DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.alexdb.go4lunch.data.viewmodel.MainViewModel;

import org.jetbrains.annotations.NotNull;

import static androidx.lifecycle.Lifecycle.State.STARTED;


/**
 * Periodically fetch data according lifecycle owner behaviour ( MainActivity ).
 * This class uses lifecycle's current state query to avoid undesired fetch or callbacks.
 */
public class MainActivityDataFetcher implements DefaultLifecycleObserver {

    private static final int WORKMATES_FETCH_INTERVAL_MS = 60_000;

    private boolean mEnabled = false;
    private Lifecycle mLifecycle;
    private Handler mTaskHandler = new Handler(Looper.getMainLooper());

    private MainViewModel mMainViewModel;

    public MainActivityDataFetcher(Lifecycle lifecycle,
                                   MainViewModel mainViewModel) {
        mLifecycle = lifecycle;
        mMainViewModel = mainViewModel;
        lifecycle.addObserver(this);
    }

    public void enable() {
        mEnabled = true;
        // fetch user's data
        mMainViewModel.fetchCurrentUser();

        if (mLifecycle.getCurrentState().isAtLeast(STARTED)) {
            startDataFetching();
        }
    }

    @Override
    public void onStart(@NotNull LifecycleOwner owner) {
        if (mEnabled) {
            startDataFetching();
        }
    }

    @Override
    public void onStop(@NotNull LifecycleOwner owner) {
        stopDataFetching();
    }

    private void startDataFetching() {
        // location data
        mMainViewModel.refreshLocation();
        // workmates data
        mTaskHandler.removeCallbacks(refreshWorkmatesTask);
        mTaskHandler.postDelayed(refreshWorkmatesTask, WORKMATES_FETCH_INTERVAL_MS);
    }

    private void stopDataFetching() {
        // location data
        mMainViewModel.denyLocationPermission();
        // workmates data {
        mTaskHandler.removeCallbacks(refreshWorkmatesTask);
    }

    private Runnable refreshWorkmatesTask = new Runnable() {
        public void run() {
            mMainViewModel.fetchWorkmates();
            mTaskHandler.postDelayed(refreshWorkmatesTask, WORKMATES_FETCH_INTERVAL_MS);
        }
    };
}

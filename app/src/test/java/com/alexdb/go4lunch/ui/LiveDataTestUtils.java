package com.alexdb.go4lunch.ui;

import androidx.lifecycle.LiveData;

import static org.junit.Assert.*;

public class LiveDataTestUtils {

    public static <T> void observeForTesting(LiveData<T> liveData, OnObservedListener<T> onObservedListener) {
        boolean[] called = {false};

        liveData.observeForever(value -> called[0] = true);

        onObservedListener.onObserved(liveData);

        if (!called[0]) {
            fail("LiveData didn't emit any value");
        }
    }

    public interface OnObservedListener<T> {
        void onObserved(LiveData<T> liveData);
    }
}
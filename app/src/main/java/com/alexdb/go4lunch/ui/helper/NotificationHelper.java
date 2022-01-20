package com.alexdb.go4lunch.ui.helper;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.alexdb.go4lunch.data.repository.SettingsRepository;
import com.alexdb.go4lunch.data.repository.UserRepository;
import com.alexdb.go4lunch.data.service.UserApiFirebase;
import com.alexdb.go4lunch.data.viewmodel.ViewModelFactory;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class NotificationHelper {

    private static volatile NotificationHelper sNotificationHelper;

    private UserApiFirebase mUserApi;
    private UserRepository mUserRepository;
    private SettingsRepository mSettingsRepository;

    public static NotificationHelper getInstance() {
        if (sNotificationHelper == null) {
            synchronized (ViewModelFactory.class) {
                if (sNotificationHelper == null) {
                    sNotificationHelper = new NotificationHelper();
                }
            }
        }
        return sNotificationHelper;
    }

    public void initLunchNotifications(Activity activity) {
        Context context = activity.getApplicationContext();
        mSettingsRepository.getLunchNotificationLiveData().observe((LifecycleOwner) activity, enabled -> {
            if (enabled) {
                String timeString = mSettingsRepository.getNotificationTimeLiveData().getValue();
                if (timeString != null) requestLunchNotification(timeString, context);
            } else deleteLunchNotification(context);
        });

        mSettingsRepository.getNotificationTimeLiveData().observe((LifecycleOwner) activity, timeString -> {
            deleteLunchNotification(context);
            requestLunchNotification(timeString, context);
        });
    }

    public void requestLunchNotification(String notificationLocalTime, Context context) {
        LocalTime dueTime = LocalTimeHelper.stringToTime(notificationLocalTime, null);
        if (dueTime == null) return;

        Calendar currentDate = Calendar.getInstance();
        Calendar dueDate = Calendar.getInstance();

        dueDate.set(Calendar.HOUR_OF_DAY, dueTime.getHour());
        dueDate.set(Calendar.MINUTE, dueTime.getMinute());
        dueDate.set(Calendar.SECOND, 0);
        //If we are later than the notification, we postpone it until tomorrow
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
        }

        long initialDelay = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder
                (LunchNotification.class, 1, TimeUnit.DAYS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .addTag("LunchNotification")
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueue(request);
    }

    private void deleteLunchNotification(Context context) {
        WorkManager.getInstance(context).cancelAllWorkByTag("LunchNotification");
    }

    public UserApiFirebase getUserApi() {
        return mUserApi;
    }

    public void setUserApi(UserApiFirebase userApi) {
        mUserApi = userApi;
    }

    public UserRepository getUserRepository() {
        return mUserRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        mUserRepository = userRepository;
    }

    public void setSettingsRepository(SettingsRepository settingsRepository) {
        mSettingsRepository = settingsRepository;
    }
}

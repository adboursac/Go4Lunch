package com.alexdb.go4lunch.ui.helper;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Constraints;
import androidx.work.ListenableWorker;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetailsPage;
import com.alexdb.go4lunch.data.repository.RestaurantDetailsRepository;
import com.alexdb.go4lunch.data.repository.UserRepository;
import com.alexdb.go4lunch.data.service.GoogleMapsApiClient;
import com.alexdb.go4lunch.data.service.UserApiFirebase;
import com.alexdb.go4lunch.data.viewmodel.DetailsViewModel;
import com.alexdb.go4lunch.data.viewmodel.ViewModelFactory;
import com.alexdb.go4lunch.ui.fragment.RestaurantDetailsFragmentArgs;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationHelper {

    private static volatile NotificationHelper sNotificationHelper;

    private UserApiFirebase mUserApi;
    private UserRepository mUserRepository;

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

    public void requestLunchNotification(Context context) {
        Calendar currentDate = Calendar.getInstance();
        Calendar dueDate = Calendar.getInstance();

        dueDate.set(Calendar.HOUR_OF_DAY, 12);
        dueDate.set(Calendar.MINUTE, 0);
        dueDate.set(Calendar.SECOND, 0);

        if(dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
        }

        long initialDelay =  dueDate.getTimeInMillis() - currentDate.getTimeInMillis();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder
                (LunchNotification.class,1, TimeUnit.MILLISECONDS)
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
}

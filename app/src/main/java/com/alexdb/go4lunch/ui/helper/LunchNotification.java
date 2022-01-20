package com.alexdb.go4lunch.ui.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetails;
import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetailsPage;
import com.alexdb.go4lunch.data.repository.UserRepository;
import com.alexdb.go4lunch.data.service.GoogleMapsApiClient;
import com.alexdb.go4lunch.data.service.UserApiFirebase;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LunchNotification extends Worker {

    public static final String CHANNEL_ID = "1";
    String mMessage = "";
    Context mContext;
    UserApiFirebase mUserApiService;
    UserRepository mUserRepository;
    User mCurrentUser;


    public LunchNotification(Context context, WorkerParameters params) {
        super(context, params);
    }

    @NotNull
    @Override
    public Result doWork() {
        mContext = getApplicationContext();
        mUserApiService = NotificationHelper.getInstance().getUserApi();
        mUserRepository = NotificationHelper.getInstance().getUserRepository();
        fetchLunchNotificationData();
        return Result.success();
    }

    private void fetchLunchNotificationData() {
        // We can trust user repository for current user's booking placeID
        // Current user's booking local state is always up to date
        mCurrentUser = mUserRepository.getCurrentUserLiveData().getValue();
        if (mCurrentUser == null) return;
        if (mCurrentUser.hasValidBookingDate()) {
            fetchRestaurantData(mCurrentUser.getBookedPlaceId());
        }
    }

    public void fetchRestaurantData(String placeId) {

        GoogleMapsApiClient.getPlaceDetails(placeId).enqueue(new Callback<MapsPlaceDetailsPage>() {
            @Override
            public void onResponse(@NonNull Call<MapsPlaceDetailsPage> call, @NonNull Response<MapsPlaceDetailsPage> response) {
                if (response.isSuccessful()) {
                    MapsPlaceDetailsPage detailsPage = response.body();
                    if (detailsPage != null) {
                        generateRestaurantMessage(detailsPage.getResult());
                        fetchWorkmatesList(placeId);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MapsPlaceDetailsPage> call, @NonNull Throwable t) {
                Log.d("LunchNotification", "fetchRestaurantDetails failure" + t);
                //If we failed getting
                mMessage = getApplicationContext().getString(R.string.notification_lunch);
                mMessage += " "+mCurrentUser.getBookedPlaceName();
                fetchWorkmatesList(mCurrentUser.getBookedPlaceId());
            }

        });
    }

    public void generateRestaurantMessage(MapsPlaceDetails restaurantDetails) {
        mMessage = getApplicationContext().getString(R.string.notification_lunch);
        mMessage += " "+restaurantDetails.getName();
        mMessage += "\n"+restaurantDetails.getFormatted_address();
    }


    /**
     * Fetch all users in database and generate workmates list
     */
    public void fetchWorkmatesList(String placeId) {
        mUserApiService.getAllUsers()
                .addOnSuccessListener(task -> {
                    List<DocumentSnapshot> users = task.getDocuments();
                    generateWorkmatesList(users, mCurrentUser.getUid(), placeId);
                    buildAndSendNotification();
                })
                .addOnFailureListener(e -> {
                    Log.w("LunchNotification", "fetchWorkmates Error", e);
                    buildAndSendNotification();
                });
    }

    public void generateWorkmatesList (List<DocumentSnapshot> users, String currentUserId, String placeId) {
        int workmatesAmount = 0;
        StringBuilder workmatesList = new StringBuilder("\n");
        workmatesList.append(getApplicationContext().getString(R.string.notification_with));
        for (DocumentSnapshot u : users) {
            User user = u.toObject(User.class);
            if (user == null) break;
            if (!currentUserId.contentEquals(user.getUid()) && placeId.contentEquals(user.getBookedPlaceId())) {
                workmatesAmount++;
                workmatesList.append("\n");
                workmatesList.append(user.getName());
            }
        }
        if (workmatesAmount > 0) mMessage += workmatesList.toString();
    }

    private void buildAndSendNotification() {
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "android",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("WorkManger");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_go4lunch_logo)
                .setContentTitle(mContext.getString(R.string.app_name))
                .setContentText(mMessage)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(mMessage))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat.from(mContext).notify(1, builder.build());
    }
}

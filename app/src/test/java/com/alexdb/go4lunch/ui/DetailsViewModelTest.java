package com.alexdb.go4lunch.ui;

import android.content.res.Resources;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.alexdb.go4lunch.data.model.RestaurantDetailsStateItem;
import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.model.maps.MapsGeometry;
import com.alexdb.go4lunch.data.model.maps.MapsLocation;
import com.alexdb.go4lunch.data.model.maps.MapsOpeningHours;
import com.alexdb.go4lunch.data.model.maps.MapsPhoto;
import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetails;
import com.alexdb.go4lunch.data.repository.RestaurantDetailsRepository;
import com.alexdb.go4lunch.data.repository.UserRepository;
import com.alexdb.go4lunch.data.viewmodel.DetailsViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;

@SuppressWarnings("all")
@RunWith(MockitoJUnitRunner.class)
public class DetailsViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private DetailsViewModel mDetailsViewModel;

    @Mock
    private RestaurantDetailsRepository mRestaurantDetailsRepository;
    //
    MutableLiveData<MapsPlaceDetails> mDetailsLiveData;
    @Mock
    private UserRepository mUserRepository;
    @Mock
    private Resources mResources;

    @Before
    public void setUp() {
        // mRestaurantDetailsRepository Mock
        mDetailsLiveData = new MutableLiveData<>();
        given(mRestaurantDetailsRepository.getRestaurantDetailsLiveData()).willReturn(mDetailsLiveData);
        doAnswer(invocation -> {
            mDetailsLiveData.setValue(place_1_DetailsDummy);
            return null;
        }).when(mRestaurantDetailsRepository).fetchRestaurantDetails("placeId_1");

        // mUserRepository Mock
        given(mUserRepository.getCurrentUserLiveData())
                .willReturn(new MutableLiveData<>(currentUserDummy));
        given(mUserRepository.getWorkmatesLiveData())
                .willReturn(new MutableLiveData<>(workmatesDummy));

        mDetailsViewModel = new DetailsViewModel(mRestaurantDetailsRepository, mUserRepository, mResources);
    }

    @Test
    public void getRestaurantsDetailsLiveData_test() {
        // When we fetch dummy details of restaurant with id : placeId_1
        mRestaurantDetailsRepository.fetchRestaurantDetails("placeId_1");

        // We don't care about open status for detail view
        given(mDetailsViewModel.mapOpeningStatus(any())).willReturn("Open");

        // Assert that restaurant state item resulting from merging both user and detail repository is correct
        LiveDataTestUtils.observeForTesting(mDetailsViewModel.getRestaurantsDetailsLiveData(), liveData -> {
            RestaurantDetailsStateItem detailsState = liveData.getValue();
            // comparing dummy details values with detail state item
            assertEquals(place_1_DetailsDummy.getPlace_id(), detailsState.getPlaceId());
            assertEquals(place_1_DetailsDummy.getName(), detailsState.getName());
            assertEquals("Open", detailsState.getOpenStatus());
            assertEquals(place_1_DetailsDummy.getWebsite(), detailsState.getWebsite());
            assertEquals(place_1_DetailsDummy.getInternational_phone_number(), detailsState.getPhoneNumber());
            assertEquals(place_1_DetailsDummy.getFormatted_address(), detailsState.getAddress());
            assertEquals(place_1_DetailsDummy.getRating(), detailsState.getRating());
            // assert detailsState has the correct url conversion from google picture reference
            assertEquals(
                    mRestaurantDetailsRepository.getPictureUrl(place_1_DetailsDummy.getFirstPhotoReference()),
                    detailsState.getPhotoUrl());
            assertEquals(
                    currentUserDummy.hasBookedPlace(place_1_DetailsDummy.getPlace_id()),
                    detailsState.isBooked());
            assertEquals(
                    currentUserDummy.getLikedPlaces().contains(place_1_DetailsDummy.getPlace_id()),
                    detailsState.isLiked());
            // As dummy user[0] is Paul and the only workmate that booked our tested place
            // we assert that booked workmates list contains Paul
            assertEquals(1, detailsState.getBookedWorkmates().size());
            assertTrue(detailsState.getBookedWorkmates().contains(workmatesDummy.get(0)));
        });
    }

    ///// Dummy Data :
    // Current displayed place has id placeId_1, both booked by currentUser and Paul
    // Lisa booked another place

    User currentUserDummy = new User("currentUser_id",
            "currentUser",
            "currentUser_email",
            "currentUser_profilePicture",
            new Date(System.currentTimeMillis()),
            "placeId_1",
            "placeId_1_RestaurantName",
            Arrays.asList("placeId_1", "placeId_2")
    );

    List<User> workmatesDummy = Arrays.asList(
            new User("paul_id",
                    "Paul",
                    "email_paul",
                    "paul_ProfilePicture",
                    new Date(System.currentTimeMillis()),
                    "placeId_1",
                    "placeId_1_RestaurantName",
                    Arrays.asList("placeId_1", "placeId_2")
            ),
            new User("lisa_id",
                    "Lisa",
                    "email_lisa",
                    "lisa_ProfilePicture",
                    new Date(System.currentTimeMillis()),
                    "placeId_2",
                    "placeId_2_RestaurantName",
                    Arrays.asList("placeId_1", "placeId_2")
            )
    );

    @Mock
    MapsOpeningHours mapsOpeningHoursMock;

    MapsPlaceDetails place_1_DetailsDummy = new MapsPlaceDetails("placeId_1",
            new MapsGeometry(new MapsLocation(1D, 1D)),
            "placeId_1_RestaurantName",
            mapsOpeningHoursMock,
            "placeId_1_website",
            "placeId_1_website",
            "placeId_1_address",
            4F,
            Arrays.asList(new MapsPhoto(360, 360, null, "placeId_1_photoUrl"))
    );
}


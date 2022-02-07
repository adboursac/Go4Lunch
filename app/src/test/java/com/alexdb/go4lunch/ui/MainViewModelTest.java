package com.alexdb.go4lunch.ui;

import android.content.res.Resources;
import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.PredictionStateItem;
import com.alexdb.go4lunch.data.model.RestaurantStateItem;
import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.model.maps.MapsGeometry;
import com.alexdb.go4lunch.data.model.maps.MapsLocation;
import com.alexdb.go4lunch.data.model.maps.MapsOpeningHours;
import com.alexdb.go4lunch.data.model.maps.MapsPhoto;
import com.alexdb.go4lunch.data.model.maps.MapsPlace;
import com.alexdb.go4lunch.data.model.maps.MapsPlaceDetails;
import com.alexdb.go4lunch.data.model.maps.MapsPlacePrediction;
import com.alexdb.go4lunch.data.model.maps.MapsStructuredFormattingText;
import com.alexdb.go4lunch.data.model.maps.PlaceOpeningHoursPeriod;
import com.alexdb.go4lunch.data.model.maps.PlaceOpeningHoursPeriodDetail;
import com.alexdb.go4lunch.data.repository.LocationRepository;
import com.alexdb.go4lunch.data.repository.PlacePredictionRepository;
import com.alexdb.go4lunch.data.repository.RestaurantDetailsRepository;
import com.alexdb.go4lunch.data.repository.RestaurantPlacesRepository;
import com.alexdb.go4lunch.data.repository.SettingsRepository;
import com.alexdb.go4lunch.data.repository.UserRepository;
import com.alexdb.go4lunch.data.viewmodel.MainViewModel;
import com.alexdb.go4lunch.ui.activity.MainActivity;
import com.alexdb.go4lunch.ui.helper.MapsOpeningHoursHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("all")
@RunWith(MockitoJUnitRunner.class)
public class MainViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MainViewModel mMainViewModel;

    @Mock
    private LocationRepository mLocationRepository;
    private MutableLiveData<Location> mLocationLiveDataDummy = new MutableLiveData<>();
    @Mock
    private Location currentUserLocationMock;
    @Mock
    private Location place_1_locationMock;
    private float expectedDistance = 76;

    @Mock
    private RestaurantPlacesRepository mRestaurantPlacesRepository;
    MutableLiveData<List<MapsPlace>> mRestaraurantsLiveDataDummy = new MutableLiveData<>();

    @Mock
    private UserRepository mUserRepository;
    private MutableLiveData<List<User>> mWorkmatesLiveDataDummy = new MutableLiveData<>();

    @Mock
    private RestaurantDetailsRepository mRestaurantDetailsRepository;
    private MutableLiveData<Map<String, MapsPlaceDetails>> mDetailsLiveDataDummy = new MutableLiveData<>();

    @Mock
    private PlacePredictionRepository mPlacePredictionRepository;
    private MutableLiveData<List<MapsPlacePrediction>> mPredictionsLiveDataDummy = new MutableLiveData<>();

    @Mock
    private SettingsRepository mSettingsRepository;

    @Mock
    private Resources mResources;

    @Mock
    private MainActivity mainActivityMock;

    @Before
    public void setUp() {
        // LocationRepository Mock
        given(mLocationRepository.getLocationLiveData()).willReturn(mLocationLiveDataDummy);

        // Locations mock
        given(currentUserLocationMock.distanceTo(place_1_locationMock)).willReturn(expectedDistance);
        when(place_1_Dummy.getLocation()).thenReturn(place_1_locationMock);

        // RestaurantPlacesRepository Mock
        given(mRestaurantPlacesRepository.getRestaurantPlacesLiveData()).willReturn(mRestaraurantsLiveDataDummy);
        doAnswer(invocation -> {
            List<MapsPlace> restaurantList = new ArrayList<>();
            restaurantList.add(place_1_Dummy);
            mRestaraurantsLiveDataDummy.setValue(restaurantList);
            return null;
        }).when(mRestaurantPlacesRepository).fetchRestaurantPlaces(currentUserLocationMock);

        // UserRepository Mock
        given(mUserRepository.getWorkmatesLiveData())
                .willReturn(new MutableLiveData<>(workmatesDummy));
        given(mUserRepository.getCurrentUserLiveData())
                .willReturn(new MutableLiveData<>(currentUserDummy));

        // Resources strings mock. We use lenient() but actually avoiding this mock would generate null pointer exceptions
        lenient().when(mResources.getString(R.string.restaurant_open_until)).thenReturn("Open until %s");
        lenient().when(mResources.getString(R.string.restaurant_closing_soon)).thenReturn("Closing soon (%s)");
        lenient().when(mResources.getString(R.string.restaurant_opens_at)).thenReturn("Opens at %s");
        lenient().when(mResources.getString(R.string.restaurant_open)).thenReturn("Open");

        mMainViewModel = new MainViewModel(mLocationRepository,
                mRestaurantPlacesRepository,
                mUserRepository,
                mPlacePredictionRepository,
                mSettingsRepository,
                mResources);
    }

    @Test
    public void fetchRestaurants_test() {
        // When
        //      both current user and wormates list has been fetched (in setUp()
        //      location has been fetched
        mLocationLiveDataDummy.setValue(currentUserLocationMock);
        //      no places has yet been retrieved
        //      no details has yet been retrieved
        mDetailsLiveDataDummy = new MutableLiveData<>(new HashMap<>());

        // When we fetch restaurant places for given location : currentUserLocationMock
        mMainViewModel.fetchRestaurantPlaces(currentUserLocationMock);

        // We verify mRestaurantPlacesRepository is feching with the right location parameter
        verify(mRestaurantPlacesRepository).fetchRestaurantPlaces(currentUserLocationMock);

        // Assert that restaurant state item resulting from merging both user, places and details repositories is correct
        LiveDataTestUtils.observeForTesting(mMainViewModel.getRestaurantsLiveData(), liveData -> {

            List<RestaurantStateItem> restaurantStateItemList = liveData.getValue();
            // We should have only one state item
            assertEquals(1, restaurantStateItemList.size());

            // comparing dummy place and dummy details values with restaurant state item
            RestaurantStateItem restaurant = restaurantStateItemList.get(0);
            assertEquals(place_1_Dummy.getPlaceId(), restaurant.getPlaceId());
            assertEquals(place_1_Dummy.getName(), restaurant.getName());
            // assert we are correctly calling MapsOpeningHoursHelper.generateOpeningString
            assertEquals(MapsOpeningHoursHelper.generateOpeningString(mapsOpeningHoursDummy, new boolean[]{false}, mResources),
                    restaurant.getOpenStatus());
            // We won't check detailsState.isClosingSoon() here, as we want more extensive testing for MapsOpeningHoursHelper class
            //assertEquals(closingSoon[0]);
            assertEquals(place_1_Dummy.getVicinity(), restaurant.getAddress());
            assertEquals(place_1_Dummy.getLocation(), restaurant.getLocation());
            assertEquals(Math.round(expectedDistance), restaurant.getDistance());
            assertEquals(place_1_Dummy.getRating(), restaurant.getRating());
            assertEquals(
                    mRestaurantPlacesRepository.getPictureUrl(place_1_Dummy.getFirstPhotoReference()),
                    restaurant.getPhotoUrl());
            // As dummy user[0] Paul is the only workmate that booked our tested place
            // we assert that getWorkmatesAmount return 1
            assertEquals(1, restaurant.getWorkmatesAmount());
        });
    }

    @Test
    public void requestPlacesPredictions_test() {
        // SettingsRepository Mock
        given(mSettingsRepository.getSearchRadiusLiveData())
                .willReturn(new MutableLiveData<>(searchRadiusDummy));

        // Place Prediction Mock
        given(mPlacePredictionRepository.getRestaurantPredictionsLiveData())
                .willReturn(mPredictionsLiveDataDummy);

        // PlacePredictionRepository Mock
        doAnswer(invocation -> {
            mPredictionsLiveDataDummy.setValue(Arrays.asList(place_1_PlacePredictionDummy));
            return null;
        }).when(mPlacePredictionRepository).requestRestaurantPredictions(
                currentUserLocationMock,
                searchRadiusDummy,
                "placeId_1_uncomplete_name_text_input");

        // When
        //      location has been fetched
        mLocationLiveDataDummy.setValue(currentUserLocationMock);
        //      we call requestPlacesPredictions with placeId_1 related input text
        mMainViewModel.requestPlacesPredictions("placeId_1_uncomplete_name_text_input");

        // We ensure we did the right request
        verify(mPlacePredictionRepository).requestRestaurantPredictions(
                currentUserLocationMock,
                searchRadiusDummy,
                "placeId_1_uncomplete_name_text_input");

        // Assert the predictions state tiems generate by our method are correct
        LiveDataTestUtils.observeForTesting(mMainViewModel.getRestaurantPredictionsLivaData(), liveData -> {
            PredictionStateItem prediction = liveData.getValue().get(0);
            assertEquals(place_1_PlacePredictionDummy.getPlace_id(), prediction.getPlaceId());
            assertEquals(place_1_PlacePredictionDummy.getStructured_formatting().getMain_text(), prediction.getMainText());
            assertEquals(place_1_PlacePredictionDummy.getStructured_formatting().getSecondary_text(), prediction.getSecondaryText());
        });
    }

    @Test
    public void predictionsToStrings_test() {
        // When we call predictionsToStrings with a list that contains place_1_PlacePredictionDummy
        List<PredictionStateItem> predictionList = Arrays.asList(place_1_PredictionStateItemDummy);
        List<String> result = mMainViewModel.predictionsToStrings(predictionList);

        // Assert that String list have been correctly generated
        assertEquals(place_1_PredictionStateItemDummy.getMainText(), result.get(0));
    }

    @Test
    public void applySearch_Test() {
        // Place Prediction Mock
        given(mPlacePredictionRepository.getRestaurantPredictionsLiveData())
                .willReturn(mPredictionsLiveDataDummy);

        // PlacePredictionRepository Mock
        doAnswer(invocation -> {
            mPredictionsLiveDataDummy.setValue(Arrays.asList(place_1_PlacePredictionDummy));
            return null;
        }).when(mPlacePredictionRepository).requestRestaurantPredictions(
                currentUserLocationMock,
                searchRadiusDummy,
                "placeId_1_uncomplete_name_text_input");

        // SettingsRepository Mock
        given(mSettingsRepository.getSearchRadiusLiveData())
                .willReturn(new MutableLiveData<>(searchRadiusDummy));

        // When
        //      both current user and wormates list has been fetched (in setUp()
        //      location has been fetched
        mLocationLiveDataDummy.setValue(currentUserLocationMock);
        //      no places has yet been retrieved
        //      no details has yet been retrieved
        mDetailsLiveDataDummy = new MutableLiveData<>(new HashMap<>());

        //      predictions has been fetched with placeId_1 related input text
        mMainViewModel.requestPlacesPredictions("placeId_1_uncomplete_name_text_input");

        //      We call applySearch method with prediction result restaurant name
        String predictionRestaurantName = "placeId_1_name";
        mMainViewModel.applySearch(predictionRestaurantName);

        // We verify mRestaurantPlacesRepository does the right request
        verify(mRestaurantPlacesRepository).requestRestaurant(place_1_PlacePredictionDummy.getPlace_id());
    }


    @Test
    public void requestLocationPermission_test() {
        given(mLocationRepository.hasLocationPermission()).willReturn(false);
        mMainViewModel.requestLocationPermission(mainActivityMock);
        verify(mLocationRepository).requestLocationPermission(mainActivityMock);
    }

    @Test
    public void hasLocationPermission() {
        mMainViewModel.hasLocationPermission();
        verify(mLocationRepository).hasLocationPermission();
    }

    @Test
    public void denyLocationPermission() {
        mMainViewModel.denyLocationPermission();
        verify(mLocationRepository).denyLocationPermission();
    }

    @Test
    public void grantLocationPermission() {
        mMainViewModel.grantLocationPermission();
        verify(mLocationRepository).grantLocationPermission();
    }

    @Test
    public void getCurrentSearchQuery_test() {
        mMainViewModel.getCurrentSearchQuery();
        verify(mPlacePredictionRepository).getCurrentSearchQuery();
    }

    @Test
    public void getLocationLiveData_test() {
       assertEquals(mLocationRepository.getLocationLiveData(), mMainViewModel.getLocationLiveData());
    }

    @Test
    public void getLocationPermissionLiveData_test() {
        given(mLocationRepository.getLocationPermissionLiveData()).willReturn(new MutableLiveData<>(Boolean.TRUE));
        assertEquals(mLocationRepository.getLocationPermissionLiveData(), mMainViewModel.getLocationPermissionLiveData());
    }

    @Test
    public void getCurrentUserLiveData_test() {
        given(mUserRepository.getCurrentUserLiveData()).willReturn(new MutableLiveData<>(currentUserDummy));
        assertEquals(mUserRepository.getCurrentUserLiveData(), mMainViewModel.getCurrentUserLiveData());
    }

    @Test
    public void getMapZoomLiveData() {
        given(mSettingsRepository.getMapZoomLiveData()).willReturn(new MutableLiveData<>(mSettingsRepository.DEFAULT_MAP_ZOOM));
        assertEquals(mSettingsRepository.getMapZoomLiveData(), mMainViewModel.getMapZoomLiveData());
    }



    // ------ Dummy and Mocked Data ------
    // Scenario :
    // Current displayed place has id placeId_1
    // placeId_1 is booked by both currentUser and Paul
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

    String place_1_OpeningTimeDummy = "1100";
    String place_1_ClosingTimeDummy = "1450";
    MapsOpeningHours mapsOpeningHoursDummy = new MapsOpeningHours(
            true,
            Arrays.asList(
                    new PlaceOpeningHoursPeriod(
                            new PlaceOpeningHoursPeriodDetail(LocalDate.now().getDayOfWeek().getValue(), place_1_ClosingTimeDummy),
                            new PlaceOpeningHoursPeriodDetail(LocalDate.now().getDayOfWeek().getValue(), place_1_OpeningTimeDummy)
                    )
            )
    );

    MapsPlaceDetails place_1_DetailsDummy = new MapsPlaceDetails("placeId_1",
            new MapsGeometry(new MapsLocation(48.85390599999999, 2.371129)),
            "placeId_1_name",
            mapsOpeningHoursDummy,
            "placeId_1_website",
            "placeId_1_website",
            "placeId_1_address",
            4F,
            Arrays.asList(new MapsPhoto(360, 360, null, "placeId_1_photoUrl"))
    );

    @Spy
    MapsPlace place_1_Dummy = new MapsPlace("placeId_1",
            "placeId_1_name",
            "placeId_1_address",
            new MapsGeometry(new MapsLocation(48.85390599999999, 2.371129)),
            mapsOpeningHoursDummy,
            4F,
            Arrays.asList(new MapsPhoto(360, 360, null, "placeId_1_photoUrl"))
    );


    // ----- requestPlacesPredictions Mocks -----

    private MapsPlacePrediction place_1_PlacePredictionDummy = new MapsPlacePrediction("placeId_1",
            new MapsStructuredFormattingText("placeId_1_name",
                    "placeId_1_address"));
    private int searchRadiusDummy = 1000;


    // ----- predictionsToStrings Mocks -----

    private PredictionStateItem place_1_PredictionStateItemDummy = new PredictionStateItem("placeId_1",
            "placeId_1_name",
            "placeId_1_address");

}

package com.alexdb.go4lunch.data;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.repository.UserRepository;
import com.alexdb.go4lunch.data.service.UserApiFirebase;
import com.alexdb.go4lunch.ui.LiveDataTestUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

//@SuppressWarnings("unchecked")
@SuppressWarnings("all")
@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private UserApiFirebase mUserApiService;

    @InjectMocks
    private UserRepository mUserRepository;

    List<DocumentSnapshot> dummyRemoteUserList;

    /**
     * Sets test environment with mocks and a dummy database.
     * As a default starting case, database already contains 1 mocked DocumentSnapshot : anyUserDocumentSnapshotMock.
     * Provides two other mocked DocumentSnapshot for testing purposes :
     * currentUserDocumentSnapshotMock and expectedUserDocumentSnapshotMock.
     */
    @Before
    public void setUp() {
        dummyRemoteUserList = new ArrayList<>();
        dummyRemoteUserList.add(anyUserDocumentSnapshotMock);
        // Current User
        given(currentUserDocumentSnapshotMock.toObject(User.class)).willReturn(new User("current"));
        given(mUserApiService.getFirebaseAuthCurrentUser()).willReturn(new User("current"));
        // Expected User
        given(expectedUserDocumentSnapshotMock.toObject(User.class)).willReturn(new User("expected"));
        // Any User
        given(anyUserDocumentSnapshotMock.toObject(User.class)).willReturn(new User("any"));
    }

    /**
     * Utility test method, convert user instance into DocumentSnapshot for database simulation
     *
     * @param user instance to be converted into DocumentSnapshot
     * @return converted DocumentSnapshot
     */
    private DocumentSnapshot userToDocumentSnapshot(User user) {
        switch (user.getUid()) {
            case "current" : return currentUserDocumentSnapshotMock;
            case "expected" : return expectedUserDocumentSnapshotMock;
            default: return anyUserDocumentSnapshotMock;
        }
    }

    @Test
    public void fetchCurrentUser_test() {
        mock_UserApiService_getCurrentUser();

        // When currentUser is already in database
        dummyRemoteUserList.add(currentUserDocumentSnapshotMock);
        LiveData<User> currentUserLiveData = mUserRepository.getCurrentUserLiveData();

        // Calling fetchCurrentUser
        mUserRepository.fetchCurrentUser();
        capture_UserApiService_getCurrentUser_Success();

        // Assert current user is in our liveData
        LiveDataTestUtils.observeForTesting(currentUserLiveData, liveData ->
                assertEquals("current", liveData.getValue().getUid()));
    }

    @Test
    public void fetchWorkmates_test() {
        mock_UserApiService_getAllUsers();

        // When currentUser is already in database
        dummyRemoteUserList.add(currentUserDocumentSnapshotMock);
        LiveData<List<User>> workmates = mUserRepository.getWorkmatesLiveData();

        // Calling fetchWorkmates
        mUserRepository.fetchWorkmates();
        capture_UserApiService_getAllUsers_Success();

        // Check workmates liveData
        LiveDataTestUtils.observeForTesting(workmates, liveData -> {
            // Assert there's only one user
            assertEquals(1, liveData.getValue().size());
            // Assert it's not currentUser but anyUser
            assertEquals("any", liveData.getValue().get(0).getUid());
        });
    }

    @Test
    public void createUser_test() {
        mock_UserApiService_createUser();
        mock_UserApiService_getAllUsers();

        LiveData<List<User>> workmates = mUserRepository.getWorkmatesLiveData();

        // Calling createUser
        mUserRepository.createUser(new User("expected"));
        capture_UserApiService_createUser();

        // Calling fetchWorkmates
        mUserRepository.fetchWorkmates();
        capture_UserApiService_getAllUsers_Success();

        // Check workmates liveData
        LiveDataTestUtils.observeForTesting(workmates, liveData -> {
            // Assert there's 2 users
            assertEquals(2, liveData.getValue().size());
            // Assert it contains our expected user
            Predicate<User> containsExpectedUser = user -> user.getUid().contentEquals("expected");
            assertTrue(liveData.getValue().stream().anyMatch(containsExpectedUser));
        });
    }

    @Test
    public void addAuthenticatedUserInDatabase_test() {
        mock_UserApiService_createUser();
        mock_UserApiService_getCurrentUser();
        given(mUserApiService.getUser("current")).willReturn(getUserTaskMock);

        // When currentUser is not in database yet
        LiveData<User> currentUserLiveData = mUserRepository.getCurrentUserLiveData();

        // Calling addAuthenticatedUserInDatabase
        mUserRepository.addAuthenticatedUserInDatabase();
        capture_UserApiService_getUser_Failure();
        capture_UserApiService_createUser();

        // Calling fetchCurrentUser
        mUserRepository.fetchCurrentUser();
        capture_UserApiService_getCurrentUser_Success();

        // Assert current user is in our liveData
        LiveDataTestUtils.observeForTesting(currentUserLiveData, liveData ->
                assertEquals("current", liveData.getValue().getUid()));
    }

    @Test
    public void updateCurrentUserBooking_test() {
        mock_UserApiService_updateBookedPlace();
        mock_UserApiService_getCurrentUser();

        // When currentUser is already in database
        dummyRemoteUserList.add(currentUserDocumentSnapshotMock);
        LiveData<User> currentUserLiveData = mUserRepository.getCurrentUserLiveData();

        // Calling fetchCurrentUser
        mUserRepository.fetchCurrentUser();
        capture_UserApiService_getCurrentUser_Success();

        // Assert current user booking hasn't been updated yet
        LiveDataTestUtils.observeForTesting(currentUserLiveData, liveData -> {
            assertNotEquals("placeId", liveData.getValue().getBookedPlaceId());
            assertNotEquals("placeName", liveData.getValue().getBookedPlaceName());
        });

        // Calling updateCurrentUserBooking
        mUserRepository.updateCurrentUserBooking("placeId", "placeName");
        capture_UserApiService_updateBookedPlace();

        // Assert current user booking has benn updated
        LiveDataTestUtils.observeForTesting(currentUserLiveData, liveData -> {
            assertEquals("placeId", liveData.getValue().getBookedPlaceId());
            assertEquals("placeName", liveData.getValue().getBookedPlaceName());
        });
    }

    @Test
    public void toggleCurrentUserLikedPlace_test() {
        mock_UserApiService_for_toggleCurrentUserLikedPlace();
        mock_UserApiService_getCurrentUser();

        // When currentUser is already in database
        dummyRemoteUserList.add(currentUserDocumentSnapshotMock);
        LiveData<User> currentUserLiveData = mUserRepository.getCurrentUserLiveData();

        // Calling fetchCurrentUser
        mUserRepository.fetchCurrentUser();
        capture_UserApiService_getCurrentUser_Success();

        // Assert current user didn't liked the place yet
        LiveDataTestUtils.observeForTesting(currentUserLiveData, liveData -> {
            // Assert user's liked list doesn't contains place id
            Predicate<String> containsExpectedPlaceId = placeId -> placeId.contentEquals("placeId");
            assertFalse(liveData.getValue().getLikedPlaces().stream().anyMatch(containsExpectedPlaceId));
        });

        // Calling toggleCurrentUserLikedPlace
        mUserRepository.toggleCurrentUserLikedPlace("placeId");
        capture_UserApiService_for_toggleCurrentUserLikedPlace();

        // Assert current user like is registered
        LiveDataTestUtils.observeForTesting(currentUserLiveData, liveData -> {
            // Assert user's liked list contains place id
            Predicate<String> containsExpectedPlaceId = placeId -> placeId.contentEquals("placeId");
            assertTrue(liveData.getValue().getLikedPlaces().stream().anyMatch(containsExpectedPlaceId));
        });

        // Calling toggleCurrentUserLikedPlace again
        mUserRepository.toggleCurrentUserLikedPlace("placeId");
        capture_UserApiService_for_toggleCurrentUserLikedPlace();

        // Assert place is not liked anymore
        LiveDataTestUtils.observeForTesting(currentUserLiveData, liveData -> {
            // Assert user's liked list doesn't contains place id
            Predicate<String> containsExpectedPlaceId = placeId -> placeId.contentEquals("placeId");
            assertFalse(liveData.getValue().getLikedPlaces().stream().anyMatch(containsExpectedPlaceId));
        });
    }

    ///// Api Mocks

    public void mock_UserApiService_getAllUsers() {
        given(mUserApiService.getAllUsers()).willReturn(getAllUsersTaskMock);
        given(getAllUsersTaskMock.addOnSuccessListener(any(OnSuccessListener.class))).willReturn(getAllUsersTaskMock);
        given(getAllUsersQuerySnapshotMock.getDocuments()).willReturn(dummyRemoteUserList);
    }

    public void mock_UserApiService_getCurrentUser() {
        given(mUserApiService.getCurrentUser()).willReturn(getCurrentUserTaskMock);
        given(getCurrentUserTaskMock.addOnSuccessListener(any(OnSuccessListener.class))).willReturn(getCurrentUserTaskMock);
    }

    public void mock_UserApiService_createUser() {
        given(mUserApiService.createUser(any(User.class))).willReturn(createUserTaskMock);
    }

    public void mock_UserApiService_updateBookedPlace() {
        given(mUserApiService.updateBookedPlace(any(String.class),
                any(String.class),
                any(String.class),
                any(Date.class)))
                .willReturn(updateBookedPlaceTaskMock);
        given(updateBookedPlaceTaskMock.addOnSuccessListener(any(OnSuccessListener.class))).willReturn(updateBookedPlaceTaskMock);
    }

    public void mock_UserApiService_for_toggleCurrentUserLikedPlace() {
        given(mUserApiService.addLikedPlace(any(String.class),
                any(String.class)))
                .willReturn(toggleLikedPlaceTaskMock);
        given(mUserApiService.removeLikedPlace(any(String.class),
                any(String.class)))
                .willReturn(toggleLikedPlaceTaskMock);
        given(toggleLikedPlaceTaskMock.addOnSuccessListener(any(OnSuccessListener.class))).willReturn(toggleLikedPlaceTaskMock);
    }

    ///// Api Capture and triggers

    public void capture_UserApiService_getAllUsers_Success() {
        // mUserApiService capture OnSuccessListener and trigger .onSuccess
        final ArgumentCaptor<OnSuccessListener<QuerySnapshot>> listenerCaptor = ArgumentCaptor.forClass(OnSuccessListener.class);
        verify(mUserApiService.getAllUsers()).addOnSuccessListener(listenerCaptor.capture());
        listenerCaptor.getValue().onSuccess(getAllUsersQuerySnapshotMock);
    }

    public void capture_UserApiService_getCurrentUser_Success() {
        // getCurrentUser and trigger .onSuccess
        final ArgumentCaptor<OnSuccessListener<User>> listenerCaptor = ArgumentCaptor.forClass(OnSuccessListener.class);
        verify(mUserApiService.getCurrentUser()).addOnSuccessListener(listenerCaptor.capture());
        listenerCaptor.getValue().onSuccess(new User("current"));
    }

    public void capture_UserApiService_createUser() {
        // Capture user argument and simulate add in database
        final ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(mUserApiService).createUser(argumentCaptor.capture());
        dummyRemoteUserList.add(userToDocumentSnapshot(argumentCaptor.getValue()));
    }

    public void capture_UserApiService_getUser_Failure() {
        // mUserApiService capture OnFailureListener and trigger .onFailure
        final ArgumentCaptor<OnFailureListener> listenerCaptor = ArgumentCaptor.forClass(OnFailureListener.class);
        verify(mUserApiService.getUser("current")).addOnFailureListener(listenerCaptor.capture());
        listenerCaptor.getValue().onFailure(exceptionMock);
    }

    public void capture_UserApiService_updateBookedPlace() {
        // mUserApiService capture addOnSuccessListener and trigger .onSuccess
        final ArgumentCaptor<OnSuccessListener>listenerCaptor = ArgumentCaptor.forClass(OnSuccessListener.class);
        verify(updateBookedPlaceTaskMock).addOnSuccessListener(listenerCaptor.capture());
        listenerCaptor.getValue().onSuccess(updateBookedPlaceTaskResultMock);
    }

    public void capture_UserApiService_for_toggleCurrentUserLikedPlace() {
        // mUserApiService capture OnSuccessListener and trigger .onSuccess
        final ArgumentCaptor<OnSuccessListener>listenerCaptor = ArgumentCaptor.forClass(OnSuccessListener.class);
        verify(toggleLikedPlaceTaskMock, atLeast(1)).addOnSuccessListener(listenerCaptor.capture());
        listenerCaptor.getValue().onSuccess(toggleLikedPlaceTaskResultMock);
    }

    ///// DocumentSnapshot Mocks
    @Mock
    private DocumentSnapshot currentUserDocumentSnapshotMock;
    @Mock
    private DocumentSnapshot expectedUserDocumentSnapshotMock;
    @Mock
    private DocumentSnapshot anyUserDocumentSnapshotMock;

    ///// mock_UserApiService_getCurrentUser()
    // IN
    @Mock
    private Task<User> getCurrentUserTaskMock;
    // OUT

    ///// mock_UserApiService_getAllUsers()
    // IN
    @Mock
    private Task<QuerySnapshot> getAllUsersTaskMock;
    // OUT
    @Mock
    private QuerySnapshot getAllUsersQuerySnapshotMock;

    ///// mock_UserApiService_createUser()
    // IN
    @Mock
    private Task<Void> createUserTaskMock;
    // OUT

    ///// mock_UserApiService_getUser()
    // IN
    @Mock
    private Task<User> getUserTaskMock;
    // OUT
    @Mock
    private Exception exceptionMock;

    ///// mock_UserApiService_updateBookedPlace()
    // IN
    @Mock
    private Task<Void> updateBookedPlaceTaskMock;
    // OUT
    @Mock
    private Void updateBookedPlaceTaskResultMock;

    ///// mock_UserApiService_addLikedPlace()
    ///// mock_UserApiService_removeLikedPlace()
    // IN
    @Mock
    private Task<Void> toggleLikedPlaceTaskMock;
    // OUT
    @Mock
    private Void toggleLikedPlaceTaskResultMock;
}

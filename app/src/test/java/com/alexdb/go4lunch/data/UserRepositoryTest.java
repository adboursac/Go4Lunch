package com.alexdb.go4lunch.data;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.repository.UserRepository;
import com.alexdb.go4lunch.data.service.UserApiFirebase;
import com.alexdb.go4lunch.ui.LiveDataTestUtils;
import com.google.android.gms.tasks.Continuation;
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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
     * Sets test environment with mocks and a dummy database
     * As a default starting case, database already contains one mocked user : anyUser
     * Provides two other mocked users for testing purposes : currentUser and expectedUser
     */
    @Before
    public void setUp() {
        dummyRemoteUserList = new ArrayList<>();
        dummyRemoteUserList.add(anyUserDocumentSnapshotMock);
        // Current User
        given(mUserApiService.getFirebaseAuthCurrentUser()).willReturn(currentUserMock);
        given(currentUserDocumentSnapshotMock.toObject(User.class)).willReturn(currentUserMock);
        given(currentUserMock.getUid()).willReturn("current");
        // Expected User
        given(expectedUserDocumentSnapshotMock.toObject(User.class)).willReturn(expectedUserMock);
        given(expectedUserMock.getUid()).willReturn("expected");
        // Any User
        given(anyUserDocumentSnapshotMock.toObject(User.class)).willReturn(anyUserMock);
        given(anyUserMock.getUid()).willReturn("any");
    }

    /**
     * Util test method that convert a mocked user into DocumentSnapshot for database simulation
     */
    private DocumentSnapshot convertToDocumentSnapshot(User mockedUser) {
        switch (mockedUser.getUid()) {
            case "current" : return currentUserDocumentSnapshotMock;
            case "expected" : return expectedUserDocumentSnapshotMock;
            default: return anyUserDocumentSnapshotMock;
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fetchCurrentUser_returns_currentUser_when_alreadyInDatabase_test() throws Exception {
        // Mocks
        given(mUserApiService.getCurrentUser()).willReturn(getCurrentUserTaskMock);
        given(getCurrentUserTaskMock.continueWith(any(Continuation.class))).willReturn(getCurrentUserTaskMock);
        given(getCurrentUserTaskMock.getResult()).willReturn(currentUserMock);

        // When currentUser is already in database
        dummyRemoteUserList.add(currentUserDocumentSnapshotMock);
        LiveData<User> currentUserLiveData = mUserRepository.getCurrentUserLiveData();

        // Calling fetchCurrentUser
        mUserRepository.fetchCurrentUser();

        // Capture continuation and trigger .then method
        final ArgumentCaptor<Continuation<User, ?>> continuationCaptor = ArgumentCaptor.forClass(Continuation.class);
        verify(mUserApiService.getCurrentUser()).continueWith(continuationCaptor.capture());
        continuationCaptor.getValue().then(getCurrentUserTaskMock);

        // Assert current user is in our liveData
        LiveDataTestUtils.observeForTesting(currentUserLiveData, liveData ->
                assertEquals(currentUserMock, liveData.getValue()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fetchWorkmates_test() throws Exception {
        // Mock
        given(mUserApiService.getAllUsers()).willReturn(getAllUsersTaskMock);
        given(getAllUsersTaskMock.continueWith(any(Continuation.class))).willReturn(getAllUsersTaskMock);
        given(getAllUsersTaskMock.getResult()).willReturn(getAllUsersQuerySnapshotMock);
        given(getAllUsersQuerySnapshotMock.getDocuments()).willReturn(dummyRemoteUserList);

        // When currentUser is already in database
        dummyRemoteUserList.add(currentUserDocumentSnapshotMock);
        LiveData<List<User>> workmates = mUserRepository.getWorkmatesLiveData();

        // Calling fetchWorkmates
        mUserRepository.fetchWorkmates();

        // Capture continuation and trigger .then method
        final ArgumentCaptor<Continuation<QuerySnapshot, ?>> continuationCaptor = ArgumentCaptor.forClass(Continuation.class);
        verify(mUserApiService.getAllUsers()).continueWith(continuationCaptor.capture());
        continuationCaptor.getValue().then(getAllUsersTaskMock);

        // Check workmates liveData
        LiveDataTestUtils.observeForTesting(workmates, liveData -> {
            // Assert there's only one user
            assertEquals(1, liveData.getValue().size());
            // Assert it's not currentUser but anyUser
            assertEquals("any", liveData.getValue().get(0).getUid());
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createUser_test() throws Exception {
        // Mock mUserApiService.fetchWorkmates() method
        given(mUserApiService.getAllUsers()).willReturn(getAllUsersTaskMock);
        given(getAllUsersTaskMock.continueWith(any(Continuation.class))).willReturn(getAllUsersTaskMock);
        given(getAllUsersTaskMock.getResult()).willReturn(getAllUsersQuerySnapshotMock);
        given(getAllUsersQuerySnapshotMock.getDocuments()).willReturn(dummyRemoteUserList);

        LiveData<List<User>> workmates = mUserRepository.getWorkmatesLiveData();

        // Create user in database
        mUserRepository.createUser(expectedUserMock);

        // Capture user argument and simulate add in database
        final ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(mUserApiService).createUser(argumentCaptor.capture());
        dummyRemoteUserList.add(convertToDocumentSnapshot(argumentCaptor.getValue()));

        // Calling fetchWorkmates
        mUserRepository.fetchWorkmates();

        // Capture continuation and trigger .then method
        final ArgumentCaptor<Continuation<QuerySnapshot, ?>> continuationCaptor = ArgumentCaptor.forClass(Continuation.class);
        verify(mUserApiService.getAllUsers()).continueWith(continuationCaptor.capture());
        continuationCaptor.getValue().then(getAllUsersTaskMock);

        // Check workmates liveData
        LiveDataTestUtils.observeForTesting(workmates, liveData -> {
            // Assert there's 2 users
            assertEquals(2, liveData.getValue().size());
            // Assert it contains our expected user
            assertTrue(liveData.getValue().contains(expectedUserMock));
        });
    }

    ///// Global Mock
    @Mock
    private DocumentSnapshot currentUserDocumentSnapshotMock;
    @Mock
    private DocumentSnapshot expectedUserDocumentSnapshotMock;
    @Mock
    private DocumentSnapshot anyUserDocumentSnapshotMock;
    @Mock
    private User currentUserMock;
    @Mock
    private User expectedUserMock;
    @Mock
    private User anyUserMock;

    ///// fetchCurrentUser_returns_currentUser_when_alreadyInDatabase_test Mock :
    // IN
    @Mock
    private Task<User> getCurrentUserTaskMock;
    // OUT

    ///// fetchWorkmates_test Mock :
    // IN
    @Mock
    private Task<QuerySnapshot> getAllUsersTaskMock;
    // OUT
    @Mock
    private QuerySnapshot getAllUsersQuerySnapshotMock;
}

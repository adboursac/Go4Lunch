package com.alexdb.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.repository.UserRepository;
import com.alexdb.go4lunch.data.service.UserApiFirebase;
import com.alexdb.go4lunch.data.viewmodel.UserViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private UserViewModel mUserViewModel;

    @Mock
    private UserRepository mUserRepository;

    @Before
    public void setUp() {
        mUserViewModel = new UserViewModel(mUserRepository);
    }

    @Test
    public void getWorkmatesLiveData_test() {
        given(mUserRepository.getWorkmatesLiveData()).willReturn(workmatesLiveDataMock);
        assertEquals(mUserRepository.getWorkmatesLiveData(), mUserViewModel.getWorkmatesLiveData());
    }

    @Test
    public void fetchWorkmates_test() {
        mUserViewModel.fetchWorkmates();
        verify(mUserRepository).fetchWorkmates();
    }

    @Test
    public void addAuthenticatedUserInDatabase_test() {
        mUserViewModel.addAuthenticatedUserInDatabase();
        verify(mUserRepository).addAuthenticatedUserInDatabase();
    }

    @Test
    public void isCurrentUserLogged_test() {
        mUserViewModel.isCurrentUserLogged();
        verify(mUserRepository).isCurrentUserLogged();
    }

    @Mock
    private LiveData<List<User>> workmatesLiveDataMock;
}

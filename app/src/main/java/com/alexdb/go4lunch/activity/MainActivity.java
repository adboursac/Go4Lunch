package com.alexdb.go4lunch.activity;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.alexdb.go4lunch.databinding.ActivityMainBinding;
import com.alexdb.go4lunch.viewmodel.UserViewModel;
import com.alexdb.go4lunch.viewmodel.ViewModelFactory;

public class MainActivity extends BaseActivity<ActivityMainBinding>{

    private UserViewModel mUserViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(UserViewModel.class);
        setupListeners();
    }

    @Override
    ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    private void setupListeners(){
        // Logout Button
        mBinding.logout.setOnClickListener(
                view -> mUserViewModel.signOut(this).addOnSuccessListener(aVoid -> finish())
        );
    }
}

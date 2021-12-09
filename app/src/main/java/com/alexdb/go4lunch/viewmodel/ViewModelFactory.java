package com.alexdb.go4lunch.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.alexdb.go4lunch.repository.UserRepository;

import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;
    @NonNull
    private final UserRepository mUserRepository;

    private ViewModelFactory() {
        mUserRepository = new UserRepository();
    }

    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory();
                }
            }
        }
        return factory;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(mUserRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
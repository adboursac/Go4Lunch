package com.alexdb.go4lunch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.alexdb.go4lunch.databinding.FragmentSettingsBinding;

import org.jetbrains.annotations.NotNull;


public class SettingsFragment extends Fragment {

    FragmentSettingsBinding mBinding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentSettingsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }
}
package com.alexdb.go4lunch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import com.alexdb.go4lunch.databinding.FragmentMapViewBinding;

import org.jetbrains.annotations.NotNull;

public class MapViewFragment extends Fragment {

    FragmentMapViewBinding mBinding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMapViewBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }
}

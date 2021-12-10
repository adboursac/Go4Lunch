package com.alexdb.go4lunch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import com.alexdb.go4lunch.databinding.FragmentWorkmatesViewBinding;

import org.jetbrains.annotations.NotNull;

public class WorkmatesViewFragment extends Fragment {

    FragmentWorkmatesViewBinding mBinding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentWorkmatesViewBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }
}

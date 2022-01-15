package com.alexdb.go4lunch.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.User;
import com.alexdb.go4lunch.data.viewmodel.UserViewModel;
import com.alexdb.go4lunch.data.viewmodel.ViewModelFactory;
import com.alexdb.go4lunch.databinding.FragmentWorkmatesViewBinding;
import com.alexdb.go4lunch.ui.helper.ArrayAdapterSearchView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorkmatesViewFragment extends Fragment implements ArrayAdapterSearchView.OnQueryTextListener {

    FragmentWorkmatesViewBinding mBinding;
    private SearchView mSearchView;

    private RecyclerView mRecyclerView;
    private WorkmatesRecyclerViewAdapter mAdapter;
    private List<User> mWorkmates = new ArrayList<>();
    private UserViewModel mUserViewModel;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentWorkmatesViewBinding.inflate(inflater, container, false);
        initRecyclerView();
        initData();
        setHasOptionsMenu(true);
        return mBinding.getRoot();
    }

    private void initRecyclerView() {
        mRecyclerView = mBinding.recyclerview;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new WorkmatesRecyclerViewAdapter(mWorkmates);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        mUserViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(UserViewModel.class);
        mUserViewModel.getWorkmatesLiveData().observe(getViewLifecycleOwner(), workmates -> {
            mWorkmates.clear();
            mWorkmates.addAll(workmates);
            if (mSearchView != null) mAdapter.getFilter().filter(mSearchView.getQuery());
            if (mWorkmates.size() == 0) {
                Log.d("WorkmatesViewFragment", "Workmates list is empty");
            }
            mAdapter.notifyDataSetChanged();
        });

        if (mUserViewModel.getWorkmatesLiveData().getValue() == null) mUserViewModel.fetchWorkmates();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        mSearchView = (ArrayAdapterSearchView) menu.findItem(R.id.toolbar_search).getActionView();
        initData();
        configureSearchView();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
        return false;
    }

    private void configureSearchView() {
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getString(R.string.toolbar_search_workmates));
    }
}

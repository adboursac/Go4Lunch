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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorkmatesViewFragment extends Fragment implements SearchView.OnQueryTextListener {

    FragmentWorkmatesViewBinding mBinding;
    private SearchView mSearchView;

    private RecyclerView mRecyclerView;
    private List<User> mWorkmates = new ArrayList<>();
    private UserViewModel mUserViewModel;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentWorkmatesViewBinding.inflate(inflater, container, false);
        initRecyclerView(mBinding.getRoot());
        initData();
        setHasOptionsMenu(true);
        return mBinding.getRoot();
    }

    private void initRecyclerView(View root) {
        mRecyclerView = mBinding.recyclerview;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        WorkmatesRecyclerViewAdapter mAdapter = new WorkmatesRecyclerViewAdapter(mWorkmates);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        mUserViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(UserViewModel.class);
        mUserViewModel.getWorkmatesLiveData().observe(getViewLifecycleOwner(), workmates -> {
            mWorkmates.clear();
            mWorkmates.addAll(workmates);
            if (mWorkmates.size() == 0) {
                Log.d("WorkmatesViewFragment", "Workmates list is empty");
            }
            Objects.requireNonNull(mRecyclerView.getAdapter()).notifyDataSetChanged();
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        mSearchView = (SearchView) menu.findItem(R.id.toolbar_search).getActionView();
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getString(R.string.toolbar_search_workmates));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchView.onActionViewCollapsed();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}

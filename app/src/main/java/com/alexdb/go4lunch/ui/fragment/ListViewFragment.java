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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.RestaurantStateItem;
import com.alexdb.go4lunch.data.model.maps.MapsPlace;
import com.alexdb.go4lunch.data.viewmodel.ListViewModel;
import com.alexdb.go4lunch.data.viewmodel.ViewModelFactory;
import com.alexdb.go4lunch.databinding.FragmentListViewBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListViewFragment extends Fragment implements SearchView.OnQueryTextListener {

    private List<RestaurantStateItem> mRestaurants = new ArrayList<>();
    private ListViewModel mListViewModel;

    FragmentListViewBinding mBinding;
    private SearchView mSearchView;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentListViewBinding.inflate(inflater, container, false);
        initRecyclerView();
        initData();
        mListViewModel.fetchRestaurants();
        setHasOptionsMenu(true);
        return mBinding.getRoot();
    }

    private void initRecyclerView() {
        mRecyclerView = mBinding.recyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        ListViewRecyclerViewAdapter mAdapter = new ListViewRecyclerViewAdapter(mRestaurants);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        mListViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ListViewModel.class);
        mListViewModel.getRestaurantsLiveData().observe(getViewLifecycleOwner(), restaurants -> {
            mRestaurants.clear();
            mRestaurants.addAll(restaurants);
            if (mRestaurants.size() == 0) {
                Log.d("ListVewFragment", "restaurant list is empty");
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
        mSearchView.setQueryHint(getString(R.string.toolbar_search_restaurants));
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

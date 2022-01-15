package com.alexdb.go4lunch.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexdb.go4lunch.R;
import com.alexdb.go4lunch.data.model.PredictionStateItem;
import com.alexdb.go4lunch.data.model.RestaurantStateItem;
import com.alexdb.go4lunch.data.viewmodel.ListViewModel;
import com.alexdb.go4lunch.data.viewmodel.ViewModelFactory;
import com.alexdb.go4lunch.databinding.FragmentListViewBinding;
import com.alexdb.go4lunch.ui.helper.ArrayAdapterSearchView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListViewFragment extends Fragment implements ArrayAdapterSearchView.OnQueryTextListener {

    private List<RestaurantStateItem> mRestaurants = new ArrayList<>();
    private ListViewModel mListViewModel;

    FragmentListViewBinding mBinding;
    private ArrayAdapterSearchView mSearchView;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentListViewBinding.inflate(inflater, container, false);
        initRecyclerView();
        initData();
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
        mListViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(ListViewModel.class);
        mListViewModel.getRestaurantsLiveData().observe(getViewLifecycleOwner(), restaurants -> {
            mRestaurants.clear();
            mRestaurants.addAll(restaurants);
            if (mRestaurants.size() == 0) {
                Log.d("ListVewFragment", "restaurant list is empty");
            }
            Objects.requireNonNull(mRecyclerView.getAdapter()).notifyDataSetChanged();
        });
        
        mListViewModel.getRestaurantPredictionsLivaData().observe(getViewLifecycleOwner(), predictionList -> {
            if (mSearchView != null) mSearchView.setSuggestionsList(predictionsToStrings(predictionList), true);
        });
    }

    private List<String> predictionsToStrings(List<PredictionStateItem> predictions) {
        List<String> predictionsStrings = new ArrayList<>();
        for (PredictionStateItem p : predictions) {
            predictionsStrings.add(p.getMainText());
        }
        return predictionsStrings;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        mSearchView = (ArrayAdapterSearchView) menu.findItem(R.id.toolbar_search).getActionView();
        configureSearchView();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 2) {
            mListViewModel.requestRestaurantPredictions(newText);
        }
        return false;
    }

    private void configureSearchView() {
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getString(R.string.toolbar_search_restaurants));

        //Handle suggestion click
        mSearchView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedString = mSearchView.applyItemSelection(position);
            mListViewModel.applySearch(selectedString);
            mSearchView.setQuery(selectedString, true);
        });

        //Handle clear button
        mSearchView.getClearButton().setOnClickListener(view -> {
            if(mSearchView.getQuery().length() == 0) {
                mSearchView.setIconified(true);
            } else {
                mSearchView.setQuery("", false);
                mListViewModel.clearSearch();
            }
        });

        //Display current search on searchView
        String currentQuery = mListViewModel.getCurrentSearchQuery();
        if ( currentQuery.length() > 0) {
            mSearchView.setIconified(false);
            mSearchView.setQuery(mListViewModel.getCurrentSearchQuery(), true);
        }
    }
}

package com.alexdb.go4lunch.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexdb.go4lunch.data.model.maps.MapsPlace;
import com.alexdb.go4lunch.data.viewmodel.ListViewModel;
import com.alexdb.go4lunch.data.viewmodel.ViewModelFactory;
import com.alexdb.go4lunch.databinding.FragmentListViewBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListViewFragment extends Fragment {

    private List<MapsPlace> mRestaurants = new ArrayList<>();
    private ListViewModel mListViewModel;

    FragmentListViewBinding mBinding;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentListViewBinding.inflate(inflater, container, false);
        initRecyclerView(mBinding.getRoot());
        initData();
        mListViewModel.fetchRestaurants();
        return mBinding.getRoot();
    }

    private void initRecyclerView(View root) {
        mRecyclerView = mBinding.recyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        ListViewRecyclerViewAdapter mAdapter = new ListViewRecyclerViewAdapter(mRestaurants);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        mListViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ListViewModel.class);
        mListViewModel.getRestaurantsLiveData().observe(getViewLifecycleOwner(), restaurant -> {
            mRestaurants.clear();
            mRestaurants.addAll(restaurant);
            if (mRestaurants.size() == 0) {
                Log.d("ListVewFragment", "restaurant list is empty");
            }
            Objects.requireNonNull(mRecyclerView.getAdapter()).notifyDataSetChanged();
        });
    }
}

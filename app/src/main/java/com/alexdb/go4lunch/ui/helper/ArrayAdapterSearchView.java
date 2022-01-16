package com.alexdb.go4lunch.ui.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;

import com.alexdb.go4lunch.R;

import java.util.List;

/**
 * Customized SearchView that implements suggestion feature with an ArrayAdapter
 */
public class ArrayAdapterSearchView extends SearchView {

    private SearchView.SearchAutoComplete mSearchAutoComplete;
    private ArrayAdapter<String> mAdapter;
    private ImageView mClearButton;

    public ArrayAdapterSearchView(Context context) {
        super(context);
        init();
    }

    public ArrayAdapterSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageView getClearButton() {
        return mClearButton;
    }

    public void init() {
        mSearchAutoComplete = findViewById(androidx.appcompat.R.id.search_src_text);
        mClearButton = findViewById(androidx.appcompat.R.id.search_close_btn);
        mAdapter = new ArrayAdapter<>(getContext(), R.layout.search_autocomplete_item);

        mSearchAutoComplete.setAdapter(mAdapter);
        setSubmitButtonEnabled(false);
    }

    @Override
    public void setSuggestionsAdapter(CursorAdapter adapter) {
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mSearchAutoComplete.setOnItemClickListener(listener);
    }

    public void setAdapter(ArrayAdapter<?> adapter) {
        mSearchAutoComplete.setAdapter(adapter);
    }

    /**
     * Set the list of suggested strings to display when autocomplete triggers
     *
     * @param suggestions list of suggested strings to display
     * @param displayNow if set to true, it will display immediately the suggestions list
     */
    public void setSuggestionsList(List<String> suggestions, boolean displayNow) {
        mAdapter.clear();
        mAdapter.addAll(suggestions);
        if (displayNow && suggestions.size() > 0) displaySuggestions();
    }

    /**
     * Apply the selected suggestion to the search view
     *
     * @param position position of selected suggestion
     * @return suggestion string that have ben selected
     */
    public String applySelection(int position) {
        String selectedString = mAdapter.getItem(position);
        setQuery(selectedString, true);
        return selectedString;
    }

    /**
     * Display autocomplete suggestions list
     */
    public void displaySuggestions() {
        mSearchAutoComplete.callOnClick();
    }

    /**
     * Hide autocomplete suggestions list
     */
    public void hideSuggestions() {
        mSearchAutoComplete.dismissDropDown();
    }
}